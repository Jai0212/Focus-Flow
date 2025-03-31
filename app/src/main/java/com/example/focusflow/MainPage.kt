package com.example.focusflow

import android.accessibilityservice.AccessibilityService
import android.content.ComponentName
import android.content.ContentValues.TAG
import android.content.Context
import android.content.Intent
import android.graphics.Typeface
import android.os.Bundle
import android.provider.Settings
import android.text.SpannableString
import android.text.Spanned
import android.text.TextUtils
import android.text.style.StyleSpan
import android.util.Log
import android.view.View
import android.view.accessibility.AccessibilityManager
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.Recycler
import com.chaquo.python.PyObject
import com.chaquo.python.Python
import java.io.File

class MainPage : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main_page)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val databaseManager = DatabaseManager.getInstance()

//        databaseManager.addAllPromptsFromFile(this)
//        databaseManager.getAllPrompts { prompts ->
//            prompts.forEach { prompt ->
//                Log.d("prompt: ", prompt)
//            }
//        }

// In your Activity or Fragment:
        val py = Python.getInstance()
        val pyScript = py.getModule("my_script")  // Python file (without the .py extension)
        val result: PyObject = pyScript.callAttr("greet", "John")  // Calling the greet function

        val message = result.toString()  // Convert result to string
        println(message)  // Output: Hello, John!


        if (!isAccessibilityServiceEnabled(this, YourAccessibilityService::class.java)) {
            Log.d("MainPage Accessibility", "Accessibility Service NOT Enabled")
            promptEnableAccessibilityService(this)
        }
        else {
            Log.d("MainPage Accessibility", "Accessibility Service Enabled")
        }

        databaseManager.getActiveApps { activeApps ->
            val rvBlockedAppsRecyclerView: RecyclerView = findViewById(R.id.rvBlockedAppsRecyclerView)
            rvBlockedAppsRecyclerView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
            rvBlockedAppsRecyclerView.adapter = BlockedAppsRecyclerViewAdapter(this, activeApps)
        }

        val imgMainPageAddApp: ImageView = findViewById(R.id.imgMainPageAddApp)
        imgMainPageAddApp.setOnClickListener {
//            databaseManager.addApp(App(true, R.drawable.instagram, "Instagram"))
            val intent = Intent(this, EditPage::class.java)
            intent.putExtra("from", "add")
            startActivity(intent)
        }

        val bMPedit: Button = findViewById(R.id.bMPedit)
        bMPedit.setOnClickListener {
            val intent = Intent(this, EditPage::class.java)
            intent.putExtra("from", "edit")
            startActivity(intent)
        }

        val imgMainPageLogout: ImageView = findViewById(R.id.imgMainPageLogout)
        imgMainPageLogout.setOnClickListener {
            databaseManager.logOut { isSuccess ->
                if (isSuccess) {
                    Toast.makeText(this, "Logged Out", Toast.LENGTH_SHORT).show()

                    val sharedPreferences = getSharedPreferences("curr_user_session", Context.MODE_PRIVATE)
                    val editor = sharedPreferences.edit()
                    editor.clear() // Remove all saved data
                    editor.apply()

                    val intent = Intent(this, LogIn::class.java)
                    startActivity(intent)
                }
                else {
                    Toast.makeText(this, "Error Logging Out", Toast.LENGTH_SHORT).show()
                }
            }
        }

        val tvMainPageTimeSaved: TextView = findViewById(R.id.tvMainPageTimeSaved)
        val tvMainPageOpeningsPrevented: TextView = findViewById(R.id.tvMainPageOpeningsPrevented)

        val user = databaseManager.getCurrUser()
        val timeSaved: String = user?.timeSaved.toString() + " hours"
        val openingsPrevented: String = user?.openingsPrevented.toString() + " "

        // Create the spannable string for Time Saved
        val timeSavedText = "Time Saved: $timeSaved"
        val spannableTimeSaved = SpannableString(timeSavedText)
        spannableTimeSaved.setSpan(
            StyleSpan(Typeface.BOLD),
            12, // Start index for timeSaved value
            12 + timeSaved.length, // End index for timeSaved value
            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        tvMainPageTimeSaved.text = spannableTimeSaved

        // Create the spannable string for Openings Prevented
        val openingsPreventedText = "Openings Prevented: $openingsPrevented"
        val spannableOpeningsPrevented = SpannableString(openingsPreventedText)
        spannableOpeningsPrevented.setSpan(
            StyleSpan(Typeface.BOLD),
            19, // Start index for openingsPrevented value
            19 + openingsPrevented.length, // End index for openingsPrevented value
            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        tvMainPageOpeningsPrevented.text = spannableOpeningsPrevented

        val tvHours: TextView = findViewById(R.id.tvHours)
        val etHours: EditText = findViewById(R.id.etHours)

        // When the user clicks on the "0 hours" TextView
        tvHours.setOnClickListener {
            // Hide the TextView and show the EditText
            tvHours.visibility = View.GONE
            etHours.visibility = View.VISIBLE

            // Set the EditText's text to the current value
            etHours.setText(tvHours.text)

            // Focus and show the keyboard
            etHours.requestFocus()
            val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.showSoftInput(etHours, InputMethodManager.SHOW_IMPLICIT)
        }

        etHours.setOnEditorActionListener { v, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_DONE || actionId == EditorInfo.IME_ACTION_NEXT) {
                // Hide the EditText and show the updated TextView
                val newGoal = etHours.text.toString()
                tvHours.text = "$newGoal hours"  // Set the new value
                tvHours.visibility = View.VISIBLE
                etHours.visibility = View.GONE

                // Hide the keyboard
                val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.hideSoftInputFromWindow(v.windowToken, 0)
                true
            } else {
                false
            }
        }
    }

    fun isAccessibilityServiceEnabled(context: Context, service: Class<out AccessibilityService>): Boolean {

        val am = context.getSystemService(Context.ACCESSIBILITY_SERVICE) as AccessibilityManager
        val enabledServices = Settings.Secure.getString(
            context.contentResolver,
            Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES
        )

        if (enabledServices.isNullOrEmpty()) {
            Log.d("MainPage Accessibility", "No Services Enabled")
            return false
        }

        val componentNames = enabledServices.split(":").toList()

        Log.d("MainPage Accessibility Components", componentNames.toString())

        val serviceName = "com.example.focusflow/" + service.name

        for (componentName in componentNames) {
            if (componentName.equals(serviceName, ignoreCase = true)) {
                return true
            }
        }

        return false
    }

    fun promptEnableAccessibilityService(context: Context) {
        val intent = Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS)
        context.startActivity(intent)
    }
}
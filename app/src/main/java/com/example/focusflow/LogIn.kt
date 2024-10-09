package com.example.focusflow

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth

class LogIn : AppCompatActivity() {

    private lateinit var edtEmailLogIn: EditText
    private lateinit var edtPasswordLogIn: EditText
    private lateinit var btnLogIn: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_log_in)

        edtEmailLogIn = findViewById(R.id.edtEmailLogIn)
        edtPasswordLogIn = findViewById(R.id.edtPasswordLogIn)
        btnLogIn = findViewById(R.id.btnLogIn)
        val btnSwitchSignUp: Button = findViewById(R.id.btnSwitchSignUp)

        btnLogIn.setOnClickListener {
            logIn()
        }

        btnSwitchSignUp.setOnClickListener {
            val intent = Intent(this, SignUp::class.java)
            startActivity(intent)
        }
    }

    private fun logIn() {
        val email = edtEmailLogIn.text.toString().trim()
        val password = edtPasswordLogIn.text.toString().trim()

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show()
            return
        }

        val databaseManager = DatabaseManager.getInstance()

        databaseManager.logIn(User(email, password, "", mutableListOf())) { success ->
            if (success) {
                Toast.makeText(this, "Log-In successful!", Toast.LENGTH_SHORT).show()
                val intent = Intent(this, MainPage::class.java)
                startActivity(intent)
            } else {
                Toast.makeText(this, "Log-In failed. Wrong credentials.", Toast.LENGTH_SHORT).show()
            }
        }
    }
}

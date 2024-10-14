package com.example.focusflow

import android.content.Intent
import android.os.Bundle
import android.provider.ContactsContract.Data
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth

class SignUp : AppCompatActivity() {

    private lateinit var edtEmailSignUp: EditText
    private lateinit var edtPasswordSignUp: EditText
    private lateinit var edtNameSignUp: EditText
    private lateinit var btnSignUp: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)

        edtEmailSignUp = findViewById(R.id.edtEmailSignUp)
        edtPasswordSignUp = findViewById(R.id.edtPasswordSignUp)
        edtNameSignUp = findViewById(R.id.edtNameSignUp)
        btnSignUp = findViewById(R.id.btnSignUp)

        val btnSwitchLogIn: Button = findViewById(R.id.btnSwitchLogIn)

        btnSignUp.setOnClickListener {
            signUp()
        }

        btnSwitchLogIn.setOnClickListener {
            val intent = Intent(this, LogIn::class.java)
            startActivity(intent)
        }
//        val newUser = User("user@example.com", "pass", "John Doe",
//            mutableListOf(
//                App(true, R.drawable.instagram, "Instagram"),
//                App(true, R.drawable.snapchat, "Snapchat"))
//        )
//
//        val databaseManager = DatabaseManager.getInstance()
//
//        databaseManager.signUp(newUser) { success ->
//            if (success) {
//                Toast.makeText(this, "Sign-up successful!", Toast.LENGTH_SHORT).show()
//                val intent = Intent(this, MainPage::class.java)
//                startActivity(intent)
//            } else {
//                Toast.makeText(this, "Sign-up failed. Email may already be in use.", Toast.LENGTH_SHORT).show()
//                val intent = Intent(this, MainPage::class.java)
//                startActivity(intent)
//            }
//        }
    }

    private fun signUp() {
        val email = edtEmailSignUp.text.toString().trim()
        val password = edtPasswordSignUp.text.toString().trim()
        val name = edtNameSignUp.toString().trim()

        if (email.isEmpty() || password.isEmpty() || name.isEmpty()) {
            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show()
            return
        }

        val databaseManager = DatabaseManager.getInstance()

        databaseManager.signUp(User(email, password, name, mutableListOf())) { success ->
            if (success) {
                Toast.makeText(this, "Sign-Up successful!", Toast.LENGTH_SHORT).show()
                val intent = Intent(this, MainPage::class.java)
                startActivity(intent)
            } else {
                Toast.makeText(this, "Log-In failed. Email already in use.", Toast.LENGTH_SHORT).show()
            }
        }
    }
}

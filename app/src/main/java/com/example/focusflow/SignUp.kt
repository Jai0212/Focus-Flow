package com.example.focusflow

import android.content.Intent
import android.os.Bundle
import android.provider.ContactsContract.Data
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class SignUp : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var editTextEmail: EditText
    private lateinit var editTextPassword: EditText
    private lateinit var buttonSignUp: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)

        // Initialize Firebase Auth
        auth = FirebaseAuth.getInstance()

        editTextEmail = findViewById(R.id.editTextEmail)
        editTextPassword = findViewById(R.id.editTextPassword)
        buttonSignUp = findViewById(R.id.buttonSignUp)

        val databaseManager = DatabaseManager.getInstance()
        val newUser = User("user@example,com", "pass", "John Doe", mutableListOf())

        databaseManager.logIn(newUser) { success ->
            if (success) {
                Toast.makeText(this, "Sign-up successful!", Toast.LENGTH_SHORT).show()
                val intent = Intent(this, MainPage::class.java)
                startActivity(intent)
            } else {
                Toast.makeText(this, "Sign-up failed. Email may already be in use.", Toast.LENGTH_SHORT).show()
            }
        }
    }
}

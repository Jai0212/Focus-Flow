package com.example.focusflow

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity

class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_splash)

        val databaseManager = DatabaseManager.getInstance()

        Handler(Looper.getMainLooper()).postDelayed({
            val sharedPreferences = getSharedPreferences("curr_user_session", Context.MODE_PRIVATE)
            val userEmail = sharedPreferences.getString("curr_user_email", null)

            if (userEmail != null) {
                databaseManager.getUser(userEmail) { user ->
                    if (user != null) {
                        databaseManager.setCurrUser(user)
                        startActivity(Intent(this, BlockedAppPage::class.java))
                    } else {
                        startActivity(Intent(this, LogIn::class.java))
                    }
                    finish()
                }
            } else {
                startActivity(Intent(this, LogIn::class.java))
                finish()
            }
        }, 500)
    }
}

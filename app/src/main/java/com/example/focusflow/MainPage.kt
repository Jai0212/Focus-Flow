package com.example.focusflow

import android.content.ContentValues.TAG
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.Recycler

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

        databaseManager.getActiveApps { activeApps ->
            val rvBlockedAppsRecyclerView: RecyclerView = findViewById(R.id.rvBlockedAppsRecyclerView)
            rvBlockedAppsRecyclerView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
            rvBlockedAppsRecyclerView.adapter = BlockedAppsRecyclerViewAdapter(activeApps)
        }

        val imgMainPageAddApp: ImageView = findViewById(R.id.imgMainPageAddApp)
        imgMainPageAddApp.setOnClickListener {
            databaseManager.addApp(App(true, R.drawable.instagram, "Instagram"))
        }

        val bMPedit: Button = findViewById(R.id.bMPedit)
        bMPedit.setOnClickListener {
            val intent = Intent(this, EditPage::class.java)
            startActivity(intent)
        }
    }
}
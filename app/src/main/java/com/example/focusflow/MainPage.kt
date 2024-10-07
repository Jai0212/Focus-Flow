package com.example.focusflow

import android.content.Intent
import android.os.Bundle
import android.widget.Button
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

        val images = listOf(
            R.drawable.instagram,
            R.drawable.snapchat
        )

        val rvBlockedAppsRecyclerView: RecyclerView = findViewById(R.id.rvBlockedAppsRecyclerView)
        rvBlockedAppsRecyclerView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        rvBlockedAppsRecyclerView.adapter =  BlockedAppsRecyclerViewAdapter(images)

        val bMPedit: Button = findViewById(R.id.bMPedit)

        bMPedit.setOnClickListener {
            val intent = Intent(this, EditPage::class.java)
            startActivity(intent)
        }
    }
}
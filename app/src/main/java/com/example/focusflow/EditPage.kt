package com.example.focusflow

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class EditPage : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_edit_page)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Sample app list
        val appList = listOf(
            App("App 1", R.drawable.instagram, true),
            App("App 2", R.drawable.snapchat, false),
            App("App 3", R.drawable.snapchat, true)
        )

        val rvEditPage: RecyclerView = findViewById(R.id.rvEditPage)
        rvEditPage.layoutManager = LinearLayoutManager(this)
        rvEditPage.adapter = EditPageRecyclerViewAdapter(this, appList)
    }
}
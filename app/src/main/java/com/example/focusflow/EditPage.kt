package com.example.focusflow

import android.os.Bundle
import android.view.MenuItem
import android.widget.ImageView
import android.widget.Toolbar
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

        val tlEditPage: androidx.appcompat.widget.Toolbar = findViewById(R.id.tlEditPage)

        val imgEditPageBackArrow: ImageView = findViewById(R.id.imgEditPageBackArrow)
        imgEditPageBackArrow.setOnClickListener {
            onBackPressed()
        }

        val databaseManager = DatabaseManager.getInstance()

        databaseManager.getAllApps { allApps ->
            val rvEditPage: RecyclerView = findViewById(R.id.rvEditPage)
            rvEditPage.layoutManager = LinearLayoutManager(this)
            rvEditPage.adapter = allApps?.let { EditPageRecyclerViewAdapter(this, it) }
        }
    }
}
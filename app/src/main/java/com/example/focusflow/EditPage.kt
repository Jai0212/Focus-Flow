package com.example.focusflow

import android.content.Intent
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

        val intent = intent
        val from = intent.getStringExtra("from")

        val tlEditPage: androidx.appcompat.widget.Toolbar = findViewById(R.id.tlEditPage)

        val imgEditPageBack: ImageView = findViewById(R.id.imgEditPageBack)
        imgEditPageBack.setOnClickListener {
            startActivity(Intent(this, MainPage::class.java))
        }

        val imgEditPageLogo: ImageView = findViewById(R.id.imgEditPageLogo)
        imgEditPageLogo.setOnClickListener {
            startActivity(Intent(this, MainPage::class.java))
        }

        val databaseManager = DatabaseManager.getInstance()

        if (from == "edit") {
            databaseManager.getAllApps { allApps ->
                val rvEditPage: RecyclerView = findViewById(R.id.rvEditPage)
                rvEditPage.layoutManager = LinearLayoutManager(this)
                rvEditPage.adapter = EditPageRecyclerViewAdapter(this, allApps, "edit")
            }
        }
        else {
            val installedApps = databaseManager.getInstalledApps(packageManager)

            val rvEditPage: RecyclerView = findViewById(R.id.rvEditPage)
            rvEditPage.layoutManager = LinearLayoutManager(this)
            rvEditPage.adapter = EditPageRecyclerViewAdapter(this, installedApps, "add")
        }
    }
}
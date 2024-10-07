package com.example.focusflow

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class EditPageRecyclerViewAdapter(private val context: Context, private val apps: List<App>) : RecyclerView.Adapter<EditPageRecyclerViewAdapter.ViewHolder>() {

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val appLogo: ImageView = itemView.findViewById(R.id.imgAppLogo)
        val appName: TextView = itemView.findViewById(R.id.tvAppName)
        val toggleButton: Button = itemView.findViewById(R.id.btnToggle)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.item_edit, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val app = apps[position]

        // Set app logo and name
        holder.appLogo.setImageResource(app.logo)
        holder.appName.text = app.name

        // Set button text based on app's activation state
        holder.toggleButton.text = if (app.isActive) "Deactivate" else "Activate"

        // Toggle activation state when button is clicked
        holder.toggleButton.setOnClickListener {
            app.isActive = !app.isActive
            notifyItemChanged(position) // Update the item to reflect the state change
        }
    }

    override fun getItemCount(): Int {
        return apps.size
    }
}

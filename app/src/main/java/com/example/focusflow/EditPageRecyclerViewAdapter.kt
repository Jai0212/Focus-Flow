package com.example.focusflow

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class EditPageRecyclerViewAdapter(
    private val context: Context,
    private val apps: List<App>,
    private val from: String) : RecyclerView.Adapter<EditPageRecyclerViewAdapter.ViewHolder>() {

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
        val databaseManager = DatabaseManager.getInstance()

        holder.appLogo.setImageResource(app.logo)
        holder.appName.text = app.name


        if (from == "edit") {
            holder.toggleButton.text = if (app.active) "Deactivate" else "Activate"
        }
        else {
            databaseManager.isAppInDatabase(app) { isThere ->
                if (isThere) {
                    holder.toggleButton.text = "Remove"
                } else {
                    holder.toggleButton.text = "Add"
                }
            }
        }

        // Toggle activation state when button is clicked
        holder.toggleButton.setOnClickListener {
            if (from == "edit") {
                app.active = !app.active
                holder.toggleButton.text = if (app.active) "Deactivate" else "Activate"

                databaseManager.updateAppStateInDatabase(app)
            }
            else {
                databaseManager.isAppInDatabase(app) { isThere ->
                    if (isThere) {
                        databaseManager.removeApp(app)
                    } else {
                        databaseManager.addApp(App(true, app.logo, app.name))
                    }
                }
            }

            notifyItemChanged(position) // Update the item to reflect the state change
        }
    }

    override fun getItemCount(): Int {
        return apps.size
    }
}

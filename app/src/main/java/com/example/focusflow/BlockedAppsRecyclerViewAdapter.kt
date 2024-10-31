package com.example.focusflow

import android.content.Context
import android.provider.ContactsContract.Data
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView

class BlockedAppsRecyclerViewAdapter(
    private val context: Context,
    private val items: List<App>) : RecyclerView.Adapter<BlockedAppsRecyclerViewAdapter.ViewHolder>() {

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val ivItemBlockedApp: ImageView = itemView.findViewById(R.id.ivItemBlockedApp)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_blocked_app, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val databaseManager = DatabaseManager.getInstance()
        val appIcon = databaseManager.getAppIconFromPackage(items[position].packageName, context.packageManager)

        if (appIcon != null) {
            holder.ivItemBlockedApp.setImageDrawable(appIcon)
        } else {
            Log.d("BLOCKED APP RECYCLER VIEW", "Skipped App: " + items[position].packageName)
            return
        }
    }

    override fun getItemCount() = items.size
}

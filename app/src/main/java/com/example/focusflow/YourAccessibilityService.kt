package com.example.focusflow

import android.accessibilityservice.AccessibilityService
import android.view.accessibility.AccessibilityEvent
import android.content.Intent
import android.util.Log

class YourAccessibilityService : AccessibilityService() {

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        if (event?.eventType == AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED) {
            val packageName = event.packageName.toString()

            val databaseManager = DatabaseManager.getInstance()

            databaseManager.isBlocked(packageName) { isBlocked ->
                if (isBlocked) {
                    Log.d("YourAccessibilityService", "App Opened: $packageName")

                    databaseManager.updateUserDetails()

                    val launchIntent = Intent(this, MainPage::class.java)
                    launchIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                    startActivity(launchIntent)
                }
            }
        }
    }

    override fun onInterrupt() {
        Log.d("YourAccessibilityService", "Interrupted")
    }
}
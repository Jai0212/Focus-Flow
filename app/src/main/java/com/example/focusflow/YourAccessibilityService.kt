package com.example.focusflow

import android.accessibilityservice.AccessibilityService
import android.view.accessibility.AccessibilityEvent
import android.content.Intent
import android.util.Log

class YourAccessibilityService : AccessibilityService() {

    private val temporaryWhitelist = mutableSetOf<String>()

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        if (event?.eventType == AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED) {

            val packageName = event.packageName.toString()

            val packageManager = packageManager

            val appName = packageManager.getApplicationLabel(
                packageManager.getApplicationInfo(packageName, 0)
            ).toString()

            val databaseManager = DatabaseManager.getInstance()

            if (temporaryWhitelist.contains(packageName)) {
                // Ignore apps in the whitelist
                Log.d("YourAccessibilityService", "App $packageName is temporarily whitelisted.")
                return
            }

            databaseManager.isBlocked(packageName) { isBlocked ->
                if (isBlocked && !temporaryWhitelist.contains(packageName)) {
                    Log.d("YourAccessibilityService", "App Opened: $packageName")

                    databaseManager.updateUserDetails()

                    temporaryWhitelist.add(packageName)

                    val launchIntent = Intent(this, BlockedAppPage::class.java)
                    launchIntent.putExtra("appName", appName)
                    launchIntent.putExtra("appPackage", packageName)
                    launchIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                    startActivity(launchIntent)
                }
            }
        }
    }

    override fun onInterrupt() {
        Log.d("YourAccessibilityService", "Interrupted")
    }

    fun removeFromWhitelist(packageName: String) {
        temporaryWhitelist.remove(packageName)
        Log.d("YourAccessibilityService", "Removed $packageName from the whitelist.")
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        intent?.getStringExtra("removeFromWhitelist")?.let { packageName ->
            // Delay removing the app from the whitelist for 5 seconds
            temporaryWhitelist.add(packageName)
            Log.d("YourAccessibilityService", "Delaying removal of $packageName from whitelist.")

            val handler = android.os.Handler()
            handler.postDelayed({
                removeFromWhitelist(packageName)
            }, 5000) // 5 seconds
        }
        return super.onStartCommand(intent, flags, startId)
    }
}
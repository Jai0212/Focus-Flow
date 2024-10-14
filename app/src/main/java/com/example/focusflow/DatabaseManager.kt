package com.example.focusflow

import android.content.ContentValues.TAG
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.util.Log
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.GenericTypeIndicator
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import kotlin.math.log

class DatabaseManager private constructor() {
    private val database: FirebaseDatabase = FirebaseDatabase.getInstance()
    private val databaseReference: DatabaseReference = database.reference
    private val storage: FirebaseStorage = FirebaseStorage.getInstance()
    private val storageReference: StorageReference = storage.reference
    private var currUser: User? = null

    companion object {
        @Volatile
        private var INSTANCE: DatabaseManager? = null

        fun getInstance(): DatabaseManager {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: DatabaseManager().also { INSTANCE = it }
            }
        }
    }

    fun signUp(user: User, onComplete: (Boolean) -> Unit) {
        // Get a reference to the "users" node in the database
        val usersRef = databaseReference.child("users")

        // Fetch all users from the "users" node
        usersRef.get().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val snapshot = task.result
                var emailExists = false

                // Iterate over each child (user) in the snapshot
                if (snapshot != null) {
                    for (userSnapshot in snapshot.children) {
                        val storedUser = userSnapshot.getValue(User::class.java)
                        // Check if the email already exists
                        if (storedUser?.email == user.email) {
                            emailExists = true
                            break // Exit the loop if email is found
                        }
                    }
                }

                // If the email does not exist, add the new user
                if (!emailExists) {
                    usersRef.child(user.email.replace(".", ",")).setValue(user)
                        .addOnCompleteListener { addTask ->
                            onComplete(addTask.isSuccessful)
                            currUser = user
                        }
                } else {
                    // Email already in use
                    onComplete(false)
                }
            } else {
                // Handle possible errors during the fetch
                onComplete(false)
            }
        }
    }


    fun logIn(user: User, onComplete: (Boolean) -> Unit) {
        // Get a reference to the "users" node in the database
        val usersRef = databaseReference.child("users")

        // Fetch all users from the "users" node
        usersRef.get().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val snapshot = task.result
                if (snapshot != null) {
                    // Iterate over each child (user) in the snapshot
                    for (userSnapshot in snapshot.children) {
                        val storedUser = userSnapshot.getValue(User::class.java)
                        // Check if the email matches
                        if (storedUser?.email == user.email) {
                            // If email matches, check the password
                            if (storedUser.password == user.password) {
                                // Login successful
                                onComplete(true)
                                currUser = storedUser
                                return@addOnCompleteListener
                            }
                        }
                    }
                }
                // If no matching user is found or password is incorrect
                onComplete(false)
            } else {
                // Handle possible errors during the fetch
                onComplete(false)
            }
        }
    }

    fun logOut(onComplete: (Boolean) -> Unit) {
        if (currUser != null) {
            currUser = null
            Log.d("FIREBASE", "Log Out Successful")
            onComplete(true)
        } else {
            Log.e("FIREBASE", "Null User")
            onComplete(false)
        }
    }

    fun getAllApps(callback: (List<App>) -> Unit) {
        val user = currUser
        if (user == null) {
            Log.e("FIREBASE", "Null User")
            callback(emptyList())
            return
        }

        databaseReference.child("users").child(user.email.replace(".", ",")).child("blockedApps").addListenerForSingleValueEvent(object :
            ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val blockedAppsList = mutableListOf<App>()
                for (appSnapshot in snapshot.children) {
                    val app = appSnapshot.getValue(App::class.java)
                    if (app != null) {
                        blockedAppsList.add(app)
                    }
                }
                callback(blockedAppsList)
            }

            override fun onCancelled(error: DatabaseError) {
                callback(emptyList())
            }
        })
    }

    fun getActiveApps(callback: (List<App>) -> Unit) {
        getAllApps { blockedApps ->
            val activeAppsList = blockedApps.filter { it.active }
            callback(activeAppsList)
        }
    }

    fun getCurrUser(): User? {
        return currUser
    }

    fun updateAppStateInDatabase(app: App) {
        val user = currUser
        if (user == null) {
            Log.e("FIREBASE", "Null User")
            return
        }

        // Reference to the user's blockedApps list in the database
        val blockedAppsRef = databaseReference.child("users").child(user.email.replace(".", ",")).child("blockedApps")

        // Retrieve the current list of blocked apps
        blockedAppsRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    for (appSnapshot in snapshot.children) {
                        val blockedApp = appSnapshot.getValue(App::class.java)

                        // Find the app by name (or another unique field)
                        if (blockedApp != null && blockedApp.name == app.name) {
                            // Toggle the active state
                            val updatedApp = blockedApp.copy(active = !blockedApp.active)

                            // Update the app in Firebase
                            blockedAppsRef.child(appSnapshot.key!!).setValue(updatedApp)
                                .addOnSuccessListener {
                                    Log.d("FIREBASE", "App state updated successfully: ${updatedApp.name} (active: ${updatedApp.active})")
                                }
                                .addOnFailureListener { exception ->
                                    Log.e("FIREBASE", "Failed to update app state: ${exception.message}")
                                }
                            break
                        }
                    }
                } else {
                    Log.e("FIREBASE", "No blocked apps found for user")
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("FIREBASE", "Failed to retrieve blocked apps: ${error.message}")
            }
        })
    }


    fun addApp(newApp: App) {
        val user = currUser
        if (user == null) {
            Log.e("FIREBASE", "Null User")
            return
        }

        // Reference to the user's blockedApps list
        val blockedAppsRef = databaseReference.child("users").child(user.email.replace(".", ",")).child("blockedApps")

        blockedAppsRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                // Check if blockedApps is currently a list
                val blockedAppsList = mutableListOf<App>()

                // If blockedApps already exists, retrieve the current list
                if (snapshot.exists()) {
                    for (appSnapshot in snapshot.children) {
                        val app = appSnapshot.getValue(App::class.java)
                        if (app != null) {
                            blockedAppsList.add(app)
                        }
                    }
                }

                // Add the new app to the list
                blockedAppsList.add(newApp)

                // Update the entire list in Firebase (replace the old one)
                blockedAppsRef.setValue(blockedAppsList)
                    .addOnSuccessListener {
                        Log.d("FIREBASE", "App added successfully: ${newApp.name}")
                    }
                    .addOnFailureListener { exception ->
                        Log.e("FIREBASE", "Failed to add app: ${exception.message}")
                    }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("FIREBASE", "Failed to retrieve blocked apps: ${error.message}")
            }
        })
    }

    fun removeApp(appToRemove: App) {
        val user = currUser
        if (user == null) {
            Log.e("FIREBASE", "Null User")
            return
        }

        // Reference to the user's blockedApps list
        val blockedAppsRef = databaseReference.child("users").child(user.email.replace(".", ",")).child("blockedApps")

        blockedAppsRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                // List to hold the current blocked apps
                val blockedAppsList = mutableListOf<App>()

                // If blockedApps already exists, retrieve the current list
                if (snapshot.exists()) {
                    for (appSnapshot in snapshot.children) {
                        val app = appSnapshot.getValue(App::class.java)
                        if (app != null) {
                            blockedAppsList.add(app)
                        }
                    }
                }

                // Remove the specified app from the list
                blockedAppsList.removeIf { it.name == appToRemove.name && it.logo == appToRemove.logo }

                // Update the entire list in Firebase (replace the old one)
                blockedAppsRef.setValue(blockedAppsList)
                    .addOnSuccessListener {
                        Log.d("FIREBASE", "App removed successfully: ${appToRemove.name}")
                    }
                    .addOnFailureListener { exception ->
                        Log.e("FIREBASE", "Failed to remove app: ${exception.message}")
                    }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("FIREBASE", "Failed to retrieve blocked apps: ${error.message}")
            }
        })
    }


    fun getInstalledApps(packageManager: PackageManager): List<App> {
        val appsList = mutableListOf<App>()

        // Get the list of installed applications
        val packages = packageManager.getInstalledApplications(PackageManager.GET_META_DATA)

        for (packageInfo in packages) {
            // Filter out system apps (if needed)
            if (packageManager.getLaunchIntentForPackage(packageInfo.packageName) != null &&
                (packageInfo.flags and ApplicationInfo.FLAG_SYSTEM) == 0) {

                // Get the app name, logo, and other details
                val appName = packageManager.getApplicationLabel(packageInfo).toString()
                val appIcon = packageInfo.icon  // App icon as resource ID
                val app = App(
                    active = false,  // You can set this based on your logic
                    logo = appIcon,  // App icon resource
                    name = appName   // App name
                )
                appsList.add(app)
            }
        }

        return appsList
    }

    fun isAppInDatabase(appParam: App, callback: (Boolean) -> Unit) {
        val appName = appParam.name

        val user = currUser
        if (user == null) {
            Log.e("FIREBASE", "Null User")
            callback(false)
            return
        }

        databaseReference.child("users").child(user.email.replace(".", ",")).child("blockedApps")
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    var appFound = false
                    for (appSnapshot in snapshot.children) {
                        val app = appSnapshot.getValue(App::class.java)
                        if (app != null && app.name == appName) {
                            appFound = true
                            break
                        }
                    }
                    callback(appFound) // Return true if found, otherwise false
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.e("FIREBASE", "Failed to check if app is blocked: ${error.message}")
                    callback(false) // Return false on error
                }
            })
    }

}

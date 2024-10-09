package com.example.focusflow

import android.content.ContentValues.TAG
import android.util.Log
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.GenericTypeIndicator
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
            // Clear the current user session
            currUser = null

            // Notify that the logout was successful
            onComplete(true)
        } else {
            // If no user is logged in, return false
            onComplete(false)
        }
    }

    fun getAllApps(onComplete: (List<App>?) -> Unit) {
        val user = currUser
        if (user == null) {
            onComplete(null)
            return
        }

        val blockedAppsRef = databaseReference.child("users").child(user.email.replace(".", ",")).child("blockedApps")
        blockedAppsRef.get().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val snapshot = task.result
                val appList = mutableListOf<App>()

                if (snapshot != null && snapshot.exists()) {
                    // Use GenericTypeIndicator to correctly deserialize the HashMap
                    val typeIndicator = object : GenericTypeIndicator<HashMap<String, App>>() {}
                    val appsMap = snapshot.getValue(typeIndicator)

                    appsMap?.forEach { (_, app) ->
                        appList.add(app)
                    }
                }

                onComplete(appList)
            } else {
                onComplete(null)
            }
        }
    }


    fun getActiveApps(onComplete: (List<App>?) -> Unit) {
        // Call getAllApps to get the list of all apps
        getAllApps { allApps ->
            if (allApps != null) {
                // Filter the list to include only active apps
                val activeApps = allApps.filter { it.active }
                // Return the list of active apps via the callback
                onComplete(activeApps)
            } else {
                // If there are no apps or an error occurred, return null
                onComplete(null)
            }
        }
    }

    fun getCurrUser(): User? {
        return currUser
    }

    fun updateAppStateInDatabase(app: App) {
        currUser?.let { user ->
            // Get a reference to the "blockedApps" node for the logged-in user
            val appRef = databaseReference.child("users")
                .child(user.email.replace(".", ","))
                .child("blockedApps")
                .child(app.name)  // Assuming app names are unique

            appRef.child("active").setValue(app.active)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        // Update successful
                        println("${app.name} activation state updated to ${app.active}")
                    } else {
                        // Handle any errors that occur during the update
                        println("Failed to update ${app.name} activation state")
                    }
                }
        }
    }

    fun addApp(newApp: App) {
        currUser?.let { user ->
            // Get a reference to the "blockedApps" node for the logged-in user
            val appRef = databaseReference.child("users")
                .child(user.email.replace(".", ","))
                .child("blockedApps")
                .child(newApp.name)  // Assuming app names are unique

            // Set the new app data in the database (adds if it doesn't exist)
            appRef.setValue(newApp)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        // App added successfully
                        println("${newApp.name} has been added to the user's blocked apps list")
                    } else {
                        // Handle any errors that occur during the addition
                        println("Failed to add ${newApp.name} to the user's blocked apps list")
                    }
                }
        }
    }

}

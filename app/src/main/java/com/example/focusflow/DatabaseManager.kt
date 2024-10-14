package com.example.focusflow

import android.content.ContentValues.TAG
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
            // Clear the current user session
            currUser = null

            // Notify that the logout was successful
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

}

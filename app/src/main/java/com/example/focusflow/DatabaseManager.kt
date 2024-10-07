package com.example.focusflow

import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference

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
                                currUser = user
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


    fun getCurrUser(): User? {
        return currUser
    }
}

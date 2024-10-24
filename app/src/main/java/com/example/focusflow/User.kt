package com.example.focusflow

data class User(
    val email: String,
    val password: String,
    val name: String,
    val blockedApps: MutableList<App>,
    val timeSaved: Int = 0,
    val openingsPrevented: Int = 0,
    val prevActivities: MutableList<String> = mutableListOf()
) {
    constructor() : this("", "", "", mutableListOf())
}

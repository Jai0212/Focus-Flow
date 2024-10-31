package com.example.focusflow

data class User(
    val email: String,
    val password: String,
    val name: String,
    val blockedApps: MutableList<App>,
    var timeSaved: Double = 0.0,
    var openingsPrevented: Int = 1,
    val prevActivities: MutableList<String> = mutableListOf()
) {
    constructor() : this("", "", "", mutableListOf())
}

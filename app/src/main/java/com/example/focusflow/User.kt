package com.example.focusflow

data class User(val email: String, val password: String, val name: String, val blockedApps: MutableList<App>) {
    constructor() : this("", "", "", mutableListOf(App()))
}

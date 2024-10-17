package com.example.focusflow

data class App(
    var active: Boolean = false,
    val logo: Int = 0,
    val name: String = "", // a user-recognizable name
    val packageName: String = "" // the underlying package name
)
package com.example.focusflow

data class App(val logo: Int, val name: String, var isActive: Boolean) {
    constructor() : this(0, "", false)
}
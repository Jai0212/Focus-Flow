package com.example.focusflow

data class App(val name: String, val logo: Int, var isActive: Boolean) {
    constructor() : this("", 0, false)
}
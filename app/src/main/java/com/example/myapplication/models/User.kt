package com.example.myapplication.models

data class User(
    val uid: String = "",
    val email: String = "",
    val name: String = "",
    val createdAt: Long = System.currentTimeMillis()
)

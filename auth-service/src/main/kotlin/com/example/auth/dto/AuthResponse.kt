package com.example.auth.dto

data class AuthResponse(
    val accessToken: String,
    val userId: Long,
    val userName: String,
    val email: String,
    val authority: List<String>,
)

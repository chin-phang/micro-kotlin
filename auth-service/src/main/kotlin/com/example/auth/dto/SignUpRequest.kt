package com.example.auth.dto

data class SignUpRequest(
    val userName: String,
    val email: String,
    val password: String,
)

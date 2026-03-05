package com.example.auth.repository

import com.example.auth.entity.User
import org.springframework.data.repository.kotlin.CoroutineCrudRepository

interface UserRepository : CoroutineCrudRepository<User, Long> {
    suspend fun findByUsername(username: String): User?
    suspend fun findByEmail(email: String): User?
}

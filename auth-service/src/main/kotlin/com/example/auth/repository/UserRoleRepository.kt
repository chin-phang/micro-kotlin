package com.example.auth.repository

import com.example.auth.entity.UserRole
import org.springframework.data.repository.kotlin.CoroutineCrudRepository

interface UserRoleRepository : CoroutineCrudRepository<UserRole, Long>

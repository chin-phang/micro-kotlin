package com.example.auth.repository

import com.example.auth.entity.Role
import kotlinx.coroutines.flow.Flow
import org.springframework.data.r2dbc.repository.Query
import org.springframework.data.repository.kotlin.CoroutineCrudRepository

interface RoleRepository : CoroutineCrudRepository<Role, Long> {

    suspend fun findByName(name: String): Role?

    @Query("""
        SELECT r.id, r.name
        FROM role r
        JOIN user_role ur ON r.id = ur.role_id
        WHERE ur.user_id = :userId
    """)
    fun findByUserId(userId: Long): Flow<Role>
}

package com.example.auth.entity

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Table
import java.time.Instant

@Table("app_user")
data class User(
    @Id val id: Long? = null,
    val username: String,
    val password: String,
    val email: String,
    val enabled: Boolean = true,
    override val createdAt: Instant? = null,
    override val createdBy: String? = null,
    override val lastModifiedAt: Instant? = null,
    override val lastModifiedBy: String? = null,
) : CustomAudit(createdAt, createdBy, lastModifiedAt, lastModifiedBy)

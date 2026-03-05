package com.example.auth.entity

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Table
import java.time.Instant

@Table("user_role")
data class UserRole(
    @Id val id: Long? = null,
    val userId: Long,
    val roleId: Long,
    override val createdAt: Instant? = null,
    override val createdBy: String? = null,
    override val lastModifiedAt: Instant? = null,
    override val lastModifiedBy: String? = null,
) : CustomAudit(createdAt, createdBy, lastModifiedAt, lastModifiedBy)

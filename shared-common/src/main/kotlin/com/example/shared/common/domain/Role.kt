package com.example.shared.common.domain

import jakarta.persistence.Id
import jakarta.persistence.Table
import java.time.Instant

@Table(name = "role")
data class Role(
    @Id
    val id: Long? = null,

    val name: String,

    override val createdAt: Instant? = null,
    override val createdBy: String? = null,
    override val lastModifiedAt: Instant? = null,
    override val lastModifiedBy: String? = null
) : CustomAudit(createdAt, createdBy, lastModifiedAt, lastModifiedBy)
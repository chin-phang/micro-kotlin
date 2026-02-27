package com.example.shared.common.domain

import jakarta.persistence.Column
import java.time.Instant

abstract class CustomAudit(
    @Column("created_at")
    open val createdAt: Instant? = null,

    @Column("created_by")
    open val createdBy: String? = null,

    @Column("last_modified_at")
    open val lastModifiedAt: Instant? = null,

    @Column("last_modified_by")
    open val lastModifiedBy: String? = null
)
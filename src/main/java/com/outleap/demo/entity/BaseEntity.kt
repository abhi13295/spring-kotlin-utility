package com.outleap.demo.entity

import com.fasterxml.jackson.annotation.JsonIgnore
import jakarta.persistence.Column
import org.hibernate.annotations.CreationTimestamp
import org.hibernate.annotations.UpdateTimestamp
import org.hibernate.envers.Audited
import java.util.*

abstract class BaseEntity {
    @CreationTimestamp
    @Column(name = "created_at")
    var createdAt = Date()

    @UpdateTimestamp
    @Column(name = "updated_at")
    var updatedAt = Date()

    @JsonIgnore
    @Column(name = "updated_by")
    var updatedBy: Long? = null

    @JsonIgnore
    @Column(name = "ip_address")
    @Audited
    var ipAddress: String? = null

    @JsonIgnore
    @Column(name = "is_delete")
    @Audited
    var isDeleted: Boolean? = null
}

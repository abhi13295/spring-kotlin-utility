package com.outleap.demo.entity

import jakarta.persistence.*
import org.hibernate.annotations.SQLRestriction

@Entity(name = "customer")
@SQLRestriction(value = "is_delete = false")
class Customer : BaseEntity() {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null

    @Column(name = "name")
    var name: String? = null

    @Column(name = "email")
    var email: String? = null

    @Column(name = "contact_number")
    var contactNumber: String? = null
}

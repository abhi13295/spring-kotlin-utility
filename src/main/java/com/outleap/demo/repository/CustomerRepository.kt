package com.outleap.demo.repository

import com.outleap.demo.entity.Customer
import org.springframework.data.jpa.repository.JpaRepository

interface CustomerRepository : JpaRepository<Customer, Long> {

    fun findByEmail(email: String): List<Customer>
}

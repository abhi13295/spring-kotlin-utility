package com.outleap.demo.service

import com.outleap.demo.entity.Customer
import com.outleap.demo.repository.CustomerRepository
import org.springframework.stereotype.Service

@Service
class CustomerService(
    private val customerRepository: CustomerRepository
) {
    fun createCustomer(customer: Customer): Customer {
        return customerRepository.save(customer)
    }
}

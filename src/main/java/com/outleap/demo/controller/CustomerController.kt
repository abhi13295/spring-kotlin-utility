package com.outleap.demo.controller

import com.outleap.demo.dto.CustomerDto
import com.outleap.demo.facade.CustomerFacade
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("v1/customer")
class CustomerController(
    private val customerFacade: CustomerFacade
) {
    @PostMapping("")
    fun createCustomer(@RequestBody customerDto: CustomerDto): Boolean {
        customerFacade.createCustomerFacade(customerDto)
        return true
    }
}

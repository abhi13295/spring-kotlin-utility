package com.outleap.demo.controller

import com.outleap.demo.dto.CustomerDto
import com.outleap.demo.dto.CustomerResponseDto
import com.outleap.demo.dto.ResponseWrapper
import com.outleap.demo.entity.Customer
import com.outleap.demo.facade.CustomerFacade
import com.outleap.demo.facade.CustomerFacadeLocked
import org.springframework.web.bind.annotation.PatchMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("v1/customer")
class CustomerController(
    private val customerFacade: CustomerFacade,
    private val customerFacadeLocked: CustomerFacadeLocked
) {
    @PostMapping("")
    fun createCustomer(@RequestBody customerDto: CustomerDto): ResponseWrapper<Boolean> {
        customerFacade.createCustomerFacade(customerDto)
        return ResponseWrapper(success = true, message = "success", data = true)
    }

    @PatchMapping("")
    fun updateCustomer(@RequestBody customerDto: CustomerDto): ResponseWrapper<CustomerResponseDto> {
        val dto = customerFacadeLocked.updateCustomer(customerDto.email!!, customerDto)
        return ResponseWrapper(success = true, message = "success", data = dto)
    }
}

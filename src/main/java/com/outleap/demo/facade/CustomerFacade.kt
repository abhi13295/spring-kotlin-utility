package com.outleap.demo.facade

import com.outleap.demo.dto.CustomerDto
import com.outleap.demo.dto.CustomerResponseDto
import com.outleap.demo.entity.Customer
import com.outleap.demo.repository.CustomerRepository
import com.outleap.demo.service.CustomerService
import com.outleap.demo.utils.exceptions.CustomException
import com.outleap.notification.dto.NotificationDto
import com.outleap.notification.facade.NotificationFacade
import org.springframework.stereotype.Component
import java.util.concurrent.Executors

@Component
class CustomerFacade(
    private val customerRepository: CustomerRepository,
    private val customerService: CustomerService,
    private val notificationFacade: NotificationFacade
) {


    private val executorService = Executors.newSingleThreadExecutor()

    fun createCustomerFacade(customerDto: CustomerDto) {
        val customer = Customer()
        customer.contactNumber = customerDto.contactNumber
        customer.email = customerDto.email
        customer.name = customerDto.name
        val customerEntity = customerService.createCustomer(customer)

        executorService.execute {
            sendNotification(customerEntity)
        }
    }

    fun updateCustomer(customerDto: CustomerDto, email: String?): CustomerResponseDto {
        val customer = email?.let { customerRepository.findByEmail(email).firstOrNull() }?: throw CustomException("Customer not found")
        customer.contactNumber = customerDto.contactNumber?: customer.contactNumber
        customer.email = customerDto.email?: customer.email
        customer.name = customerDto.name?: customer.name
        val customerEntity = customerService.updateCustomer(customer)

        executorService.execute {
            sendNotification(customerEntity)
        }

        return CustomerResponseDto(name = customerEntity.name, email = customerEntity.email, contactNumber = customerEntity.contactNumber)
    }

    private fun sendNotification(customer: Customer) {
        notificationFacade.sendNotification(NotificationDto(customerId = customer.id, notificationType = "EMAIL"))
    }
}

package com.outleap.demo.facade

import com.outleap.demo.dto.CustomerDto
import com.outleap.demo.entity.Customer
import com.outleap.demo.service.CustomerService
import com.outleap.notification.dto.NotificationDto
import com.outleap.notification.facade.NotificationFacade
import org.springframework.stereotype.Component
import java.util.concurrent.Executors

@Component
class CustomerFacade(
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

    private fun sendNotification(customer: Customer) {
        notificationFacade.sendNotification(NotificationDto(customerId = customer.id, notificationType = "EMAIL"))
    }
}

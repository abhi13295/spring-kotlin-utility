package com.outleap.demo.facade

import com.outleap.demo.dto.CustomerDto
import com.outleap.demo.dto.CustomerResponseDto
import com.outleap.demo.entity.Customer
import com.outleap.demo.lock.ExecuteWithLock
import com.outleap.demo.lock.LockKey
import com.outleap.demo.service.CustomerService
import com.outleap.demo.utils.logger
import com.outleap.notification.dto.NotificationDto
import com.outleap.notification.facade.NotificationFacade
import org.springframework.stereotype.Component
import java.util.concurrent.Executors

@Component
@ExecuteWithLock
class CustomerFacadeLocked(
    val customerFacade: CustomerFacade
) {

    private val log = logger()

    fun updateCustomer(@LockKey key: String, customerDto: CustomerDto): CustomerResponseDto {
        log.info("inside updateCustomer | Locked $key $key $customerDto")
        return customerFacade.updateCustomer(customerDto, customerDto.email)
    }


}

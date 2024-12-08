package com.outleap.notification.repository

import com.outleap.notification.entity.Notification
import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.stereotype.Repository

@Repository
interface NotificationMongoRepo : MongoRepository<Notification, String> {
    fun findByCustomerId(customerId: Long): List<Notification>
}
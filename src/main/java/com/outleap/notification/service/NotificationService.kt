package com.outleap.notification.service

import com.outleap.notification.entity.Notification
import com.outleap.notification.repository.NotificationMongoRepo
import org.springframework.stereotype.Service

@Service
class NotificationService (
    private val notificationRepo: NotificationMongoRepo
) {

    fun createNotification(notification: Notification) {
        notificationRepo.save(notification);
    }
}
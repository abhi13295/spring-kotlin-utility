package com.outleap.notification.facade

import com.outleap.notification.entity.Notification
import com.outleap.notification.dto.NotificationDto
import com.outleap.notification.service.NotificationService
import lombok.extern.slf4j.Slf4j
import org.springframework.stereotype.Component
import java.util.logging.Logger

@Slf4j
@Component
class NotificationFacade(
    private val notificationService: NotificationService
) {

    private val log = Logger.getLogger(NotificationFacade::class.java.name)

    fun sendNotification(notificationDto: NotificationDto) {
        log.info("sending notification $notificationDto")
        val entity = Notification(
            customerId = notificationDto.customerId,
            notificationType = "EMAIL"
        )
        notificationService.createNotification(entity)
    }
}
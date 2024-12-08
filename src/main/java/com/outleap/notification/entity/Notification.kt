package com.outleap.notification.entity

import jakarta.persistence.Id
import lombok.AllArgsConstructor
import lombok.Getter
import lombok.NoArgsConstructor
import lombok.Setter
import org.hibernate.annotations.CreationTimestamp
import org.hibernate.annotations.UpdateTimestamp
import org.springframework.data.mongodb.core.mapping.Document
import org.springframework.data.mongodb.core.mapping.Field
import java.time.LocalDateTime

@Document("notification")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
data class Notification(
        @Id
        val id: String? = null,

        @Field("notification_type")
        val notificationType: String,

        @Field("entity_id")
        val customerId: Long? = null,

        @CreationTimestamp
        @Field(name = "created_at")
        var createdAt: LocalDateTime = LocalDateTime.now(),

        @UpdateTimestamp
        @Field(name = "updated_at")
        var updatedAt: LocalDateTime = LocalDateTime.now()
)


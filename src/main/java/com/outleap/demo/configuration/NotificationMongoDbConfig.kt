package com.outleap.demo.configuration

import org.springframework.context.annotation.Configuration
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories

@Configuration
@EnableMongoRepositories(basePackages = ["com.outleap.notification"], mongoTemplateRef = NotificationMongoDbConfig.MONGO_TEMPLATE)
class NotificationMongoDbConfig {
    companion object {
        const val MONGO_TEMPLATE = "notificationDbMongoTemplate"
    }
}
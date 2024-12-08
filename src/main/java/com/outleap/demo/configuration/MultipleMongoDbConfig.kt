package com.outleap.demo.configuration

import org.springframework.boot.autoconfigure.mongo.MongoProperties
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Primary
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.data.mongodb.core.SimpleMongoClientDatabaseFactory

@Configuration
class MultipleMongoDbConfig {

    @Primary
    @Bean(name = ["appDbProperties"])
    @ConfigurationProperties(prefix = "spring.data.mongodb.appdb")
    fun getAppDbProps(): MongoProperties {
        val properties = MongoProperties()
        return properties
    }

    @Bean(name = ["notificationDbProperties"])
    @ConfigurationProperties(prefix = "spring.data.mongodb.notificationdb")
    fun getNotificationDbProps(): MongoProperties {
        val properties = MongoProperties()
        return properties
    }

    @Primary
    @Bean(name = ["appDbMongoTemplate"])
    fun appDbMongoTemplate(): MongoTemplate {
        return MongoTemplate(appDbFactory(getAppDbProps()))
    }

    @Bean(name = ["notificationDbMongoTemplate"])
    fun notificationDbMongoTemplate(): MongoTemplate {
        return MongoTemplate(notificationDbFactory(getNotificationDbProps()))
    }


    @Primary
    @Bean
    fun appDbFactory(properties: MongoProperties): SimpleMongoClientDatabaseFactory {
        return SimpleMongoClientDatabaseFactory(properties.uri)
    }

    @Bean
    fun notificationDbFactory(properties: MongoProperties): SimpleMongoClientDatabaseFactory {
        return SimpleMongoClientDatabaseFactory(properties.uri)
    }
}
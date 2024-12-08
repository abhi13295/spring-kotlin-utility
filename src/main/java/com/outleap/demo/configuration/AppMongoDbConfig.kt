package com.outleap.demo.configuration

import org.springframework.context.annotation.Configuration
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories

@Configuration
@EnableMongoRepositories(basePackages = ["com.outleap.demo"], mongoTemplateRef = AppMongoDbConfig.MONGO_TEMPLATE)
class AppMongoDbConfig {
    companion object {
        const val MONGO_TEMPLATE = "appDbMongoTemplate"
    }
}
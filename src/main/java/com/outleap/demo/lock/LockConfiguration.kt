package com.outleap.demo.lock
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.redis.connection.RedisConnectionFactory
import org.springframework.integration.jdbc.lock.DefaultLockRepository
import org.springframework.integration.jdbc.lock.LockRepository
import org.springframework.integration.redis.util.RedisLockRegistry
import javax.sql.DataSource

@Configuration
class LockConfiguration {

    @Bean
    fun defaultLockRepository(dataSource: DataSource): LockRepository =
            DefaultLockRepository(dataSource).apply {
                setTimeToLive(10000)
            }

    @Bean(destroyMethod = "destroy")
    fun redisLockRegistry(redisConnectionFactory: RedisConnectionFactory) =
            RedisLockRegistry(redisConnectionFactory, "lock")
}
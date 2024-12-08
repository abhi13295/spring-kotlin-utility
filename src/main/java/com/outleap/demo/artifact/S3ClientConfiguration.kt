package com.outleap.demo.artifact

import com.amazonaws.ClientConfiguration
import com.amazonaws.auth.AWSCredentialsProviderChain
import com.amazonaws.auth.InstanceProfileCredentialsProvider
import com.amazonaws.auth.profile.ProfileCredentialsProvider
import com.amazonaws.regions.Regions
import com.amazonaws.services.s3.AmazonS3
import com.amazonaws.services.s3.AmazonS3ClientBuilder
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class S3ClientConfiguration {

    @Value("\${AWS_S3_TIMEOUT:20000}")
    val timeout: Int = 0

    @get:Qualifier("IAM")
    @get:Bean
    val s3Client: AmazonS3
        get() {
            val providerChain = AWSCredentialsProviderChain(
                    InstanceProfileCredentialsProvider.getInstance(),
                    ProfileCredentialsProvider()
            )
            val clientConfiguration = ClientConfiguration()
            clientConfiguration.connectionTimeout = timeout
            return AmazonS3ClientBuilder.standard().withRegion(Regions.AP_SOUTH_1)
                    .withCredentials(providerChain)
                    .withClientConfiguration(clientConfiguration)
                    .build()
        }
}
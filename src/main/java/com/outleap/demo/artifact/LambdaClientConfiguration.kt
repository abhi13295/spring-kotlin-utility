package com.outleap.demo.artifact

import com.amazonaws.ClientConfiguration
import com.amazonaws.auth.AWSCredentialsProviderChain
import com.amazonaws.auth.InstanceProfileCredentialsProvider
import com.amazonaws.auth.profile.ProfileCredentialsProvider
import com.amazonaws.regions.Regions
import com.amazonaws.services.lambda.AWSLambda
import com.amazonaws.services.lambda.AWSLambdaClientBuilder
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class LambdaClientConfiguration {

    @Value("\${AWS_S3_TIMEOUT:20000}")
    val timeout: Int = 0

    @get:Qualifier("IAM")
    @get:Bean
    val awsLambda: AWSLambda
        get() {
            val providerChain = AWSCredentialsProviderChain(
                InstanceProfileCredentialsProvider.getInstance(),
                ProfileCredentialsProvider()
            )
            val clientConfiguration = ClientConfiguration()
            clientConfiguration.connectionTimeout = timeout
            return AWSLambdaClientBuilder.standard().withRegion(Regions.AP_SOUTH_1)
                .withCredentials(providerChain)
                .withClientConfiguration(clientConfiguration)
                .build()
        }

}
package com.outleap.demo

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication(scanBasePackages = ["com.outleap.demo", "com.outleap.notification"])
class Application
fun main(args: Array<String>) {
    runApplication<Application>(*args)
}
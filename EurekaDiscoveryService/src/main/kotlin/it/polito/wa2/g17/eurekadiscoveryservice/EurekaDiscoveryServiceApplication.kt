package it.polito.wa2.g17.eurekadiscoveryservice

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer

@SpringBootApplication
@EnableEurekaServer
class EurekaDiscoveryServiceApplication

fun main(args: Array<String>) {
    runApplication<EurekaDiscoveryServiceApplication>(*args)
}

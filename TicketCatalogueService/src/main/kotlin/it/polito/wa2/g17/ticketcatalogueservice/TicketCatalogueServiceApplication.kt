package it.polito.wa2.g17.ticketcatalogueservice

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.cloud.client.loadbalancer.LoadBalanced
import org.springframework.cloud.netflix.eureka.EnableEurekaClient
import org.springframework.context.annotation.Bean
import org.springframework.web.reactive.function.client.WebClient

@SpringBootApplication
@EnableEurekaClient
class TicketCatalogueServiceApplication{
    @Bean
    @LoadBalanced
    fun loadBalancedWebClient(): WebClient.Builder {
        return WebClient.builder()
    }
}

fun main(args: Array<String>) {
    runApplication<TicketCatalogueServiceApplication>(*args)
}

package it.polito.wa2.g17.gateway.configs

import org.springframework.cloud.gateway.route.RouteLocator
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class GatewayConfig {

    @Bean
    fun gatewayRoutes(routeLocatorBuilder: RouteLocatorBuilder) : RouteLocator {
        return routeLocatorBuilder.routes()
            .route {
                it.path("/auth/**")
                    .uri("lb://LOGIN-SERVICE")
            }
            .route {
                it.path("/payment/**")
                    .uri("lb://PAYMENT-SERVICE")
            }
            .route {
                it.path("/ticket/catalogue/**")
                    .uri("lb://TICKET-CATALOGUE-SERVICE")
            }
            .route {
                it.path("/ticket/validation/**")
                    .uri("lb://TICKET-VALIDATION-SERVICE")
            }
            .route {
                it.path("/traveler/**")
                    .uri("lb://TRAVELER-SERVICE")
            }
            .route {
                it.path("/turnstile/**")
                    .uri("lb://TURNSTILE-SERVICE")
            }
            .build()

    }

}
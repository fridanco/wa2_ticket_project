package it.polito.wa2.g17.ticketcatalogueservice.kafka

import org.apache.kafka.clients.admin.AdminClientConfig
import org.apache.kafka.clients.admin.NewTopic
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.kafka.core.KafkaAdmin


@Configuration
class KafkaConfig (
    @Value("\${spring.kafka.bootstrap-servers}")
    private val servers: String,
    @Value("\${spring.kafka.template.default-topic}")
    private val topic: String
    )
{

    private val log = LoggerFactory.getLogger(javaClass)
    @Bean
    fun kafkaAdmin(): KafkaAdmin {
        val configs: MutableMap<String,Any?> = HashMap()
        configs[AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG] = servers
        return KafkaAdmin(configs)
    }

    @Bean
    fun topic_ticketCatalogueServiceToPaymentService(): NewTopic {
        return NewTopic("ticketCatalogueService_paymentService", 1, 1.toShort())
    }

    @Bean
    fun topic_ticketCatalogueServiceToTravelerService(): NewTopic {
        return NewTopic("ticketCatalogueService_travelerService", 1, 1.toShort())
    }

}

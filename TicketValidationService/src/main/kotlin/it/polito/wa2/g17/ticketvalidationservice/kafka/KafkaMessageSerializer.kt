package it.polito.wa2.g17.ticketvalidationservice.kafka

import com.fasterxml.jackson.databind.ObjectMapper
import org.apache.kafka.common.serialization.Serializer

class KafkaMessageSerializer<T> : Serializer<T> {

    private val objectMapper = ObjectMapper()

    override fun serialize(topic: String?, data: T): ByteArray {
        return objectMapper.writeValueAsBytes(data)
    }

}
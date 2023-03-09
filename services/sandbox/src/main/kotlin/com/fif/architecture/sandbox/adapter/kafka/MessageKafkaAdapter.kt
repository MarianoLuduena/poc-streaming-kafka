package com.fif.architecture.sandbox.adapter.kafka

import com.fif.architecture.sandbox.adapter.kafka.model.PersonKafkaModel
import com.fif.architecture.sandbox.application.port.out.SendMessageOutPort
import com.fif.architecture.sandbox.config.Config
import com.fif.architecture.sandbox.domain.Person
import org.apache.avro.generic.GenericRecord
import org.slf4j.LoggerFactory
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.stereotype.Repository

@Repository
class MessageKafkaAdapter(
    private val kafkaTemplate: KafkaTemplate<String, GenericRecord>,
    private val config: Config
) : SendMessageOutPort {

    override fun send(person: Person) {
        // PLAIN JSON
        /*val model = PersonKafkaModel.toJsonModel(person)
        LOG.info("Sending message with model {}", model)
        kafkaTemplate
            .send("person", model.documentNumber, model)
            .addCallback(
                { LOG.info("Successfully sent message {}", it) },
                { LOG.error("Error sending message", it) }
            )*/

        // AVRO
        val model = PersonKafkaModel.toAvroModel(person)
        LOG.info("Sending message with model {}", model)
        kafkaTemplate
            .send(config.producer!!.topic, person.documentNumber, model)
            .addCallback(
                { LOG.info("Successfully sent message {}", it) },
                { LOG.error("Error sending message", it) }
            )
    }

    companion object {
        private val LOG = LoggerFactory.getLogger(this::class.java)
    }

}

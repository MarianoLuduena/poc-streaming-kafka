package com.fif.architecture.sandbox.adapter.kafka.model

import com.fasterxml.jackson.annotation.JsonFormat
import com.fasterxml.jackson.databind.PropertyNamingStrategies
import com.fasterxml.jackson.databind.annotation.JsonNaming
import com.fif.architecture.sandbox.domain.Person
import org.apache.avro.Schema
import org.apache.avro.generic.GenericData
import java.time.LocalDate

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy::class)
data class PersonKafkaModel(
    val documentType: String,
    val documentNumber: String,
    val firstName: String,
    val surname: String,
    @JsonFormat(pattern = "yyyy-MM-dd")
    val birthDate: LocalDate?
) {

    companion object {
        private val AVRO_SCHEMA = Schema.Parser().parse(
            """{
                "type": "record",
                "name": "Person",
                "namespace": "com.fif.architecture.sandbox",
                "fields": [{
                    "name": "birth_date",
                    "type": ["null", "string"],
                    "default": null
                }, {
                    "name": "document_number",
                    "type": "string"
                }, {
                    "name": "document_type",
                    "type": "string"
                }, {
                    "name": "first_name",
                    "type": "string"
                }, {
                    "name": "surname",
                    "type": "string"
                }]
            }""".trimIndent()
        )

        fun toJsonModel(domain: Person): PersonKafkaModel =
            PersonKafkaModel(
                documentType = domain.documentType,
                documentNumber = domain.documentNumber,
                firstName = domain.firstName,
                surname = domain.surname,
                birthDate = domain.birthDate
            )

        fun toAvroModel(domain: Person): GenericData.Record {
            val record = GenericData.Record(AVRO_SCHEMA)
            record.put("document_type", domain.documentType)
            record.put("document_number", domain.documentNumber)
            record.put("first_name", domain.firstName)
            record.put("surname", domain.surname)
            domain.birthDate?.also { record.put("birth_date", it.toString()) }
            return record
        }
    }

}

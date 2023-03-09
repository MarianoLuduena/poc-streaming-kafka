package com.fif.architecture.sandbox.adapter.controller.model

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.databind.PropertyNamingStrategies
import com.fasterxml.jackson.databind.annotation.JsonNaming
import com.fif.architecture.sandbox.domain.Person
import java.time.LocalDate

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy::class)
@JsonIgnoreProperties(ignoreUnknown = true)
data class PersonControllerModel(
    val documentType: String,
    val documentNumber: String,
    val firstName: String,
    val surname: String,
    val birthDate: LocalDate?
) {

    fun toDomain(): Person =
        Person(
            documentType = documentType,
            documentNumber = documentNumber,
            firstName = firstName,
            surname = surname,
            birthDate = birthDate
        )

}

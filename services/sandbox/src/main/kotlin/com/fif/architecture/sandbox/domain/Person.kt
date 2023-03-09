package com.fif.architecture.sandbox.domain

import java.time.LocalDate

data class Person(
    val documentType: String,
    val documentNumber: String,
    val firstName: String,
    val surname: String,
    val birthDate: LocalDate?
)

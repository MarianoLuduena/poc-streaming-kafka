package com.fif.architecture.sandbox.application.port.`in`

import com.fif.architecture.sandbox.domain.Person

interface SendMessageInPort {

    fun execute(person: Person)

}

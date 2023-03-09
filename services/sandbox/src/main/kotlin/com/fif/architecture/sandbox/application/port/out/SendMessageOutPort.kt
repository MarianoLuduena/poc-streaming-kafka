package com.fif.architecture.sandbox.application.port.out

import com.fif.architecture.sandbox.domain.Person

interface SendMessageOutPort {

    fun send(person: Person)

}

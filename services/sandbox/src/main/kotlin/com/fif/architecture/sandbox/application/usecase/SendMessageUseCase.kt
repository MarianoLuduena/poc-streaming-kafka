package com.fif.architecture.sandbox.application.usecase

import com.fif.architecture.sandbox.application.port.`in`.SendMessageInPort
import com.fif.architecture.sandbox.application.port.out.SendMessageOutPort
import com.fif.architecture.sandbox.domain.Person
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component

@Component
class SendMessageUseCase(private val sendMessageOutPort: SendMessageOutPort) : SendMessageInPort {

    override fun execute(person: Person) {
        LOG.info("Sending message with {}", person)
        sendMessageOutPort.send(person)
    }

    companion object {
        private val LOG = LoggerFactory.getLogger(this::class.java)
    }

}

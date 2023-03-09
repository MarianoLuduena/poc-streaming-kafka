package com.fif.architecture.sandbox.adapter.controller

import com.fif.architecture.sandbox.adapter.controller.model.PersonControllerModel
import com.fif.architecture.sandbox.application.port.`in`.SendMessageInPort
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController

@RestController
@Validated
class MessageControllerAdapter(
    private val sendMessageInPort: SendMessageInPort
) {

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PostMapping("/messages")
    fun produce(@Validated @RequestBody body: PersonControllerModel) {
        LOG.info("Call to POST /messages with body {}", body)
        sendMessageInPort.execute(body.toDomain())
        LOG.info("Response to POST /messages OK")
    }

    companion object {
        private val LOG = LoggerFactory.getLogger(this::class.java)
    }

}

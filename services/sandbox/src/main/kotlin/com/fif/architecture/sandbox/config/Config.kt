package com.fif.architecture.sandbox.config

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.stereotype.Component
import org.springframework.validation.annotation.Validated
import javax.validation.constraints.NotBlank
import javax.validation.constraints.NotNull

@Validated
@Component
@ConfigurationProperties(prefix = "sandbox")
class Config {

    @get:NotNull
    var producer: Producer? = null

    class Producer {
        @get:NotBlank
        lateinit var topic: String
    }

}

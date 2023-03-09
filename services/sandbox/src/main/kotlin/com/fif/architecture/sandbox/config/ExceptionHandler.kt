package com.fif.architecture.sandbox.config

import brave.Tracer
import com.fasterxml.jackson.databind.PropertyNamingStrategies
import com.fasterxml.jackson.databind.annotation.JsonNaming
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import java.time.ZoneOffset
import java.time.ZonedDateTime
import javax.servlet.http.HttpServletRequest

@ControllerAdvice
class ExceptionHandler(
    private val httpServletRequest: HttpServletRequest,
    private val tracer: Tracer?
) {

    private val log = LoggerFactory.getLogger(this::class.java)

    @ExceptionHandler(Throwable::class)
    fun handle(ex: Throwable): ResponseEntity<ApiErrorResponse> {
        log.error(HttpStatus.INTERNAL_SERVER_ERROR.reasonPhrase, ex)
        return buildResponseError(HttpStatus.INTERNAL_SERVER_ERROR, ex, AppError.INTERNAL_ERROR.errorCode)
    }

    private fun buildResponseError(
        httpStatus: HttpStatus,
        ex: Throwable,
        errorCode: Int,
        errorMessage: String = ex.message ?: ""
    ): ResponseEntity<ApiErrorResponse> {
        val traceId = tracer
            ?.currentSpan()
            ?.context()
            ?.traceIdString()
            ?: TraceSleuthInterceptor.TRACE_ID_MISSING

        val spanId = tracer
            ?.currentSpan()
            ?.context()
            ?.spanIdString()
            ?: TraceSleuthInterceptor.SPAN_ID_MISSING

        val apiErrorResponse = ApiErrorResponse(
            timestamp = ZonedDateTime.now().withZoneSameInstant(ZoneOffset.UTC),
            name = httpStatus.reasonPhrase,
            detail = errorMessage,
            status = httpStatus.value(),
            code = errorCode,
            resource = httpServletRequest.requestURI,
            metadata = Metadata(xB3TraceId = traceId, xB3SpanId = spanId)
        )

        return ResponseEntity(apiErrorResponse, httpStatus)
    }

    @JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy::class)
    data class Metadata(
        val xB3TraceId: String,
        val xB3SpanId: String
    )

    @JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy::class)
    data class ApiErrorResponse(
        val name: String,
        val status: Int,
        val timestamp: ZonedDateTime,
        val code: Int,
        val resource: String,
        val detail: String,
        val metadata: Metadata
    )

}

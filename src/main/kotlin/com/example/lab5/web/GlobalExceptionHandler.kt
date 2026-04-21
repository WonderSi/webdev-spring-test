package com.example.lab5.web

import com.example.lab5.domain.exception.*
import com.example.lab5.web.dto.ErrorResponse
import com.example.lab5.web.dto.ValidationErrorResponse
import io.github.oshai.kotlinlogging.KotlinLogging
import jakarta.validation.ConstraintViolationException
import org.springframework.dao.DataIntegrityViolationException
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.http.converter.HttpMessageNotReadableException
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice

@RestControllerAdvice
class GlobalExceptionHandler {

    private val logger = KotlinLogging.logger {}

    @ExceptionHandler(AppException::class)
    fun handleAppException(e: AppException): ResponseEntity<ErrorResponse> {
        val status = when (e) {
            is NotFoundException          -> HttpStatus.NOT_FOUND    // 404
            is AlreadyExistsException     -> HttpStatus.CONFLICT     // 409
            is InvalidOrderStateException -> HttpStatus.BAD_REQUEST  // 400
            is BadRequestException        -> HttpStatus.BAD_REQUEST
        }
        logger.warn { "${e::class.simpleName}: ${e.message}" }
        return ResponseEntity
            .status(status)
            .body(ErrorResponse(status.value(), e.message))
    }

    @ExceptionHandler(MethodArgumentNotValidException::class)
    fun handleValidation(ex: MethodArgumentNotValidException): ResponseEntity<ValidationErrorResponse> {
        val errors = ex.bindingResult.fieldErrors.associate {
            it.field to (it.defaultMessage ?: "Incorrect value")
        }
        logger.warn { "Validation error: $errors" }
        return ResponseEntity
            .status(HttpStatus.BAD_REQUEST)
            .body(ValidationErrorResponse(400, "Validation error", errors))
    }

    @ExceptionHandler(ConstraintViolationException::class)
    fun handleConstraintViolation(e: ConstraintViolationException): ResponseEntity<ErrorResponse> {
        logger.warn { "ConstraintViolation: ${e.message}" }
        return ResponseEntity
            .status(HttpStatus.BAD_REQUEST)
            .body(ErrorResponse(400, e.message))
    }

    @ExceptionHandler(HttpMessageNotReadableException::class)
    fun handleNotReadable(ex: HttpMessageNotReadableException): ResponseEntity<ErrorResponse> {
        return ResponseEntity
            .status(HttpStatus.BAD_REQUEST)
            .body(ErrorResponse(400, "Invalid request body"))
    }

    @ExceptionHandler(Exception::class)
    fun handleUnexpected(e: Exception): ResponseEntity<ErrorResponse> {
        logger.error(e) { "Unexpected error" }
        return ResponseEntity
            .status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body(ErrorResponse(500, "Internal server error"))
    }

    @ExceptionHandler(DataIntegrityViolationException::class)
    fun handleDataIntegrity(e: DataIntegrityViolationException): ResponseEntity<ErrorResponse> {
        logger.warn { "Data integrity violation: ${e.message}" }
        return ResponseEntity
            .status(HttpStatus.CONFLICT)
            .body(ErrorResponse(409, "Resource already exists"))
    }
}
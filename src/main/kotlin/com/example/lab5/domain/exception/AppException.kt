package com.example.lab5.domain.exception

sealed class AppException(message: String) : RuntimeException(message)

class NotFoundException(message: String) : AppException(message)

class AlreadyExistsException(message: String) : AppException(message)

class InvalidOrderStateException(message: String) : AppException(message)

class BadRequestException(message: String) : AppException(message)
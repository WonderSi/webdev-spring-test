package com.example.lab5.web.dto

import com.example.lab5.domain.model.User
import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank

data class UserCreateRequest(
    @field:Email(message = "Invalid email format")
    @field:NotBlank(message = "Email must not be blank")
    val email: String? = null,

    @field:NotBlank(message = "First name must not be blank")
    val firstName: String? = null,

    @field:NotBlank(message = "Last name must not be blank")
    val lastName: String? = null,

    val isActive: Boolean = true
)

data class UserUpdateRequest(
    @field:Email(message = "Invalid email format")
    @field:NotBlank(message = "Email must not be blank")
    val email: String? = null,

    @field:NotBlank(message = "First name must not be blank")
    val firstName: String? = null,

    @field:NotBlank(message = "Last name must not be blank")
    val lastName: String? = null,

    val isActive: Boolean = false
)

data class UserResponse(
    val id: Long,
    val email: String,
    val firstName: String,
    val lastName: String,
    val isActive: Boolean
)

fun User.toResponse() = UserResponse(
    id = id,
    email = email,
    firstName = firstName,
    lastName = lastName,
    isActive = isActive
)

fun UserCreateRequest.toDomain() = User(
    email = email ?: "",
    firstName = firstName ?: "",
    lastName = lastName ?: "",
    isActive = isActive
)

fun UserUpdateRequest.toDomain() = User(
    email = email ?: "",
    firstName = firstName ?: "",
    lastName = lastName ?: "",
    isActive = isActive
)

package com.example.lab5.web.controller

import com.example.lab5.application.service.UserService
import com.example.lab5.web.dto.*
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/v1/users")
@Validated
class UserController(
    private val userService: UserService
) {
    @GetMapping
    fun listUsers(): ResponseEntity<List<UserResponse>> =
        ResponseEntity.ok(userService.findAll().map { it.toResponse() })

    @GetMapping("/{id}")
    fun getUserById(@PathVariable id: Long): ResponseEntity<UserResponse> =
        ResponseEntity.ok(userService.findById(id).toResponse())

    @PostMapping
    fun createUser(@Valid @RequestBody request: UserCreateRequest): ResponseEntity<UserResponse> {
        val (user, isCreated) = userService.create(request.toDomain())
        return if (isCreated) {
            ResponseEntity.status(HttpStatus.CREATED).body(user.toResponse())
        } else {
            ResponseEntity.ok(user.toResponse())
        }
    }

    @PutMapping("/{id}")
    fun updateUser(
        @PathVariable id: Long,
        @Valid @RequestBody request: UserUpdateRequest
    ): ResponseEntity<UserResponse> =
        ResponseEntity.ok(userService.update(id, request.toDomain()).toResponse())

    @DeleteMapping("/{id}")
    fun deleteUser(@PathVariable id: Long): ResponseEntity<Void> {
        userService.delete(id)
        return ResponseEntity.noContent().build()
    }
}
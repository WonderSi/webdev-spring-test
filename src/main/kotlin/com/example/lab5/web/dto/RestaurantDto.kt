package com.example.lab5.web.dto

import com.example.lab5.domain.model.Restaurant
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size

data class RestaurantCreateRequest(
    @field:NotBlank(message = "Name must not be blank")
    @field:Size(min = 2, max = 100, message = "Name: 2 to 100 characters")
    val name: String,

    @field:NotBlank(message = "Address must not be blank")
    val address: String
)

data class RestaurantUpdateRequest(
    @field:NotBlank(message = "Name must not be blank")
    @field:Size(min = 2, max = 100, message = "Name: 2 to 100 characters")
    val name: String,

    @field:NotBlank(message = "Address must not be blank")
    val address: String
)

data class RestaurantResponse(
    val id: Long,
    val name: String,
    val address: String
)

fun Restaurant.toResponse() = RestaurantResponse(id = id, name = name, address = address)

fun RestaurantCreateRequest.toDomain() = Restaurant(name = name, address = address)

fun RestaurantUpdateRequest.toDomain() = Restaurant(name = name, address = address)
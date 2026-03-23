package com.example.lab5.web.dto

import com.example.lab5.domain.model.Dish
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Positive
import java.math.BigDecimal

data class DishCreateRequest(
    @field:NotBlank(message = "Name must not be blank")
    val name: String? = null,

    val description: String? = null,

    @field:Positive(message = "Price must be greater than 0")
    val price: BigDecimal? = null,

    val isAvailable: Boolean = true
)

data class DishUpdateRequest(
    @field:NotBlank(message = "Name must not be blank")
    val name: String? = null,

    val description: String? = null,

    @field:Positive(message = "Price must be greater than 0")
    val price: BigDecimal? = null,

    val isAvailable: Boolean = true
)

data class DishResponse(
    val id: Long,
    val name: String,
    val description: String,
    val price: BigDecimal,
    val isAvailable: Boolean,
    val restaurantId: Long
)

fun Dish.toResponse() = DishResponse(
    id = id,
    name = name,
    description = description,
    price = price,
    isAvailable = isAvailable,
    restaurantId = restaurantId ?: 0
)

fun DishCreateRequest.toDomain() = Dish(
    name = name ?: "",
    description = description ?: "",
    price = price ?: BigDecimal.ZERO,
    isAvailable = isAvailable
)

fun DishUpdateRequest.toDomain() = Dish(
    name = name ?: "",
    description = description ?: "",
    price = price ?: BigDecimal.ZERO,
    isAvailable = isAvailable
)

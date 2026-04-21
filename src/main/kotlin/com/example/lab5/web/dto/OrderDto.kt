package com.example.lab5.web.dto

import com.example.lab5.domain.model.Order
import com.example.lab5.domain.model.OrderStatus
import jakarta.validation.constraints.NotEmpty
import jakarta.validation.constraints.NotNull
import java.time.LocalDateTime

data class OrderCreateRequest(
    @field:NotNull(message = "userId is required")
    val userId: Long? = null,

    @field:NotEmpty(message = "Order must contain at least one dish")
    val dishIds: List<Long>? = emptyList()
)

data class OrderStatusUpdateRequest(
    val status: OrderStatus? = null
)

data class OrderResponse(
    val id: Long,
    val userId: Long,
    val status: OrderStatus,
    val createdAt: LocalDateTime,
    val dishes: List<DishResponse>
)

fun Order.toResponse() = OrderResponse(
    id = id,
    userId = userId,
    status = status,
    createdAt = createdAt,
    dishes = dishes.map { it.toResponse() }
)

fun OrderCreateRequest.toDomain() = Order(
    userId = userId!!,
    status = OrderStatus.PENDING
)

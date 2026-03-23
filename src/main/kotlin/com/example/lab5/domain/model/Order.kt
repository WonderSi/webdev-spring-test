package com.example.lab5.domain.model

import java.time.LocalDateTime

data class Order(
    val id: Long = 0,
    val userId: Long,
    val status: OrderStatus = OrderStatus.PENDING,
    val createdAt: LocalDateTime = LocalDateTime.now(),
    val dishes: List<Dish> = emptyList()
)
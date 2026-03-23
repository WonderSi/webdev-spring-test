package com.example.lab5.domain.port

import com.example.lab5.domain.model.Order
import com.example.lab5.domain.model.OrderStatus

interface OrderRepositoryPort {
    fun findAll(userId: Long?, status: OrderStatus?): List<Order>
    fun findById(id: Long): Order?
    fun save(order: Order): Order
    fun update(order: Order): Order
    fun delete(id: Long)
}

package com.example.lab5.application.service

import com.example.lab5.domain.exception.BadRequestException
import com.example.lab5.domain.exception.InvalidOrderStateException
import com.example.lab5.domain.exception.NotFoundException
import com.example.lab5.domain.model.Dish
import com.example.lab5.domain.model.Order
import com.example.lab5.domain.model.OrderStatus
import com.example.lab5.domain.port.OrderRepositoryPort
import com.example.lab5.domain.port.UserRepositoryPort
import com.example.lab5.infrastructure.jpa.repository.DishJpaRepository
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.stereotype.Service

@Service
class OrderService(
    private val orderRepositoryPort: OrderRepositoryPort,
    private val dishJpaRepository: DishJpaRepository,
    private val userRepositoryPort: UserRepositoryPort
) {
    private val logger = KotlinLogging.logger {}

    fun findAll(userId: Long?, status: OrderStatus?): List<Order> =
        orderRepositoryPort.findAll(userId, status)

    fun findById(id: Long): Order {
        logger.info { "Fetching order id=$id" }
        return orderRepositoryPort.findById(id)
            ?: throw NotFoundException("Order with id=$id not found")
    }

    fun create(userId: Long, dishIds: List<Long>): Order {
        userRepositoryPort.findById(userId)
            ?: throw BadRequestException("User with id=$userId not found")

        val dishes = dishJpaRepository.findAllById(dishIds)
        if (dishes.size != dishIds.distinct().size) {
            throw BadRequestException("Some dishes not found")
        }
        val order = Order(
            userId = userId,
            dishes = dishes.map {
                Dish(
                    id = it.id,
                    name = it.name,
                    description = it.description,
                    price = it.price,
                    isAvailable = it.isAvailable,
                    restaurantId = it.restaurant.id
                )
            }
        )
        val created = orderRepositoryPort.save(order)
        logger.info { "Created order id=${created.id}, userId=$userId" }
        return created
    }
    
    fun delete(id: Long) {
        logger.info { "Deleting order id=$id" }
        findById(id)
        orderRepositoryPort.delete(id)
        logger.info { "Deleted order id=$id" }
    }

    fun updateStatus(id: Long, status: OrderStatus): Order {
        val order = findById(id)
        validateStatusTransition(order.status, status)
        val updated = orderRepositoryPort.update(order.copy(status = status))
        logger.info { "Order id=$id status: ${order.status} -> $status" }
        return updated
    }

    private fun validateStatusTransition(current: OrderStatus, next: OrderStatus) {
        val allowed = mapOf(
            OrderStatus.PENDING   to setOf(OrderStatus.CONFIRMED, OrderStatus.CANCELLED),
            OrderStatus.CONFIRMED to setOf(OrderStatus.DELIVERED, OrderStatus.CANCELLED),
            OrderStatus.DELIVERED to emptySet(),
            OrderStatus.CANCELLED to emptySet()
        )
        if (next !in allowed[current]!!) {
            throw InvalidOrderStateException("Invalid status transition from $current to $next")
        }
    }
}

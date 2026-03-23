package com.example.lab5.infrastructure.jpa.adapter

import com.example.lab5.domain.model.Dish
import com.example.lab5.domain.model.Order
import com.example.lab5.domain.model.OrderStatus
import com.example.lab5.domain.port.OrderRepositoryPort
import com.example.lab5.infrastructure.jpa.entity.OrderEntity
import com.example.lab5.infrastructure.jpa.repository.DishJpaRepository
import com.example.lab5.infrastructure.jpa.repository.OrderJpaRepository
import com.example.lab5.infrastructure.jpa.repository.UserJpaRepository
import org.springframework.stereotype.Component

@Component
class OrderJpaAdapter(
    private val orderJpaRepository: OrderJpaRepository,
    private val userJpaRepository: UserJpaRepository,
    private val dishJpaRepository: DishJpaRepository
) : OrderRepositoryPort {

    override fun findAll(userId: Long?, status: OrderStatus?): List<Order> {
        val entities = when {
            userId != null && status != null ->
                orderJpaRepository.findAllByUserIdAndStatusOrderByCreatedAtDesc(userId, status)
            userId != null ->
                orderJpaRepository.findAllByUserIdOrderByCreatedAtDesc(userId)
            status != null ->
                orderJpaRepository.findAllByStatusOrderByCreatedAtDesc(status)
            else ->
                orderJpaRepository.findAllByOrderByCreatedAtDesc()
        }
        return entities.map { it.toDomain() }
    }

    override fun findById(id: Long): Order? =
        orderJpaRepository.findByIdWithDetails(id)?.toDomain()

    override fun save(order: Order): Order {
        val user = userJpaRepository.findById(order.userId)
            .orElseThrow { IllegalArgumentException("User with id=${order.userId} not found") }

        val dishes = dishJpaRepository.findAllById(order.dishes.map { it.id }).toMutableSet()
        if (dishes.size != order.dishes.size) {
            throw IllegalArgumentException("Some dishes not found")
        }

        val entity = OrderEntity(
            user = user,
            status = order.status,
            dishes = dishes
        )
        return orderJpaRepository.save(entity).toDomain()
    }

    override fun update(order: Order): Order {
        val existing = orderJpaRepository.findByIdWithDetails(order.id)
            ?: throw NoSuchElementException("Order with id=${order.id} not found")

        existing.status = order.status
        return orderJpaRepository.save(existing).toDomain()
    }

    override fun delete(id: Long) {
        orderJpaRepository.deleteById(id)
    }
}

fun OrderEntity.toDomain() = Order(
    id = id,
    userId = user.id,
    status = status,
    createdAt = createdAt,
    dishes = dishes.map { dish ->
        Dish(
            id = dish.id,
            name = dish.name,
            description = dish.description,
            price = dish.price,
            isAvailable = dish.isAvailable,
            restaurantId = dish.restaurant.id
        )
    }
)

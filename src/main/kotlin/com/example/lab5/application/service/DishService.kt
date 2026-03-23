package com.example.lab5.application.service

import com.example.lab5.domain.exception.AlreadyExistsException
import com.example.lab5.domain.exception.NotFoundException
import com.example.lab5.domain.model.Dish
import com.example.lab5.domain.port.DishRepositoryPort
import com.example.lab5.infrastructure.jpa.repository.OrderJpaRepository
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class DishService(
    private val dishRepositoryPort: DishRepositoryPort,
    private val orderJpaRepository: OrderJpaRepository
) {
    private val logger = KotlinLogging.logger {}

    fun findAll(namePart: String?): List<Dish> =
        if (namePart != null) {
            dishRepositoryPort.findAllByNamePart(namePart)
        } else {
            dishRepositoryPort.findAll()
        }

    fun findById(id: Long): Dish {
        logger.info { "Fetching dish id=$id" }
        return dishRepositoryPort.findById(id)
            ?: throw NotFoundException("Dish with id=$id not found")
    }

    fun create(dish: Dish): Pair<Dish, Boolean> {
        val existing = dishRepositoryPort.findByName(dish.name)
        if (existing != null) {
            throw AlreadyExistsException("Dish '${dish.name}' already exists")
        }
        val created = dishRepositoryPort.save(dish)
        logger.info { "Created dish id=${created.id}, name=${created.name}" }
        return Pair(created, true)
    }

    fun update(id: Long, dish: Dish): Dish {
        dishRepositoryPort.findById(id)
            ?: throw NotFoundException("Dish with id=$id not found")
        return dishRepositoryPort.update(dish.copy(id = id))
    }

    @Transactional
    fun delete(id: Long) {
        val orders = orderJpaRepository.findAll()
        orders.forEach { order ->
            order.dishes.removeIf { it.id == id }
            orderJpaRepository.save(order)
        }
        val deleted = dishRepositoryPort.deleteById(id)
        if (!deleted) throw NotFoundException("Dish with id=$id not found")
        logger.info { "Deleted dish id=$id" }
    }
}
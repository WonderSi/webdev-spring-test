package com.example.lab5.application.service

import com.example.lab5.domain.exception.AlreadyExistsException
import com.example.lab5.domain.exception.NotFoundException
import com.example.lab5.domain.model.Dish
import com.example.lab5.domain.model.Restaurant
import com.example.lab5.domain.port.RestaurantRepositoryPort
import com.example.lab5.infrastructure.jpa.entity.DishEntity
import com.example.lab5.infrastructure.jpa.repository.DishJpaRepository
import com.example.lab5.infrastructure.jpa.repository.RestaurantJpaRepository
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.stereotype.Service

@Service
class RestaurantService(
    private val restaurantRepositoryPort: RestaurantRepositoryPort,
    private val restaurantJpaRepository: RestaurantJpaRepository,
    private val dishJpaRepository: DishJpaRepository
) {
    private val logger = KotlinLogging.logger {}

    fun findAll(): List<Restaurant> = restaurantRepositoryPort.findAll()

    fun findById(id: Long): Restaurant {
        logger.info { "Fetching restaurant id=$id" }
        return restaurantRepositoryPort.findById(id)
            ?: throw NotFoundException("Restaurant with id=$id not found")
    }

    fun create(restaurant: Restaurant): Restaurant {
        if (restaurantJpaRepository.existsByNameIgnoreCase(restaurant.name)) {
            throw AlreadyExistsException("Restaurant '${restaurant.name}' already exists")
        }
        val created = restaurantRepositoryPort.save(restaurant)
        logger.info { "Created restaurant id=${created.id}, name=${created.name}" }
        return created
    }

    fun update(id: Long, restaurant: Restaurant): Restaurant {
        restaurantRepositoryPort.findById(id)
            ?: throw NotFoundException("Restaurant with id=$id not found")

        val existing = restaurantJpaRepository.findByNameIgnoreCase(restaurant.name)
        if (existing != null && existing.id != id) {
            throw AlreadyExistsException("Restaurant '${restaurant.name}' already exists")
        }

        return restaurantRepositoryPort.update(restaurant.copy(id = id))
    }

    fun delete(id: Long) {
        val deleted = restaurantRepositoryPort.deleteById(id)
        if (!deleted) throw NotFoundException("Restaurant with id=$id not found")
        logger.info { "Deleted restaurant id=$id" }
    }

    fun getMenu(restaurantId: Long): List<Dish> {
        val restaurant = restaurantJpaRepository.findById(restaurantId)
            .orElseThrow { NotFoundException("Restaurant with id=$restaurantId not found") }
        return restaurant.dishes.map { dish ->
            Dish(
                id = dish.id,
                name = dish.name,
                description = dish.description,
                price = dish.price,
                isAvailable = dish.isAvailable,
                restaurantId = dish.restaurant.id
            )
        }
    }

    fun addDish(restaurantId: Long, dish: Dish): Dish {
        val restaurant = restaurantJpaRepository.findById(restaurantId)
            .orElseThrow { NotFoundException("Restaurant with id=$restaurantId not found") }
        val entity = DishEntity(
            name = dish.name,
            description = dish.description,
            price = dish.price,
            isAvailable = dish.isAvailable,
            restaurant = restaurant
        )
        val saved = dishJpaRepository.save(entity)
        logger.info { "Added dish id=${saved.id} to restaurant id=$restaurantId" }
        return Dish(
            id = saved.id,
            name = saved.name,
            description = saved.description,
            price = saved.price,
            isAvailable = saved.isAvailable,
            restaurantId = saved.restaurant.id
        )
    }
}
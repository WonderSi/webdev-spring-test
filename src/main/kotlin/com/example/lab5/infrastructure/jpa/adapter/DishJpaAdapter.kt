package com.example.lab5.infrastructure.jpa.adapter

import com.example.lab5.domain.model.Dish
import com.example.lab5.domain.port.DishRepositoryPort
import com.example.lab5.infrastructure.jpa.entity.DishEntity
import com.example.lab5.infrastructure.jpa.repository.DishJpaRepository
import com.example.lab5.infrastructure.jpa.repository.RestaurantJpaRepository
import org.springframework.stereotype.Component

@Component
class DishJpaAdapter(
    private val dishJpaRepository: DishJpaRepository,
    private val restaurantJpaRepository: RestaurantJpaRepository
) : DishRepositoryPort {

    override fun findAll(): List<Dish> =
        dishJpaRepository.findAll().map { it.toDomain() }

    override fun findAllByNamePart(namePart: String): List<Dish> =
        dishJpaRepository.findByNameContaining(namePart).map { it.toDomain() }

    override fun findById(id: Long): Dish? =
        dishJpaRepository.findById(id).orElse(null)?.toDomain()

    override fun findByName(name: String): Dish? =
        dishJpaRepository.findByName(name)?.toDomain()

    override fun save(dish: Dish): Dish {
        val restaurant = dish.restaurantId?.let {
            restaurantJpaRepository.findById(it).orElseThrow {
                NoSuchElementException("Restaurant not found: $it")
            }
        } ?: throw IllegalArgumentException("restaurantId is required")
        return dishJpaRepository.save(DishEntity.fromDomain(dish, restaurant)).toDomain()
    }

    override fun update(dish: Dish): Dish {
        // При обновлении берём ресторан из существующего блюда
        val existing = dishJpaRepository.findById(dish.id)
            .orElseThrow { NoSuchElementException("Dish with id=${dish.id} not found") }
        val updated = DishEntity(
            id = existing.id,
            name = dish.name,
            description = dish.description,
            price = dish.price,
            isAvailable = dish.isAvailable,
            restaurant = existing.restaurant  // берём из существующего
        )
        return dishJpaRepository.save(updated).toDomain()
    }

    override fun deleteById(id: Long): Boolean {
        return if (dishJpaRepository.existsById(id)) {
            dishJpaRepository.deleteById(id)
            true
        } else {
            false
        }
    }
}
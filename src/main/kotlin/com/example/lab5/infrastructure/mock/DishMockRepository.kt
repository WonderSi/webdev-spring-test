package com.example.lab5.infrastructure.mock

import com.example.lab5.domain.model.Dish
import com.example.lab5.domain.port.DishRepositoryPort
import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Component

@Component
@Profile("mock")
class DishMockRepository : DishRepositoryPort {

    private val storage = mutableMapOf<Long, Dish>()
    private var seq = 1L

    override fun findAll(): List<Dish> = storage.values.toList()

    override fun findAllByNamePart(namePart: String): List<Dish> =
        storage.values.filter {
            it.name.contains(namePart, ignoreCase = true)
        }

    override fun findById(id: Long): Dish? = storage[id]

    override fun findByName(name: String): Dish? =
        storage.values.firstOrNull { it.name == name }

    override fun save(dish: Dish): Dish {
        val saved = dish.copy(id = seq++)
        storage[saved.id] = saved
        return saved
    }

    override fun update(dish: Dish): Dish {
        storage[dish.id] = dish
        return dish
    }

    override fun deleteById(id: Long): Boolean {
        return storage.remove(id) != null
    }
}
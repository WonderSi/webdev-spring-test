package com.example.lab5.domain.port

import com.example.lab5.domain.model.Dish

interface DishRepositoryPort {
    fun findAll(): List<Dish>
    fun findAllByNamePart(namePart: String): List<Dish>
    fun findById(id: Long): Dish?
    fun findByName(name: String): Dish?
    fun save(dish: Dish): Dish
    fun update(dish: Dish): Dish
    fun deleteById(id: Long): Boolean
}
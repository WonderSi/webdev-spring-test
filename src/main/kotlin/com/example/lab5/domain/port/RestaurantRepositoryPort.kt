package com.example.lab5.domain.port

import com.example.lab5.domain.model.Restaurant

interface RestaurantRepositoryPort {
    fun findAll(): List<Restaurant>
    fun findById(id: Long): Restaurant?
    fun save(restaurant: Restaurant): Restaurant
    fun update(restaurant: Restaurant): Restaurant
    fun deleteById(id: Long): Boolean
}
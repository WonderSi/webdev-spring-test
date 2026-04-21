package com.example.lab5.infrastructure.jpa.adapter

import com.example.lab5.domain.model.Restaurant
import com.example.lab5.domain.port.RestaurantRepositoryPort
import com.example.lab5.infrastructure.jpa.entity.RestaurantEntity
import com.example.lab5.infrastructure.jpa.repository.RestaurantJpaRepository
import org.springframework.stereotype.Component

@Component
class RestaurantJpaAdapter(
    private val restaurantJpaRepository: RestaurantJpaRepository
) : RestaurantRepositoryPort {

    override fun findAll(): List<Restaurant> =
        restaurantJpaRepository.findAll().map { it.toDomain() }

    override fun findById(id: Long): Restaurant? =
        restaurantJpaRepository.findById(id).orElse(null)?.toDomain()

    override fun save(restaurant: Restaurant): Restaurant =
        restaurantJpaRepository.save(restaurant.toEntity()).toDomain()

    override fun update(restaurant: Restaurant): Restaurant =
        restaurantJpaRepository.save(restaurant.toEntity()).toDomain()

    override fun deleteById(id: Long): Boolean {
        return if (restaurantJpaRepository.existsById(id)) {
            restaurantJpaRepository.deleteById(id)
            true
        } else false
    }
}

fun RestaurantEntity.toDomain() = Restaurant(id = id, name = name, address = address)

fun Restaurant.toEntity() = RestaurantEntity(id = id, name = name, address = address)
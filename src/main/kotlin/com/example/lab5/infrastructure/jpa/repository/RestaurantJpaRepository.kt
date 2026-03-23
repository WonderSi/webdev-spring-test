package com.example.lab5.infrastructure.jpa.repository

import com.example.lab5.infrastructure.jpa.entity.RestaurantEntity
import org.springframework.data.jpa.repository.JpaRepository

interface RestaurantJpaRepository : JpaRepository<RestaurantEntity, Long> {
    fun existsByNameIgnoreCase(name: String): Boolean
    fun findByNameIgnoreCase(name: String): RestaurantEntity?
}
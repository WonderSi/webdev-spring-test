package com.example.lab5.infrastructure.jpa.repository

import com.example.lab5.infrastructure.jpa.entity.DishEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param

interface DishJpaRepository : JpaRepository<DishEntity, Long> {

    fun findByName(name: String): DishEntity?

    @Query("""
        SELECT d FROM DishEntity d 
        WHERE lower(d.name) LIKE lower(concat('%', :namePart, '%'))
    """)
    fun findByNameContaining(@Param("namePart") namePart: String): List<DishEntity>

}
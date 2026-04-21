package com.example.lab5.infrastructure.jpa.repository

import com.example.lab5.domain.model.OrderStatus
import com.example.lab5.infrastructure.jpa.entity.OrderEntity
import org.springframework.data.jpa.repository.EntityGraph
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param

interface OrderJpaRepository : JpaRepository<OrderEntity, Long> {

    @EntityGraph(attributePaths = ["user", "dishes", "dishes.restaurant"])
    @Query("SELECT o FROM OrderEntity o WHERE o.id = :id")
    fun findByIdWithDetails(@Param("id") id: Long): OrderEntity?

    fun findAllByOrderByCreatedAtDesc(): List<OrderEntity>

    fun findAllByUserIdOrderByCreatedAtDesc(userId: Long): List<OrderEntity>

    fun findAllByStatusOrderByCreatedAtDesc(status: OrderStatus): List<OrderEntity>

    fun findAllByUserIdAndStatusOrderByCreatedAtDesc(userId: Long, status: OrderStatus): List<OrderEntity>
}
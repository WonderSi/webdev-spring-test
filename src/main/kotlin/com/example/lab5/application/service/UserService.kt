package com.example.lab5.application.service

import com.example.lab5.domain.exception.NotFoundException
import com.example.lab5.domain.model.User
import com.example.lab5.domain.port.UserRepositoryPort
import com.example.lab5.infrastructure.jpa.repository.OrderJpaRepository
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class UserService(
    private val userRepositoryPort: UserRepositoryPort,
    private val orderJpaRepository: OrderJpaRepository
) {
    private val logger = KotlinLogging.logger {}

    fun findAll(): List<User> = userRepositoryPort.findAll()

    fun findById(id: Long): User {
        logger.info { "Fetching user id=$id" }
        return userRepositoryPort.findById(id)
            ?: throw NotFoundException("User with id=$id not found")
    }

    fun create(user: User): Pair<User, Boolean> {
        val existing = userRepositoryPort.findByEmail(user.email)
        return if (existing != null) {
            Pair(existing, false)
        } else {
            val created = userRepositoryPort.save(user)
            logger.info { "Created user id=${created.id}, email=${created.email}" }
            Pair(created, true)
        }
    }

    fun update(id: Long, user: User): User {
        userRepositoryPort.findById(id)
            ?: throw NotFoundException("User with id=$id not found")
        return userRepositoryPort.update(user.copy(id = id))
    }

    @Transactional
    fun delete(id: Long) {
        val orders = orderJpaRepository.findAllByUserIdOrderByCreatedAtDesc(id)
        orderJpaRepository.deleteAll(orders)

        val deleted = userRepositoryPort.deleteById(id)
        if (!deleted) throw NotFoundException("User with id=$id not found")
        logger.info { "Deleted user id=$id" }
    }
}
package com.example.lab5.infrastructure.jpa.adapter

import com.example.lab5.domain.model.User
import com.example.lab5.domain.port.UserRepositoryPort
import com.example.lab5.infrastructure.jpa.entity.UserEntity
import com.example.lab5.infrastructure.jpa.repository.UserJpaRepository
import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Component

@Component
@Profile("db")
class UserJpaAdapter(
    private val userJpaRepository: UserJpaRepository
) : UserRepositoryPort {

    override fun findAll(): List<User> =
        userJpaRepository.findAll().map { it.toDomain() }

    override fun findById(id: Long): User? =
        userJpaRepository.findById(id).orElse(null)?.toDomain()

    override fun findByEmail(email: String): User? =
        userJpaRepository.findByEmail(email)?.toDomain()

    override fun save(user: User): User =
        userJpaRepository.save(UserEntity.fromDomain(user)).toDomain()

    override fun update(user: User): User =
        userJpaRepository.save(UserEntity.fromDomain(user)).toDomain()

    override fun deleteById(id: Long): Boolean {
        return if (userJpaRepository.existsById(id)) {
            userJpaRepository.deleteById(id)
            true
        } else {
            false
        }
    }
}
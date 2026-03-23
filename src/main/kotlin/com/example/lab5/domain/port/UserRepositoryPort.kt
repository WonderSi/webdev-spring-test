package com.example.lab5.domain.port

import com.example.lab5.domain.model.User

interface UserRepositoryPort {
    fun findAll(): List<User>
    fun findById(id: Long): User?
    fun findByEmail(email: String): User?
    fun save(user: User): User
    fun update(user: User): User
    fun deleteById(id: Long): Boolean
}
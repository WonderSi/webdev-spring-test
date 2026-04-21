package com.example.lab5.infrastructure.mock

import com.example.lab5.domain.model.User
import com.example.lab5.domain.port.UserRepositoryPort
import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Component

@Component
@Profile("mock")
class UserMockRepository : UserRepositoryPort {

    private val storage = mutableMapOf<Long, User>()

    private var seq = 1L

    override fun findAll(): List<User> = storage.values.toList()

    override fun findById(id: Long): User? = storage[id]

    override fun findByEmail(email: String): User? =
        storage.values.firstOrNull { it.email == email }

    override fun save(user: User): User {
        val saved = user.copy(id = seq++)
        storage[saved.id] = saved
        return saved
    }

    override fun update(user: User): User {
        storage[user.id] = user
        return user
    }

    override fun deleteById(id: Long): Boolean {
        return storage.remove(id) != null
    }
}
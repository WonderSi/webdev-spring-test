package com.example.lab5.infrastructure.jpa.entity

import com.example.lab5.domain.model.User
import jakarta.persistence.*

@Entity
@Table(name = "users")
class UserEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @Column(unique = true, nullable = false)
    val email: String,

    @Column(nullable = false)
    val firstName: String,

    @Column(nullable = false)
    val lastName: String,

    @Column(nullable = false)
    val isActive: Boolean = true
) {
    constructor() : this(0, "", "", "", true)

    fun toDomain() = User(
        id = id,
        email = email,
        firstName = firstName,
        lastName = lastName,
        isActive = isActive
    )

    companion object {
        fun fromDomain(user: User) = UserEntity(
            id = user.id,
            email = user.email,
            firstName = user.firstName,
            lastName = user.lastName,
            isActive = user.isActive
        )
    }
}
package com.example.lab5.infrastructure.jpa.entity

import jakarta.persistence.*

@Entity
@Table(name = "restaurants")
class RestaurantEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @Column(nullable = false, unique = true)
    val name: String,

    @Column(nullable = false)
    val address: String,

    @OneToMany(mappedBy = "restaurant", cascade = [CascadeType.ALL], orphanRemoval = true)
    val dishes: MutableList<DishEntity> = mutableListOf()
) {
    constructor() : this(0, "", "", mutableListOf())
}
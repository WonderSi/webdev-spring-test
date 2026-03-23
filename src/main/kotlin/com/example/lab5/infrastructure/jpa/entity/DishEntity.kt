package com.example.lab5.infrastructure.jpa.entity

import com.example.lab5.domain.model.Dish
import jakarta.persistence.*
import java.math.BigDecimal

@Entity
@Table(name = "dishes")
class DishEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @Column(unique = false, nullable = false)
    val name: String,

    @Column(nullable = false)
    val description: String,

    @Column(nullable = false, precision = 10, scale = 2)
    val price: BigDecimal,

    @Column(nullable = false)
    val isAvailable: Boolean = true,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "restaurant_id", nullable = false)
    val restaurant: RestaurantEntity
) {
    constructor() : this(0, "", "", BigDecimal.ZERO, true, RestaurantEntity())

    fun toDomain() = Dish(
        id = id,
        name = name,
        description = description,
        price = price,
        isAvailable = isAvailable,
        restaurantId = restaurant.id
    )

    companion object {
        fun fromDomain(dish: Dish, restaurant: RestaurantEntity) = DishEntity(
            id = dish.id,
            name = dish.name,
            description = dish.description,
            price = dish.price,
            isAvailable = dish.isAvailable,
            restaurant = restaurant
        )
    }
}
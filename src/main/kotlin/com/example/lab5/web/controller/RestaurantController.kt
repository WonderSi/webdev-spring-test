package com.example.lab5.web.controller

import com.example.lab5.application.service.RestaurantService
import com.example.lab5.web.dto.*
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/v1/restaurants")
@Validated
class RestaurantController(
    private val restaurantService: RestaurantService
) {
    @GetMapping
    fun listRestaurants(): ResponseEntity<List<RestaurantResponse>> =
        ResponseEntity.ok(restaurantService.findAll().map { it.toResponse() })

    @GetMapping("/{id}")
    fun getRestaurantById(@PathVariable id: Long): ResponseEntity<RestaurantResponse> =
        ResponseEntity.ok(restaurantService.findById(id).toResponse())

    @GetMapping("/{id}/dishes")
    fun getRestaurantDishes(@PathVariable id: Long): ResponseEntity<List<DishResponse>> =
        ResponseEntity.ok(restaurantService.getMenu(id).map { it.toResponse() })

    @PostMapping
    fun createRestaurant(@Valid @RequestBody request: RestaurantCreateRequest): ResponseEntity<RestaurantResponse> =
        ResponseEntity.status(HttpStatus.CREATED)
            .body(restaurantService.create(request.toDomain()).toResponse())

    @PostMapping("/{restaurantId}/dishes")
    fun createDishInRestaurant(
        @PathVariable restaurantId: Long,
        @Valid @RequestBody request: DishCreateRequest
    ): ResponseEntity<DishResponse> =
        ResponseEntity.status(HttpStatus.CREATED)
            .body(restaurantService.addDish(restaurantId, request.toDomain()).toResponse())

    @PutMapping("/{id}")
    fun updateRestaurant(
        @PathVariable id: Long,
        @Valid @RequestBody request: RestaurantUpdateRequest
    ): ResponseEntity<RestaurantResponse> =
        ResponseEntity.ok(restaurantService.update(id, request.toDomain()).toResponse())

    @DeleteMapping("/{id}")
    fun deleteRestaurant(@PathVariable id: Long): ResponseEntity<Void> {
        restaurantService.delete(id)
        return ResponseEntity.noContent().build()
    }
}
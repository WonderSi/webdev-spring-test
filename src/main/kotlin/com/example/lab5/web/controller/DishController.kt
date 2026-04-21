package com.example.lab5.web.controller

import com.example.lab5.application.service.DishService
import com.example.lab5.web.dto.*
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/v1/dishes")
@Validated
class DishController(
    private val dishService: DishService
) {
    @GetMapping
    fun listDishes(
        @RequestParam(required = false) namePart: String?
    ): ResponseEntity<List<DishResponse>> =
        ResponseEntity.ok(dishService.findAll(namePart).map { it.toResponse() })

    @GetMapping("/{id}")
    fun getDishById(@PathVariable id: Long): ResponseEntity<DishResponse> =
        ResponseEntity.ok(dishService.findById(id).toResponse())

    @PostMapping
    fun createDish(@Valid @RequestBody request: DishCreateRequest): ResponseEntity<DishResponse> {
        val (dish, isCreated) = dishService.create(request.toDomain())
        return if (isCreated) {
            ResponseEntity.status(HttpStatus.CREATED).body(dish.toResponse())
        } else {
            ResponseEntity.ok(dish.toResponse())
        }
    }

    @PutMapping("/{id}")
    fun updateDish(
        @PathVariable id: Long,
        @Valid @RequestBody request: DishUpdateRequest
    ): ResponseEntity<DishResponse> =
        ResponseEntity.ok(dishService.update(id, request.toDomain()).toResponse())

    @DeleteMapping("/{id}")
    fun deleteDish(@PathVariable id: Long): ResponseEntity<Void> {
        dishService.delete(id)
        return ResponseEntity.noContent().build()
    }
}
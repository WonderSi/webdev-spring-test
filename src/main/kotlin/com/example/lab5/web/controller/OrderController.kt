package com.example.lab5.web.controller

import com.example.lab5.application.service.OrderService
import com.example.lab5.domain.model.OrderStatus
import com.example.lab5.web.dto.*
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/v1/orders")
@Validated
class OrderController(
    private val orderService: OrderService
) {
    @GetMapping
    fun listOrders(
        @RequestParam(required = false) userId: Long?,
        @RequestParam(required = false) status: OrderStatus?
    ): ResponseEntity<List<OrderResponse>> =
        ResponseEntity.ok(orderService.findAll(userId, status).map { it.toResponse() })

    @GetMapping("/{id}")
    fun getOrderById(@PathVariable id: Long): ResponseEntity<OrderResponse> =
        ResponseEntity.ok(orderService.findById(id).toResponse())

    @PostMapping
    fun createOrder(@Valid @RequestBody request: OrderCreateRequest): ResponseEntity<OrderResponse> {
        val order = orderService.create(
            userId = request.userId!!,
            dishIds = request.dishIds ?: emptyList()
        )
        return ResponseEntity.status(HttpStatus.CREATED).body(order.toResponse())
    }

    @DeleteMapping("/{id}")
    fun deleteOrder(@PathVariable id: Long): ResponseEntity<Void> {
        orderService.delete(id)
        return ResponseEntity.noContent().build()
    }

    @PatchMapping("/{id}/status")
    fun updateOrderStatus(
        @PathVariable id: Long,
        @RequestBody request: OrderStatusUpdateRequest
    ): ResponseEntity<OrderResponse> {
        val status = request.status 
            ?: throw IllegalArgumentException("Status is required")
        
        val updated = orderService.updateStatus(id, status)
        return ResponseEntity.ok(updated.toResponse())
    }
}

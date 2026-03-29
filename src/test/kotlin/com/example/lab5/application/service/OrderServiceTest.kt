package com.example.lab5.application.service

import com.example.lab5.domain.exception.BadRequestException
import com.example.lab5.domain.exception.InvalidOrderStateException
import com.example.lab5.domain.exception.NotFoundException
import com.example.lab5.domain.model.Dish
import com.example.lab5.domain.model.Order
import com.example.lab5.domain.model.OrderStatus
import com.example.lab5.domain.model.User
import com.example.lab5.domain.port.OrderRepositoryPort
import com.example.lab5.domain.port.UserRepositoryPort
import com.example.lab5.infrastructure.jpa.entity.DishEntity
import com.example.lab5.infrastructure.jpa.entity.RestaurantEntity
import com.example.lab5.infrastructure.jpa.repository.DishJpaRepository
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.mockk.verify
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.extension.ExtendWith
import java.math.BigDecimal

@ExtendWith(MockKExtension::class)
class OrderServiceTest {

    @MockK
    lateinit var orderRepositoryPort: OrderRepositoryPort

    @MockK
    lateinit var dishJpaRepository: DishJpaRepository

    @MockK
    lateinit var userRepositoryPort: UserRepositoryPort

    @InjectMockKs
    lateinit var orderService: OrderService

    private val testUser = User(id = 1L, email = "user@test.com", firstName = "Ivan", lastName = "Petrov")
    private val testRestaurant = RestaurantEntity(id = 1L, name = "Pizza", address = "ул. Ленина, 1")
    private val testDishEntity = DishEntity(
        id = 10L,
        name = "Маргарита",
        description = "Классическая пицца",
        price = BigDecimal("500.00"),
        isAvailable = true,
        restaurant = testRestaurant
    )
    private val testDish = Dish(
        id = 10L, name = "Маргарита", description = "Классическая пицца",
        price = BigDecimal("500.00"), isAvailable = true, restaurantId = 1L
    )

    // --- findById ---

    @Test
    fun `findById возвращает заказ если он существует`() {
        val order = Order(id = 1L, userId = 1L, dishes = listOf(testDish))
        every { orderRepositoryPort.findById(1L) } returns order

        val result = orderService.findById(1L)

        assertEquals(1L, result.id)
        assertEquals(1L, result.userId)
        assertEquals(1, result.dishes.size)
    }

    @Test
    fun `findById бросает NotFoundException для несуществующего id`() {
        every { orderRepositoryPort.findById(999L) } returns null

        assertThrows<NotFoundException> {
            orderService.findById(999L)
        }
    }

    // --- create ---

    @Test
    fun `create успешно создаёт заказ`() {
        val savedOrder = Order(id = 5L, userId = 1L, dishes = listOf(testDish))

        every { userRepositoryPort.findById(1L) } returns testUser
        every { dishJpaRepository.findAllById(listOf(10L)) } returns listOf(testDishEntity)
        every { orderRepositoryPort.save(any()) } returns savedOrder

        val result = orderService.create(userId = 1L, dishIds = listOf(10L))

        assertEquals(5L, result.id)
        assertEquals(1L, result.userId)
        verify(exactly = 1) { orderRepositoryPort.save(any()) }
    }

    @Test
    fun `create бросает BadRequestException если пользователь не найден`() {
        every { userRepositoryPort.findById(99L) } returns null

        assertThrows<BadRequestException> {
            orderService.create(userId = 99L, dishIds = listOf(10L))
        }

        verify(exactly = 0) { orderRepositoryPort.save(any()) }
    }

    @Test
    fun `create бросает BadRequestException если часть блюд не найдена`() {
        every { userRepositoryPort.findById(1L) } returns testUser
        every { dishJpaRepository.findAllById(listOf(10L, 999L)) } returns listOf(testDishEntity)

        assertThrows<BadRequestException> {
            orderService.create(userId = 1L, dishIds = listOf(10L, 999L))
        }
    }

    // --- updateStatus ---

    @Test
    fun `updateStatus PENDING to CONFIRMED успешно меняет статус`() {
        val order = Order(id = 1L, userId = 1L, status = OrderStatus.PENDING)
        val updated = order.copy(status = OrderStatus.CONFIRMED)

        every { orderRepositoryPort.findById(1L) } returns order
        every { orderRepositoryPort.update(updated) } returns updated

        val result = orderService.updateStatus(1L, OrderStatus.CONFIRMED)

        assertEquals(OrderStatus.CONFIRMED, result.status)
    }

    @Test
    fun `updateStatus CONFIRMED to CANCELLED успешно меняет статус`() {
        val order = Order(id = 1L, userId = 1L, status = OrderStatus.CONFIRMED)
        val updated = order.copy(status = OrderStatus.CANCELLED)

        every { orderRepositoryPort.findById(1L) } returns order
        every { orderRepositoryPort.update(updated) } returns updated

        val result = orderService.updateStatus(1L, OrderStatus.CANCELLED)

        assertEquals(OrderStatus.CANCELLED, result.status)
    }

    @Test
    fun `updateStatus бросает InvalidOrderStateException для недопустимого перехода`() {
        val order = Order(id = 1L, userId = 1L, status = OrderStatus.DELIVERED)

        every { orderRepositoryPort.findById(1L) } returns order

        assertThrows<InvalidOrderStateException> {
            orderService.updateStatus(1L, OrderStatus.CANCELLED)
        }
    }

    @Test
    fun `updateStatus бросает InvalidOrderStateException для перехода PENDING to DELIVERED`() {
        val order = Order(id = 1L, userId = 1L, status = OrderStatus.PENDING)

        every { orderRepositoryPort.findById(1L) } returns order

        assertThrows<InvalidOrderStateException> {
            orderService.updateStatus(1L, OrderStatus.DELIVERED)
        }
    }

    // --- delete ---

    @Test
    fun `delete успешно удаляет существующий заказ`() {
        val order = Order(id = 1L, userId = 1L)

        every { orderRepositoryPort.findById(1L) } returns order
        every { orderRepositoryPort.delete(1L) } returns Unit

        assertDoesNotThrow {
            orderService.delete(1L)
        }

        verify(exactly = 1) { orderRepositoryPort.delete(1L) }
    }

    @Test
    fun `delete бросает NotFoundException если заказ не найден`() {
        every { orderRepositoryPort.findById(999L) } returns null

        assertThrows<NotFoundException> {
            orderService.delete(999L)
        }
    }
}
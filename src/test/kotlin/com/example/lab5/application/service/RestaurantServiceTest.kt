package com.example.lab5.application.service

import com.example.lab5.domain.exception.AlreadyExistsException
import com.example.lab5.domain.exception.NotFoundException
import com.example.lab5.domain.model.Dish
import com.example.lab5.domain.model.Restaurant
import com.example.lab5.domain.port.RestaurantRepositoryPort
import com.example.lab5.infrastructure.jpa.entity.DishEntity
import com.example.lab5.infrastructure.jpa.entity.RestaurantEntity
import com.example.lab5.infrastructure.jpa.repository.DishJpaRepository
import com.example.lab5.infrastructure.jpa.repository.RestaurantJpaRepository
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
import java.util.Optional

@ExtendWith(MockKExtension::class)
class RestaurantServiceTest {

    @MockK
    lateinit var restaurantRepositoryPort: RestaurantRepositoryPort

    @MockK
    lateinit var restaurantJpaRepository: RestaurantJpaRepository

    @MockK
    lateinit var dishJpaRepository: DishJpaRepository

    @InjectMockKs
    lateinit var restaurantService: RestaurantService

    // --- findById ---

    @Test
    fun `findById возвращает ресторан если он существует`() {
        // Arrange
        val restaurant = Restaurant(id = 1L, name = "Pizza Place", address = "ул. Ленина, 1")
        every { restaurantRepositoryPort.findById(1L) } returns restaurant

        // Act
        val result = restaurantService.findById(1L)

        // Assert
        assertEquals(1L, result.id)
        assertEquals("Pizza Place", result.name)
        assertEquals("ул. Ленина, 1", result.address)
    }

    @Test
    fun `findById бросает NotFoundException для несуществующего id`() {
        every { restaurantRepositoryPort.findById(999L) } returns null

        assertThrows<NotFoundException> {
            restaurantService.findById(999L)
        }
    }

    // --- findAll ---

    @Test
    fun `findAll возвращает список всех ресторанов`() {
        val restaurants = listOf(
            Restaurant(id = 1L, name = "Pizza", address = "ул. Ленина, 1"),
            Restaurant(id = 2L, name = "Sushi", address = "ул. Мира, 2")
        )
        every { restaurantRepositoryPort.findAll() } returns restaurants

        val result = restaurantService.findAll()

        assertEquals(2, result.size)
        assertEquals("Pizza", result[0].name)
    }

    // --- create ---

    @Test
    fun `create успешно создаёт ресторан`() {
        val input = Restaurant(name = "New Place", address = "ул. Тестовая, 1")
        val saved = input.copy(id = 10L)

        every { restaurantJpaRepository.existsByNameIgnoreCase("New Place") } returns false
        every { restaurantRepositoryPort.save(input) } returns saved

        val result = restaurantService.create(input)

        assertEquals(10L, result.id)
        assertEquals("New Place", result.name)
        verify(exactly = 1) { restaurantRepositoryPort.save(input) }
    }

    @Test
    fun `create бросает AlreadyExistsException при дублировании имени`() {
        val input = Restaurant(name = "Pizza Place", address = "ул. Мира, 5")

        every { restaurantJpaRepository.existsByNameIgnoreCase("Pizza Place") } returns true

        assertThrows<AlreadyExistsException> {
            restaurantService.create(input)
        }

        verify(exactly = 0) { restaurantRepositoryPort.save(any()) }
    }

    // --- delete ---

    @Test
    fun `delete успешно удаляет существующий ресторан`() {
        every { restaurantRepositoryPort.deleteById(1L) } returns true

        // Не должно бросать исключение
        assertDoesNotThrow {
            restaurantService.delete(1L)
        }
    }

    @Test
    fun `delete бросает NotFoundException для несуществующего ресторана`() {
        every { restaurantRepositoryPort.deleteById(999L) } returns false

        assertThrows<NotFoundException> {
            restaurantService.delete(999L)
        }
    }

    // --- update ---

    @Test
    fun `update успешно обновляет ресторан`() {
        val existing = Restaurant(id = 1L, name = "Old Name", address = "ул. Старая, 1")
        val input = Restaurant(name = "New Name", address = "ул. Новая, 2")
        val updated = input.copy(id = 1L)

        every { restaurantRepositoryPort.findById(1L) } returns existing
        every { restaurantJpaRepository.findByNameIgnoreCase("New Name") } returns null
        every { restaurantRepositoryPort.update(updated) } returns updated

        val result = restaurantService.update(1L, input)

        assertEquals(1L, result.id)
        assertEquals("New Name", result.name)
    }

    @Test
    fun `update бросает NotFoundException если ресторан не найден`() {
        every { restaurantRepositoryPort.findById(999L) } returns null

        assertThrows<NotFoundException> {
            restaurantService.update(999L, Restaurant(name = "X", address = "Y"))
        }
    }

    // --- getMenu ---

    @Test
    fun `getMenu возвращает список блюд ресторана`() {
        val restaurantEntity = RestaurantEntity(id = 1L, name = "Pizza", address = "ул. Ленина, 1")
        val dishEntity = DishEntity(
            id = 10L,
            name = "Маргарита",
            description = "Классическая",
            price = BigDecimal("500.00"),
            isAvailable = true,
            restaurant = restaurantEntity
        )
        restaurantEntity.dishes.add(dishEntity)

        every { restaurantJpaRepository.findById(1L) } returns Optional.of(restaurantEntity)

        val result = restaurantService.getMenu(1L)

        assertEquals(1, result.size)
        assertEquals("Маргарита", result[0].name)
        assertEquals(1L, result[0].restaurantId)
    }

    @Test
    fun `getMenu бросает NotFoundException если ресторан не найден`() {
        every { restaurantJpaRepository.findById(999L) } returns Optional.empty()

        assertThrows<NotFoundException> {
            restaurantService.getMenu(999L)
        }
    }
}

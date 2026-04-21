package com.example.lab5.application.service

import com.example.lab5.domain.exception.AlreadyExistsException
import com.example.lab5.domain.exception.NotFoundException
import com.example.lab5.domain.model.Dish
import com.example.lab5.domain.port.DishRepositoryPort
import com.example.lab5.infrastructure.jpa.repository.OrderJpaRepository
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
class DishServiceTest {

    @MockK
    lateinit var dishRepositoryPort: DishRepositoryPort

    @MockK
    lateinit var orderJpaRepository: OrderJpaRepository

    @InjectMockKs
    lateinit var dishService: DishService

    private val testDish = Dish(
        id = 1L,
        name = "Маргарита",
        description = "Классическая пицца",
        price = BigDecimal("500.00"),
        isAvailable = true,
        restaurantId = 1L
    )

    // --- findAll ---

    @Test
    fun `findAll без фильтра возвращает все блюда`() {
        every { dishRepositoryPort.findAll() } returns listOf(testDish)

        val result = dishService.findAll(null)

        assertEquals(1, result.size)
        assertEquals("Маргарита", result[0].name)
    }

    @Test
    fun `findAll с фильтром по имени возвращает отфильтрованные блюда`() {
        every { dishRepositoryPort.findAllByNamePart("Мар") } returns listOf(testDish)

        val result = dishService.findAll("Мар")

        assertEquals(1, result.size)
        assertEquals("Маргарита", result[0].name)
        verify(exactly = 0) { dishRepositoryPort.findAll() }
    }

    // --- findById ---

    @Test
    fun `findById возвращает блюдо если оно существует`() {
        every { dishRepositoryPort.findById(1L) } returns testDish

        val result = dishService.findById(1L)

        assertEquals(1L, result.id)
        assertEquals("Маргарита", result.name)
    }

    @Test
    fun `findById бросает NotFoundException для несуществующего id`() {
        every { dishRepositoryPort.findById(999L) } returns null

        assertThrows<NotFoundException> {
            dishService.findById(999L)
        }
    }

    // --- create ---

    @Test
    fun `create успешно создаёт блюдо`() {
        val input = testDish.copy(id = 0)
        val saved = testDish

        every { dishRepositoryPort.findByName("Маргарита") } returns null
        every { dishRepositoryPort.save(input) } returns saved

        val (result, isCreated) = dishService.create(input)

        assertTrue(isCreated)
        assertEquals(1L, result.id)
        verify(exactly = 1) { dishRepositoryPort.save(input) }
    }

    @Test
    fun `create бросает AlreadyExistsException при дублировании имени`() {
        val input = testDish.copy(id = 0)

        every { dishRepositoryPort.findByName("Маргарита") } returns testDish

        assertThrows<AlreadyExistsException> {
            dishService.create(input)
        }

        verify(exactly = 0) { dishRepositoryPort.save(any()) }
    }

    // --- update ---

    @Test
    fun `update успешно обновляет блюдо`() {
        val updated = testDish.copy(name = "Пепперони", price = BigDecimal("600.00"))

        every { dishRepositoryPort.findById(1L) } returns testDish
        every { dishRepositoryPort.update(updated) } returns updated

        val result = dishService.update(1L, updated)

        assertEquals("Пепперони", result.name)
        assertEquals(BigDecimal("600.00"), result.price)
    }

    @Test
    fun `update бросает NotFoundException если блюдо не найдено`() {
        every { dishRepositoryPort.findById(999L) } returns null

        assertThrows<NotFoundException> {
            dishService.update(999L, testDish)
        }
    }

    // --- delete ---

    @Test
    fun `delete успешно удаляет блюдо`() {
        every { orderJpaRepository.findAll() } returns emptyList()
        every { dishRepositoryPort.deleteById(1L) } returns true

        assertDoesNotThrow {
            dishService.delete(1L)
        }

        verify(exactly = 1) { dishRepositoryPort.deleteById(1L) }
    }

    @Test
    fun `delete бросает NotFoundException если блюдо не найдено`() {
        every { orderJpaRepository.findAll() } returns emptyList()
        every { dishRepositoryPort.deleteById(999L) } returns false

        assertThrows<NotFoundException> {
            dishService.delete(999L)
        }
    }
}

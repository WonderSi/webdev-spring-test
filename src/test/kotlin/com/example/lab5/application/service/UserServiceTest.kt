package com.example.lab5.application.service

import com.example.lab5.domain.exception.NotFoundException
import com.example.lab5.domain.model.User
import com.example.lab5.domain.port.UserRepositoryPort
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

@ExtendWith(MockKExtension::class)
class UserServiceTest {

    @MockK
    lateinit var userRepositoryPort: UserRepositoryPort

    @MockK
    lateinit var orderJpaRepository: OrderJpaRepository

    @InjectMockKs
    lateinit var userService: UserService

    private val testUser = User(
        id = 1L,
        email = "ivan@test.com",
        firstName = "Ivan",
        lastName = "Petrov"
    )

    // --- findAll ---

    @Test
    fun `findAll возвращает список всех пользователей`() {
        every { userRepositoryPort.findAll() } returns listOf(testUser)

        val result = userService.findAll()

        assertEquals(1, result.size)
        assertEquals("ivan@test.com", result[0].email)
    }

    // --- findById ---

    @Test
    fun `findById возвращает пользователя если он существует`() {
        every { userRepositoryPort.findById(1L) } returns testUser

        val result = userService.findById(1L)

        assertEquals(1L, result.id)
        assertEquals("Ivan", result.firstName)
    }

    @Test
    fun `findById бросает NotFoundException для несуществующего id`() {
        every { userRepositoryPort.findById(999L) } returns null

        assertThrows<NotFoundException> {
            userService.findById(999L)
        }
    }

    // --- create ---

    @Test
    fun `create создаёт нового пользователя если email не занят`() {
        val input = testUser.copy(id = 0)

        every { userRepositoryPort.findByEmail("ivan@test.com") } returns null
        every { userRepositoryPort.save(input) } returns testUser

        val (result, isCreated) = userService.create(input)

        assertTrue(isCreated)
        assertEquals(1L, result.id)
        verify(exactly = 1) { userRepositoryPort.save(input) }
    }

    @Test
    fun `create возвращает существующего пользователя если email уже занят`() {
        val input = testUser.copy(id = 0)

        every { userRepositoryPort.findByEmail("ivan@test.com") } returns testUser

        val (result, isCreated) = userService.create(input)

        assertFalse(isCreated)
        assertEquals(1L, result.id)
        verify(exactly = 0) { userRepositoryPort.save(any()) }
    }

    // --- update ---

    @Test
    fun `update успешно обновляет пользователя`() {
        val updated = testUser.copy(firstName = "Petr", lastName = "Sidorov")

        every { userRepositoryPort.findById(1L) } returns testUser
        every { userRepositoryPort.update(updated) } returns updated

        val result = userService.update(1L, updated)

        assertEquals("Petr", result.firstName)
        assertEquals("Sidorov", result.lastName)
    }

    @Test
    fun `update бросает NotFoundException если пользователь не найден`() {
        every { userRepositoryPort.findById(999L) } returns null

        assertThrows<NotFoundException> {
            userService.update(999L, testUser)
        }
    }

    // --- delete ---

    @Test
    fun `delete успешно удаляет пользователя`() {
        every { orderJpaRepository.findAllByUserIdOrderByCreatedAtDesc(1L) } returns emptyList()
        every { orderJpaRepository.deleteAll(emptyList()) } returns Unit
        every { userRepositoryPort.deleteById(1L) } returns true

        assertDoesNotThrow {
            userService.delete(1L)
        }

        verify(exactly = 1) { userRepositoryPort.deleteById(1L) }
    }

    @Test
    fun `delete бросает NotFoundException если пользователь не найден`() {
        every { orderJpaRepository.findAllByUserIdOrderByCreatedAtDesc(999L) } returns emptyList()
        every { orderJpaRepository.deleteAll(emptyList()) } returns Unit
        every { userRepositoryPort.deleteById(999L) } returns false

        assertThrows<NotFoundException> {
            userService.delete(999L)
        }
    }
}

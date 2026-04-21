package com.example.lab5.web.controller

import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.testcontainers.service.connection.ServiceConnection
import org.springframework.http.MediaType
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.delete
import org.springframework.test.web.servlet.get
import org.springframework.test.web.servlet.post
import org.springframework.test.web.servlet.put
import org.springframework.transaction.annotation.Transactional
import org.testcontainers.containers.PostgreSQLContainer
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers

@SpringBootTest
@AutoConfigureMockMvc
@Testcontainers
@ActiveProfiles("test")
@Transactional
class RestaurantIntegrationTest {

    companion object {
        @Container
        @ServiceConnection
        @JvmStatic
        val postgres = PostgreSQLContainer<Nothing>("postgres:16-alpine").apply {
            withDatabaseName("integration-tests-db")
            withUsername("test")
            withPassword("test")
        }
    }

    @Autowired
    lateinit var mockMvc: MockMvc

    //GET /api/v1/restaurants
    @Test
    fun `GET restaurants возвращает 200 и пустой список`() {
        mockMvc.get("/api/v1/restaurants")
            .andExpect {
                status { isOk() }
                content { contentType(MediaType.APPLICATION_JSON) }
                jsonPath("$") { isArray() }
            }
    }

    @Test
    fun `GET restaurants возвращает 200 и список после создания`() {
        mockMvc.post("/api/v1/restaurants") {
            contentType = MediaType.APPLICATION_JSON
            content = """{"name": "Список Тест", "address": "ул. Ленина, 1"}"""
        }

        mockMvc.get("/api/v1/restaurants")
            .andExpect {
                status { isOk() }
                jsonPath("$.length()") { value(1) }
                jsonPath("$[0].name") { value("Список Тест") }
            }
    }

    //POST /api/v1/restaurants

    @Test
    fun `POST restaurant возвращает 201 и тело с id, name, address`() {
        mockMvc.post("/api/v1/restaurants") {
            contentType = MediaType.APPLICATION_JSON
            content = """{"name": "New Place", "address": "ул. Тестовая, 1"}"""
        }.andExpect {
            status { isCreated() }
            jsonPath("$.id") { exists() }
            jsonPath("$.name") { value("New Place") }
            jsonPath("$.address") { value("ул. Тестовая, 1") }
        }
    }

    @Test
    fun `POST restaurant с пустым именем возвращает 400 и errors`() {
        mockMvc.post("/api/v1/restaurants") {
            contentType = MediaType.APPLICATION_JSON
            content = """{"name": "", "address": "ул. Тестовая, 1"}"""
        }.andExpect {
            status { isBadRequest() }
            jsonPath("$.status") { value(400) }
            jsonPath("$.errors.name") { exists() }
        }
    }

    @Test
    fun `POST restaurant с дублирующимся именем возвращает 409`() {
        mockMvc.post("/api/v1/restaurants") {
            contentType = MediaType.APPLICATION_JSON
            content = """{"name": "Дубль", "address": "ул. Первая, 1"}"""
        }
        mockMvc.post("/api/v1/restaurants") {
            contentType = MediaType.APPLICATION_JSON
            content = """{"name": "Дубль", "address": "ул. Вторая, 2"}"""
        }.andExpect {
            status { isConflict() }
            jsonPath("$.status") { value(409) }
        }
    }

    //GET /api/v1/restaurants/{id}

    @Test
    fun `GET restaurant по id возвращает 200 и данные ресторана`() {
        val createResult = mockMvc.post("/api/v1/restaurants") {
            contentType = MediaType.APPLICATION_JSON
            content = """{"name": "Get By Id", "address": "ул. Ленина, 5"}"""
        }.andReturn()

        val id = com.fasterxml.jackson.databind.ObjectMapper()
            .readTree(createResult.response.contentAsString)
            .get("id").asLong()

        mockMvc.get("/api/v1/restaurants/$id")
            .andExpect {
                status { isOk() }
                jsonPath("$.id") { value(id) }
                jsonPath("$.name") { value("Get By Id") }
                jsonPath("$.address") { value("ул. Ленина, 5") }
            }
    }

    @Test
    fun `GET restaurant по несуществующему id возвращает 404`() {
        mockMvc.get("/api/v1/restaurants/999999")
            .andExpect {
                status { isNotFound() }
                jsonPath("$.status") { value(404) }
            }
    }

    //PUT /api/v1/restaurants/{id}

    @Test
    fun `PUT restaurant обновляет данные и возвращает 200`() {
        val createResult = mockMvc.post("/api/v1/restaurants") {
            contentType = MediaType.APPLICATION_JSON
            content = """{"name": "Старое Название", "address": "ул. Старая, 1"}"""
        }.andReturn()

        val id = com.fasterxml.jackson.databind.ObjectMapper()
            .readTree(createResult.response.contentAsString)
            .get("id").asLong()

        mockMvc.put("/api/v1/restaurants/$id") {
            contentType = MediaType.APPLICATION_JSON
            content = """{"name": "Новое Название", "address": "ул. Новая, 2"}"""
        }.andExpect {
            status { isOk() }
            jsonPath("$.id") { value(id) }
            jsonPath("$.name") { value("Новое Название") }
            jsonPath("$.address") { value("ул. Новая, 2") }
        }
    }

    @Test
    fun `PUT restaurant с несуществующим id возвращает 404`() {
        mockMvc.put("/api/v1/restaurants/999999") {
            contentType = MediaType.APPLICATION_JSON
            content = """{"name": "Не важно", "address": "ул. Не важно, 0"}"""
        }.andExpect {
            status { isNotFound() }
            jsonPath("$.status") { value(404) }
        }
    }

    //DELETE /api/v1/restaurants/{id}

    @Test
    fun `DELETE restaurant возвращает 204`() {
        val createResult = mockMvc.post("/api/v1/restaurants") {
            contentType = MediaType.APPLICATION_JSON
            content = """{"name": "Удаляемый", "address": "ул. Временная, 1"}"""
        }.andReturn()

        val id = com.fasterxml.jackson.databind.ObjectMapper()
            .readTree(createResult.response.contentAsString)
            .get("id").asLong()

        mockMvc.delete("/api/v1/restaurants/$id")
            .andExpect {
                status { isNoContent() }
            }
    }

    @Test
    fun `DELETE restaurant с несуществующим id возвращает 404`() {
        mockMvc.delete("/api/v1/restaurants/999999")
            .andExpect {
                status { isNotFound() }
                jsonPath("$.status") { value(404) }
            }
    }
}

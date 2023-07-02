package com.yury

import com.google.gson.Gson
import com.yury.plugins.JwtConfig
import com.yury.plugins.TriangleRequest
import com.yury.plugins.TriangleResponse
import com.yury.plugins.configureSecurity
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.serialization.gson.*
import io.ktor.server.application.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.testing.*
import kotlin.test.Test
import kotlin.test.assertEquals


class ApplicationTest {

    @Test
    fun testRoot() = testApplication {

        application {

            install(ContentNegotiation) {
                gson {
                    setPrettyPrinting()
                }
            }

            configureSecurity()
        }

        val gson = Gson()

        // Prepare a mock request with the desired input
        val request = TriangleRequest(a = 3, b = 4)
        val requestBody = gson.toJson(request)

        // Perform the request

        val response = client.post("/hypotenuse") {
            contentType(ContentType.Application.Json)
            setBody(requestBody)
            header(HttpHeaders.Authorization, "Bearer ${JwtConfig.makeToken()}")
        }

        // Verify the response
        with(response) {
            assertEquals(HttpStatusCode.OK, status)

            val triangleResponse = Gson().fromJson(response.bodyAsText(), TriangleResponse::class.java)

            assertEquals(5.0, triangleResponse.hypotenuse)
        }

    }

}

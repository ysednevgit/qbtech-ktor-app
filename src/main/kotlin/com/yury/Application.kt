package com.yury

import com.yury.plugins.configureSecurity
import io.ktor.serialization.gson.*
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.plugins.contentnegotiation.*

fun main() {

    embeddedServer(Netty, port = 8080, host = "0.0.0.0", module = Application::module).start(wait = true)
}

fun Application.module() {

    install(ContentNegotiation) {
        gson {
            setPrettyPrinting()
        }
    }

    configureSecurity()
}


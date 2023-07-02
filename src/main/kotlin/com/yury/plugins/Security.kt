package com.yury.plugins

import com.auth0.jwt.JWT
import com.auth0.jwt.JWTVerifier
import com.auth0.jwt.algorithms.Algorithm
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import java.util.*
import kotlin.math.pow

data class TriangleRequest(val a: Int, val b: Int)
data class TriangleResponse(val hypotenuse: Double)

fun Application.configureSecurity() {

    install(Authentication) {
        jwt {
            verifier(JwtConfig.verifier)
            realm = "ktor.io"
            validate { credential ->
                if (credential.payload.audience.contains("jwt-audience")) {
                    JWTPrincipal(credential.payload)
                } else {
                    null
                }
            }
        }
    }

    routing {

        get("/token") {
            val token = JwtConfig.makeToken()
            call.respondText(token)
        }

        authenticate {
            post("/hypotenuse") {

                val request = call.receive<TriangleRequest>()

                if (request.a <= 0 || request.b <= 0) {
                    call.respond("A or B should be > 0")
                }

                val hypotenuse = Math.sqrt(request.a.toDouble().pow(2) + request.b.toDouble().pow(2))
                call.respond(TriangleResponse(hypotenuse))
            }
        }

    }

}

object JwtConfig {
    private const val secret = "ktor-secret"
    private const val issuer = "ktor.io"
    private const val audience = "jwt-audience"
    private val algorithm = Algorithm.HMAC256(secret)

    val verifier: JWTVerifier = JWT
        .require(algorithm)
        .withAudience(audience)
        .withIssuer(issuer)
        .build()

    fun makeToken(): String = JWT.create()
        .withAudience(audience)
        .withIssuer(issuer)
        .withExpiresAt(Date(System.currentTimeMillis() + 60_000_000))
        .sign(algorithm)
}




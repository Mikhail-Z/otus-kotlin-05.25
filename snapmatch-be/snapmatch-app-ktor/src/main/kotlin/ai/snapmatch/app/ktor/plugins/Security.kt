package ai.snapmatch.app.ktor.snapmatch.app.ktor.plugins

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import io.ktor.client.HttpClient
import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.auth.Authentication
import io.ktor.server.auth.jwt.JWTPrincipal
import io.ktor.server.auth.jwt.jwt
import java.security.PublicKey

fun Application.configureSecurity() {
    install(Authentication) {
        jwt("auth-jwt") {
            // Keycloak public key для валидации (получать из JWKS endpoint)
//            verifier(
//                JWT.require(Algorithm.RSA256(getKeycloakPublicKey(), null))
//                    .withIssuer("https://keycloak.example.com/realms/your-realm")
//                    .build()
//            )

            validate { credential ->
                // Keycloak автоматически валидирует токен
                // Возвращаем principal с userId
                if (credential.payload.subject != null) {
                    JWTPrincipal(credential.payload)
                } else {
                    null
                }
            }
        }
    }
}

//private suspend fun getKeycloakPublicKey(keycloakUrl: String, realm: String): RSAPublicKey {
//    // Проверяем кеш
//    val now = System.currentTimeMillis()
//    if (cachedPublicKey != null && (now - cacheTimestamp) < CACHE_TTL_MS) {
//        return cachedPublicKey!!
//    }
//
//    // Получаем ключи из Keycloak JWKS endpoint
//    val jwksUrl = "$keycloakUrl/realms/$realm/protocol/openid-connect/certs"
//
//    logger.info("Fetching Keycloak public keys from: $jwksUrl")
//
//    val httpClient = HttpClient(CIO) {
//        install(ContentNegotiation) {
//            json(Json {
//                ignoreUnknownKeys = true
//            })
//        }
//    }
//
//    try {
//        val jwks: JWKSResponse = httpClient.get(jwksUrl).body()
//
//        // Берем первый RSA ключ (обычно он используется для подписи)
//        val key = jwks.keys.firstOrNull { it.kty == "RSA" && it.use == "sig" }
//            ?: throw IllegalStateException("No RSA signing key found in JWKS")
//
//        // Конвертируем из JWK в RSAPublicKey
//        val publicKey = convertJWKToRSAPublicKey(key)
//
//        // Кешируем
//        cachedPublicKey = publicKey
//        cacheTimestamp = now
//
//        logger.info("Successfully loaded Keycloak public key (kid: ${key.kid})")
//
//        return publicKey
//
//    } catch (e: Exception) {
//        logger.error("Failed to fetch Keycloak public key", e)
//        throw e
//    } finally {
//        httpClient.close()
//    }
//}
//
//private fun convertJWKToRSAPublicKey(jwk: JWK): RSAPublicKey {
//    // Декодируем Base64URL
//    val modulusBytes = Base64.getUrlDecoder().decode(jwk.n)
//    val exponentBytes = Base64.getUrlDecoder().decode(jwk.e)
//
//    val modulus = BigInteger(1, modulusBytes)
//    val exponent = BigInteger(1, exponentBytes)
//
//    val keySpec = RSAPublicKeySpec(modulus, exponent)
//    val keyFactory = KeyFactory.getInstance("RSA")
//
//    return keyFactory.generatePublic(keySpec) as RSAPublicKey
//}
//
//// Data classes для парсинга JWKS response
//@Serializable
//data class JWKSResponse(
//    val keys: List<JWK>
//)
//
//@Serializable
//data class JWK(
//    val kty: String,           // Key Type (RSA)
//    val use: String? = null,   // Public Key Use (sig/enc)
//    val kid: String,           // Key ID
//    val n: String,             // Modulus
//    val e: String,             // Exponent
//    val alg: String? = null,   // Algorithm (RS256)
//    @SerialName("x5c")
//    val x5c: List<String>? = null  // X.509 Certificate Chain
//)
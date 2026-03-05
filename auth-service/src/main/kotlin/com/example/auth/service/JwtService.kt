package com.example.auth.service

import io.jsonwebtoken.Jwts
import io.jsonwebtoken.io.Decoders
import io.jsonwebtoken.security.Keys
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import java.util.Date

@Service
class JwtService(
    @Value("\${jwt.secret}") private val secret: String,
    @Value("\${jwt.access-token-expiry}") private val accessTokenExpirySeconds: Long,
) {
    private val signingKey by lazy {
        Keys.hmacShaKeyFor(Decoders.BASE64.decode(secret))
    }

    fun generateAccessToken(userId: Long, username: String, roles: List<String>): String {
        val now = Date()
        return Jwts.builder()
            .subject(userId.toString())
            .claim("username", username)
            .claim("roles", roles)
            .issuedAt(now)
            .expiration(Date(now.time + accessTokenExpirySeconds * 1000))
            .signWith(signingKey)
            .compact()
    }
}

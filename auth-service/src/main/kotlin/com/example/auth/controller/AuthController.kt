package com.example.auth.controller

import com.example.auth.dto.AuthResponse
import com.example.auth.dto.SignInRequest
import com.example.auth.dto.SignUpRequest
import com.example.auth.service.AuthService
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseCookie
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.server.ResponseStatusException
import org.springframework.web.server.ServerWebExchange

@RestController
@RequestMapping
class AuthController(
    private val authService: AuthService,
    @Value("\${jwt.refresh-token-expiry}") private val refreshTokenExpiry: Long,
    @Value("\${app.secure-cookies:true}") private val secureCookies: Boolean,
) {

    @PostMapping("/sign-up")
    @ResponseStatus(HttpStatus.CREATED)
    suspend fun signUp(
        @RequestBody request: SignUpRequest,
        exchange: ServerWebExchange,
    ): AuthResponse {
        val result = authService.signup(request)
        setRefreshCookie(exchange, result.refreshToken)
        return result.response
    }

    @PostMapping("/sign-in")
    suspend fun signIn(
        @RequestBody request: SignInRequest,
        exchange: ServerWebExchange,
    ): AuthResponse {
        val result = authService.signIn(request)
        setRefreshCookie(exchange, result.refreshToken)
        return result.response
    }

    @PostMapping("/refresh")
    suspend fun refresh(exchange: ServerWebExchange): AuthResponse {
        val refreshToken = exchange.request.cookies.getFirst("refresh-token")?.value
            ?: throw ResponseStatusException(HttpStatus.UNAUTHORIZED, "Missing refresh token")
        val result = authService.refresh(refreshToken)
        setRefreshCookie(exchange, result.refreshToken)
        return result.response
    }

    @PostMapping("/sign-out")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    suspend fun signOut(exchange: ServerWebExchange) {
        val refreshToken = exchange.request.cookies.getFirst("refresh-token")?.value
        if (refreshToken != null) {
            authService.logout(refreshToken)
        }
        clearRefreshCookie(exchange)
    }

    private fun setRefreshCookie(exchange: ServerWebExchange, refreshToken: String) {
        exchange.response.addCookie(
            ResponseCookie.from("refresh-token", refreshToken)
                .httpOnly(true)
                .secure(secureCookies)
                .sameSite("Strict")
                .path("/refresh")
                .maxAge(refreshTokenExpiry)
                .build()
        )
    }

    private fun clearRefreshCookie(exchange: ServerWebExchange) {
        exchange.response.addCookie(
            ResponseCookie.from("refresh-token", "")
                .httpOnly(true)
                .secure(secureCookies)
                .sameSite("Strict")
                .path("/refresh")
                .maxAge(0)
                .build()
        )
    }
}

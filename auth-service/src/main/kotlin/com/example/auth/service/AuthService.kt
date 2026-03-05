package com.example.auth.service

import com.example.auth.dto.AuthResponse
import com.example.auth.dto.SignInRequest
import com.example.auth.dto.SignUpRequest
import com.example.auth.entity.User
import com.example.auth.entity.UserRole
import com.example.auth.repository.RoleRepository
import com.example.auth.repository.UserRepository
import com.example.auth.repository.UserRoleRepository
import com.example.auth.security.UserDetailsServiceImpl
import kotlinx.coroutines.reactor.awaitSingle
import kotlinx.coroutines.reactor.awaitSingleOrNull
import org.springframework.beans.factory.annotation.Value
import org.springframework.data.redis.core.ReactiveStringRedisTemplate
import org.springframework.http.HttpStatus
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.web.server.ResponseStatusException
import java.time.Duration
import java.util.UUID

data class AuthResult(
    val accessToken: String,
    val refreshToken: String,
    val response: AuthResponse,
)

@Service
class AuthService(
    private val userDetailsService: UserDetailsServiceImpl,
    private val jwtService: JwtService,
    private val passwordEncoder: PasswordEncoder,
    private val redisTemplate: ReactiveStringRedisTemplate,
    private val userRepository: UserRepository,
    private val roleRepository: RoleRepository,
    private val userRoleRepository: UserRoleRepository,
    @Value("\${jwt.access-token-expiry}") private val accessTokenExpirySeconds: Long,
    @Value("\${jwt.refresh-token-expiry}") private val refreshTokenExpirySeconds: Long,
) {
    private fun refreshKey(token: String) = "auth:refresh:$token"

    suspend fun signup(request: SignUpRequest): AuthResult {
        if (userRepository.findByUsername(request.userName) != null) {
            throw ResponseStatusException(HttpStatus.CONFLICT, "Username already taken")
        }
        if (userRepository.findByEmail(request.email) != null) {
            throw ResponseStatusException(HttpStatus.CONFLICT, "Email already registered")
        }

        val user = userRepository.save(
            User(
                username = request.userName,
                email = request.email,
                password = passwordEncoder.encode(request.password)!!,
            )
        )

        val role = roleRepository.findByName("USER")
            ?: throw ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Default role not found")

        userRoleRepository.save(UserRole(userId = user.id!!, roleId = role.id!!))

        return issueTokens(user.id, user.username, user.email, listOf(role.name))
    }

    suspend fun signIn(request: SignInRequest): AuthResult {
        val user = userRepository.findByEmail(request.email)
            ?: throw ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid credentials")

        if (!user.enabled) {
            throw ResponseStatusException(HttpStatus.UNAUTHORIZED, "Account is disabled")
        }

        if (!passwordEncoder.matches(request.password, user.password)) {
            throw ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid credentials")
        }

        val userDetails = try {
            userDetailsService.findById(user.id!!).awaitSingle()
        } catch (e: UsernameNotFoundException) {
            throw ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid credentials")
        }

        val roles = userDetails.authorities.mapNotNull { it.authority }
        return issueTokens(user.id, user.username, user.email, roles)
    }

    suspend fun refresh(refreshToken: String): AuthResult {
        val userId = redisTemplate.opsForValue()
            .get(refreshKey(refreshToken))
            .awaitSingleOrNull()
            ?.toLongOrNull()
            ?: throw ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid or expired refresh token")

        val userDetails = try {
            userDetailsService.findById(userId).awaitSingle()
        } catch (e: UsernameNotFoundException) {
            throw ResponseStatusException(HttpStatus.UNAUTHORIZED, "User not found")
        }

        if (!userDetails.isEnabled) {
            throw ResponseStatusException(HttpStatus.UNAUTHORIZED, "Account is disabled")
        }

        val roles = userDetails.authorities.mapNotNull { it.authority }

        // Rotate refresh token
        redisTemplate.delete(refreshKey(refreshToken)).awaitSingle()

        return issueTokens(userDetails.id, userDetails.username, userDetails.email, roles)
    }

    suspend fun logout(refreshToken: String) {
        redisTemplate.delete(refreshKey(refreshToken)).awaitSingle()
    }

    private suspend fun issueTokens(userId: Long, username: String, email: String, roles: List<String>): AuthResult {
        val accessToken = jwtService.generateAccessToken(userId, username, roles)
        val refreshToken = UUID.randomUUID().toString()

        redisTemplate.opsForValue()
            .set(refreshKey(refreshToken), userId.toString(), Duration.ofSeconds(refreshTokenExpirySeconds))
            .awaitSingle()

        return AuthResult(
            accessToken = accessToken,
            refreshToken = refreshToken,
            response = AuthResponse(
                accessToken = accessToken,
                userId = userId,
                userName = username,
                email = email,
                authority = roles,
            ),
        )
    }
}

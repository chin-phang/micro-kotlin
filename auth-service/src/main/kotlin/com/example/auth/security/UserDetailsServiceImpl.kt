package com.example.auth.security

import com.example.auth.entity.User
import com.example.auth.repository.RoleRepository
import com.example.auth.repository.UserRepository
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.reactor.mono
import org.springframework.security.core.userdetails.ReactiveUserDetailsPasswordService
import org.springframework.security.core.userdetails.ReactiveUserDetailsService
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono

@Service
class UserDetailsServiceImpl(
    private val userRepository: UserRepository,
    private val roleRepository: RoleRepository,
) : ReactiveUserDetailsService, ReactiveUserDetailsPasswordService {

    override fun findByUsername(username: String): Mono<UserDetails> = mono {
        val user = userRepository.findByUsername(username)
            ?: throw UsernameNotFoundException("User not found: $username")
        buildUserDetails(user)
    }

    fun findById(userId: Long): Mono<UserDetailsImpl> = mono {
        val user = userRepository.findById(userId)
            ?: throw UsernameNotFoundException("User not found: $userId")
        buildUserDetails(user)
    }

    override fun updatePassword(user: UserDetails, newPassword: String?): Mono<UserDetails> = mono {
        val existing = userRepository.findByUsername(user.username)
            ?: throw UsernameNotFoundException("User not found: ${user.username}")
        val saved = userRepository.save(existing.copy(password = newPassword ?: existing.password))
        buildUserDetails(saved)
    }

    private suspend fun buildUserDetails(user: User): UserDetailsImpl {
        val roles = roleRepository.findByUserId(user.id!!)
            .map { it.name }
            .toList()
        return UserDetailsImpl(
            id = user.id,
            username = user.username,
            password = user.password,
            enabled = user.enabled,
            email = user.email,
            roles = roles,
        )
    }
}

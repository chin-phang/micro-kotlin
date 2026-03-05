package com.example.auth.security

import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.UserDetails

class UserDetailsImpl(
    val id: Long,
    private val username: String,
    private val password: String,
    private val enabled: Boolean,
    val email: String,
    roles: List<String>,
) : UserDetails {

    private val authorities: List<GrantedAuthority> = roles.map { SimpleGrantedAuthority(it) }

    override fun getAuthorities(): Collection<GrantedAuthority> = authorities
    override fun getPassword(): String = password
    override fun getUsername(): String = username
    override fun isEnabled(): Boolean = enabled
    override fun isAccountNonExpired(): Boolean = true
    override fun isAccountNonLocked(): Boolean = true
    override fun isCredentialsNonExpired(): Boolean = true
}

package com.example.auth

import com.example.auth.repository.RoleRepository
import com.example.auth.repository.UserRepository
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.data.redis.core.ReactiveStringRedisTemplate
import org.springframework.test.context.bean.override.mockito.MockitoBean

@SpringBootTest
class AuthApplicationTests {

	@MockitoBean
	private lateinit var userRepository: UserRepository

	@MockitoBean
	private lateinit var roleRepository: RoleRepository

	@MockitoBean
	private lateinit var reactiveStringRedisTemplate: ReactiveStringRedisTemplate

	@Test
	fun contextLoads() {
	}

}

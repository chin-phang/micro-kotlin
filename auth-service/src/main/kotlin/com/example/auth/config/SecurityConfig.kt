package com.example.auth.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpMethod
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity
import org.springframework.security.config.web.server.ServerHttpSecurity
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.web.server.SecurityWebFilterChain
import org.springframework.security.web.server.csrf.CookieServerCsrfTokenRepository
import org.springframework.security.web.server.csrf.XorServerCsrfTokenRequestAttributeHandler
import org.springframework.security.web.server.util.matcher.ServerWebExchangeMatcher

@Configuration
@EnableWebFluxSecurity
class SecurityConfig {

    @Bean
    fun passwordEncoder(): PasswordEncoder = BCryptPasswordEncoder()

    @Bean
    fun securityWebFilterChain(http: ServerHttpSecurity): SecurityWebFilterChain =
        http
            .csrf { csrf ->
                // Store CSRF token in a JS-readable cookie (XSRF-TOKEN).
                // Axios reads it automatically and sends it as X-XSRF-TOKEN header.
                csrf.csrfTokenRepository(CookieServerCsrfTokenRepository.withHttpOnlyFalse())
                csrf.csrfTokenRequestHandler(XorServerCsrfTokenRequestAttributeHandler())
                // Sign-in and sign-up are exempt: the client has no session yet and therefore
                // no CSRF cookie. All other state-changing requests require the XSRF token.
                csrf.requireCsrfProtectionMatcher(ServerWebExchangeMatcher { exchange ->
                    val safeMethods = setOf(
                        HttpMethod.GET, HttpMethod.HEAD, HttpMethod.TRACE, HttpMethod.OPTIONS
                    )
                    val path = exchange.request.path.value()
                    val method = exchange.request.method
                    when {
                        method in safeMethods -> ServerWebExchangeMatcher.MatchResult.notMatch()
                        path == "/sign-in" || path == "/sign-up" -> ServerWebExchangeMatcher.MatchResult.notMatch()
                        else -> ServerWebExchangeMatcher.MatchResult.match()
                    }
                })
            }
            .httpBasic { it.disable() }
            .formLogin { it.disable() }
            .authorizeExchange { auth ->
                auth.pathMatchers("/sign-in", "/refresh", "/sign-out", "/sign-up", "/actuator/health").permitAll()
                auth.anyExchange().authenticated()
            }
            .build()
}

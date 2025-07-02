package com.jobpulse.auth_service.config;

import java.nio.charset.StandardCharsets;

import javax.crypto.SecretKey;

import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import io.jsonwebtoken.security.Keys;

@org.springframework.context.annotation.Configuration
public class SecurityConfig {


    @Bean
    public JwtDecoder jwtDecoder(@Value("${jwt.secret}") String jwtSecret) {
        SecretKey key = Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));
        
        return NimbusJwtDecoder.withSecretKey(key).build();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/ping", "/ping/", "/auth/register", "/auth/register/", "/auth/login", "/auth/login/").permitAll()
                .anyRequest().authenticated()
            )
            .oauth2ResourceServer(oauth2 -> oauth2.jwt(jwt -> {}));
        return http.build();
    }
    //  .oauth2ResourceServer(oauth2 -> oauth2.jwt());
}
package com.niolikon.taskboard.service.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.web.SecurityFilterChain;

import static com.niolikon.taskboard.service.todo.controller.TodoApiPaths.SECURITY_PATTERN_TODO_EXACT;
import static com.niolikon.taskboard.service.todo.controller.TodoApiPaths.SECURITY_PATTER_TODO_ALL;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http, JwtAuthenticationConverter authenticationConverter) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(SECURITY_PATTER_TODO_ALL).hasRole("USER")
                        .requestMatchers(SECURITY_PATTERN_TODO_EXACT).hasRole("USER")
                        .anyRequest().authenticated()
                )
                .sessionManagement(sess -> sess.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .oauth2ResourceServer(oauth2 -> oauth2.jwt(jwt -> jwt.jwtAuthenticationConverter(authenticationConverter)));

        return http.build();
    }
}

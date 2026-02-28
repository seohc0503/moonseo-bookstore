package com.moonseo.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.moonseo.security.jwt.JwtAccessDeniedHandler;
import com.moonseo.security.jwt.JwtAuthenticationEntryPoint;
import com.moonseo.security.jwt.JwtAuthenticationFilter;
import com.moonseo.security.jwt.JwtProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
public class SecurityConfig {
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http, JwtProvider jwtProvider, ObjectMapper om) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                .addFilterBefore(new JwtAuthenticationFilter(jwtProvider), UsernamePasswordAuthenticationFilter.class)
                .httpBasic(Customizer.withDefaults())

                .exceptionHandling(eh -> eh
                        .accessDeniedHandler(new JwtAccessDeniedHandler(om))
                        .authenticationEntryPoint(new JwtAuthenticationEntryPoint(om))
                )
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/health", "/api/ping").permitAll()
                        .requestMatchers("/api/dev/**").permitAll()
                        .requestMatchers("/api/admin/**").hasRole("ADMIN")
                        .anyRequest().authenticated()
                );
        return http.build();
    }
}

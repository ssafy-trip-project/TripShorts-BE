package com.trip.tripshorts.config;


import com.trip.tripshorts.auth.JwtAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.util.Arrays;
import java.util.List;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .cors(cors -> cors
                        .configurationSource(request -> {
                            var corsConfiguration = new org.springframework.web.cors.CorsConfiguration();
                            corsConfiguration.setAllowedOrigins(Arrays.asList("https://d3sspkhgtlkiph.cloudfront.net", "http://localhost:5173"));
                            corsConfiguration.setAllowedMethods(List.of("*"));
                            corsConfiguration.setAllowedHeaders(List.of("*"));
                            return corsConfiguration.applyPermitDefaultValues();
                        }))
                .csrf(csrf -> csrf.disable())
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> {
                    // TODO : 나중에 수정 필요
                    auth.anyRequest().permitAll();
                })
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}

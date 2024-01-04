package com.backoffice.upjuyanolja.global.security;

import com.backoffice.upjuyanolja.global.security.jwt.JwtAccessDeniedHandler;
import com.backoffice.upjuyanolja.global.security.jwt.JwtAuthenticationEntryPoint;
import com.backoffice.upjuyanolja.global.security.jwt.JwtAuthenticationFilter;
import com.backoffice.upjuyanolja.global.security.jwt.JwtTokenProvider;
import java.util.Arrays;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class AuthenticationConfig {

    private final JwtTokenProvider jwtTokenProvider;
    private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;
    private final JwtAccessDeniedHandler jwtAccessDeniedHandler;
    private static final String[] PERMIT_URL_ARRAY = {
        "/",
        "/error",
        "/docs/**",
        "/api/members/**",
        "/api/products/**",
        "/api/open-api"
    };

    @Bean
    SecurityFilterChain http(HttpSecurity http) throws Exception {

        http
            .formLogin(AbstractHttpConfigurer::disable)
            .cors(cors -> cors.configurationSource(request -> {
                CorsConfiguration configuration = new CorsConfiguration();
                configuration.setAllowCredentials(true);
//                configuration.setAllowedOrigins(
//                    Arrays.asList("http://localhost:3000", "http://localhost:3001", "https://candid-horse-912de6.netlify.app"));
                configuration.setAllowedHeaders(Arrays.asList("*"));
                configuration.setAllowedMethods(
                    Arrays.asList("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS"));
                configuration.addExposedHeader("X-AUTH-TOKEN");
                return configuration;
            }))
            .csrf(AbstractHttpConfigurer::disable)
            .sessionManagement(sessionConfig ->
                sessionConfig.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        http.authorizeHttpRequests(request -> request.

            requestMatchers("/v1/reservations/**").authenticated()
            .requestMatchers("/v1/carts/**").authenticated()
            .anyRequest().permitAll()
        );
        http.addFilterBefore(new JwtAuthenticationFilter(jwtTokenProvider),
            UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }
}

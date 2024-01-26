package com.backoffice.upjuyanolja.global.security;

import com.backoffice.upjuyanolja.global.config.CustomCorsConfiguration;
import com.backoffice.upjuyanolja.global.security.jwt.JwtAccessDeniedHandler;
import com.backoffice.upjuyanolja.global.security.jwt.JwtAuthenticationEntryPoint;
import com.backoffice.upjuyanolja.global.security.jwt.JwtAuthenticationFilter;
import com.backoffice.upjuyanolja.global.security.jwt.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class AuthenticationConfig {

    private final JwtTokenProvider jwtTokenProvider;
    private final CustomCorsConfiguration corsConfiguration;
    private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;
    private final JwtAccessDeniedHandler jwtAccessDeniedHandler;
    private static final String[] PERMIT_ALL_URL_ARRAY = {
        "/",
        "/error",
        "/docs/**",
        "/api/auth/**",
        "/api/open-api"
    };
    private static final String[] PERMIT_OWNER_URL_ARRAY = {
        "/backoffice-api/**",
        "/api/coupons/backoffice/**",
        "/api/points/**",
    };

    @Bean
    SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.httpBasic(AbstractHttpConfigurer::disable)
            .formLogin(AbstractHttpConfigurer::disable)
            .csrf(AbstractHttpConfigurer::disable)
            .addFilter(corsConfiguration.corsFilter())
            .sessionManagement(sessionConfig ->
                sessionConfig.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(request -> request
                .requestMatchers(PERMIT_ALL_URL_ARRAY)
                .permitAll()
                .requestMatchers(HttpMethod.GET, "/api/accommodations/**")
                .permitAll()
                .requestMatchers(HttpMethod.GET, "/api/coupons/")
                .permitAll()
                .requestMatchers("/api/reservations/**")
                .hasRole("USER")
                .requestMatchers(PERMIT_OWNER_URL_ARRAY)
                .hasRole("ADMIN")
                .requestMatchers(HttpMethod.POST, "/api/accommodations/**")
                .hasRole("ADMIN")
                .requestMatchers("/api/auth/logout")
                .hasAnyRole("USER", "ADMIN")
                .anyRequest().authenticated())
            .addFilterBefore(new JwtAuthenticationFilter(jwtTokenProvider),
                UsernamePasswordAuthenticationFilter.class)
            .exceptionHandling(AuthenticationManager -> AuthenticationManager
                .authenticationEntryPoint(jwtAuthenticationEntryPoint)
                .accessDeniedHandler(jwtAccessDeniedHandler));
        return http.build();
    }
}

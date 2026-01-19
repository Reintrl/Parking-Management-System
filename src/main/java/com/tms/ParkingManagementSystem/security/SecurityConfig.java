package com.tms.ParkingManagementSystem.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtFilter;

    public SecurityConfig(JwtAuthenticationFilter jwtFilter) {
        this.jwtFilter = jwtFilter;
    }

    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    AuthenticationManager authenticationManager(AuthenticationConfiguration cfg) throws Exception {
        return cfg.getAuthenticationManager();
    }

    @Bean
    SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/auth/**", "/swagger-ui/**", "/v3/api-docs/**").permitAll()
                        .requestMatchers(HttpMethod.GET,
                                "/parkingLot",
                                "/parkingLot/{id}",
                                "/parkingLot/{id}/spots/available",
                                "/parkingSession/{id}",
                                "/parkingSession/vehicle/{vehicleId}",
                                "/reservation/{id}",
                                "/reservation/vehicle/{vehicleId}",
                                "/spot",
                                "/spot/{id}",
                                "/spot/parkingLot/{parkingLotId}",
                                "/tariff",
                                "/tariff/{id}",
                                "/vehicle/{id}",
                                "/vehicle/user/{userId}"
                        ).hasAnyAuthority("USER", "OPERATOR", "ADMIN")

                        .requestMatchers(HttpMethod.GET,
                                "/parkingLot/{id}/dashboard",
                                "/parkingSession",
                                "/parkingSession/spot/{spotId}",
                                "/reservation",
                                "/reservation/spot/{spotId}",
                                "/user",
                                "/user/{id}",
                                "/vehicle"
                        ).hasAnyAuthority("OPERATOR", "ADMIN")

                        .requestMatchers(HttpMethod.POST,
                                "/reservation",
                                "/reservation/{id}/cancel",
                                "/vehicle"
                        ).hasAnyAuthority("USER", "OPERATOR", "ADMIN")

                        .requestMatchers(HttpMethod.POST,
                                "/parkingLot",
                                "/parkingLot/with-spots",
                                "/parkingSession",
                                "/parkingSession/from-reservation/{reservationId}",
                                "/parkingSession/{id}/finish",
                                "/spot",
                                "/tariff"
                        ).hasAnyAuthority("OPERATOR", "ADMIN")

                        .requestMatchers(HttpMethod.PUT,
                                "/parkingLot/{id}",
                                "/spot/{id}",
                                "/tariff/{id}"
                        ).hasAnyAuthority("OPERATOR", "ADMIN")

                        .requestMatchers(HttpMethod.PUT,
                                "/reservation/{id}",
                                "/vehicle/{id}"
                        ).hasAnyAuthority("USER", "OPERATOR", "ADMIN")

                        .requestMatchers(HttpMethod.PUT,
                                "/user/{id}"
                        ).hasAuthority("ADMIN")

                        .requestMatchers(HttpMethod.PATCH,
                                "/parkingLot/{id}/status",
                                "/reservation/{id}/status",
                                "/spot/{id}/status",
                                "/user/{id}/status"
                        ).hasAnyAuthority("OPERATOR", "ADMIN")

                        .requestMatchers(HttpMethod.PATCH,
                                "/user/soft/{id}"
                        ).hasAnyAuthority("USER", "ADMIN")

                        .requestMatchers(HttpMethod.DELETE,
                                "/parkingLot/{id}",
                                "/parkingSession/{id}",
                                "/reservation/{id}",
                                "/spot/{id}",
                                "/tariff/{id}",
                                "/user",
                                "/user/{id}"
                        ).hasAuthority("ADMIN")

                        .requestMatchers(HttpMethod.DELETE,
                                "/vehicle/{id}"
                        ).hasAnyAuthority("USER", "ADMIN")

                        .requestMatchers("/me/**")
                        .hasAnyAuthority("USER", "OPERATOR", "ADMIN")

                        .anyRequest().authenticated()
                );
        http.addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }
}

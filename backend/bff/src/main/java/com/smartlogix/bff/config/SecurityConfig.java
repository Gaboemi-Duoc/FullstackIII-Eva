package com.smartlogix.bff.config;

import com.smartlogix.bff.security.JwtAuthFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final JwtAuthFilter jwtAuthFilter;

    public SecurityConfig(JwtAuthFilter jwtAuthFilter) {
	this.jwtAuthFilter = jwtAuthFilter;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http)
	    throws Exception {

	http
		.csrf(csrf -> csrf.disable())
		.cors(Customizer.withDefaults())
		.sessionManagement(session ->
			session.sessionCreationPolicy(
				SessionCreationPolicy.STATELESS
			)
		)
		.authorizeHttpRequests(auth -> auth
			// LOGIN endpoint
			.requestMatchers("/api/bff/users/login")
			.permitAll()
			
			// HEALTH endpoint
			.requestMatchers("/api/bff/health")
			.permitAll()
			
			// SWAGGER UI v3 (SpringDoc)
			.requestMatchers(
			"/swagger-ui/**",
			"/swagger-ui.html",
			"/v3/api-docs/**",
			"/v3/api-docs.yaml",
			"/swagger-resources/**",
			"/swagger-resources",
			"/webjars/**"
			)
			.permitAll()
			
			// Actuator endpoints (if using)
			.requestMatchers("/actuator/**", "/health")
			.permitAll()
			
			// All other endpoints require authentication
			.anyRequest()
			.authenticated()
		)
		// JWT FILTER
		.addFilterBefore(
			jwtAuthFilter,
			UsernamePasswordAuthenticationFilter.class
		);

	return http.build();
    }
}
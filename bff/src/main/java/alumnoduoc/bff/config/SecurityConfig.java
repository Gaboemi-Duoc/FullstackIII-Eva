package alumnoduoc.bff.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;

import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http
                // Desactiva CSRF
                .csrf(csrf -> csrf.disable())

                // Habilita CORS
                .cors(Customizer.withDefaults())

                // Configuración de rutas
                .authorizeHttpRequests(auth -> auth

                        // LOGIN
                        .requestMatchers("/api/bff/users/login").permitAll()

                        // SWAGGER
                        .requestMatchers("/swagger-ui/**").permitAll()
                        .requestMatchers("/v3/api-docs/**").permitAll()

                        // Todo lo demás requiere autenticación
                        .anyRequest().authenticated()
                );

        return http.build();
    }
}
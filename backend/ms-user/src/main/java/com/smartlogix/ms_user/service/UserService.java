package com.smartlogix.ms_user.service;

import com.smartlogix.ms_user.model.User;
import com.smartlogix.ms_user.exception.InvalidCredentialsException;
import java.security.Key;
import java.util.Date;
import java.util.List;

import com.smartlogix.ms_user.repository.UserRepository;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final EmailService emailService;

    // Must match the "secret" configured under auth/validator in krakend.json
    @Value("${jwt.secret}")
    private String jwtSecret;

    // Token lifetime in milliseconds (1 hour)
    private static final long JWT_EXPIRATION_MS = 3_600_000;

    public UserService(UserRepository userRepository, EmailService emailService) {
        this.userRepository = userRepository;
        this.emailService = emailService;
    }

    public List<User> listarUsuarios() {
        return userRepository.findAll();
    }

    public User obtenerPorId(Long id) {
        return userRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
    }

    public User registrarUsuario(User user) {
        try {
            User nuevoUsuario = userRepository.save(user);

            // Send welcome email, but don't fail if it doesn't work
            sendWelcomeEmailSafely(nuevoUsuario);

            return nuevoUsuario;
        } catch (DataAccessException e) {
            throw new RuntimeException("Error al registrar usuario", e);
        }
    }

    private void sendWelcomeEmailSafely(User user) {
        try {
            emailService.enviarCorreoBienvenida(user.getEmail(), user.getUsername());
        } catch (Exception e) {
            System.out.println("Failed to send welcome email for user " + user.getId_user() + ": " + e.getMessage());
            // Optionally, store in a retry queue or database for later processing
        }
    }

    public User login(String username, String password) {
        return userRepository.findByUsernameAndPassword(username, password)
            .orElseThrow(() -> new InvalidCredentialsException("Credenciales incorrectas"));
    }

    /**
     * Generates a JWT signed with HS256 using the same secret KrakenD's
     * auth/validator uses to verify incoming Authorization headers.
     */
    public String generarToken(User user) {
        Key key = Keys.hmacShaKeyFor(jwtSecret.getBytes());
        long now = System.currentTimeMillis();

        return Jwts.builder()
                .setHeaderParam("kid", "smartlogix-key-1")
                .setSubject(String.valueOf(user.getId_user()))
                .claim("username", user.getUsername())
                .setIssuedAt(new Date(now))
                .setExpiration(new Date(now + JWT_EXPIRATION_MS))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    public User actualizarUsername(Long id, String nuevoUsername) {
        User user = obtenerPorId(id);
        user.setUsername(nuevoUsername);
        return userRepository.save(user);
    }

    public void eliminarUsuario(Long id) {
        userRepository.deleteById(id);
    }
}
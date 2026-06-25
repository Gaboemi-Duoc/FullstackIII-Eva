package com.smartlogix.ms_user.service;

import com.smartlogix.ms_user.model.User;
import java.util.List;

import com.smartlogix.ms_user.repository.UserRepository;

import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final EmailService emailService;

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
            // Log the error but don't rethrow - user is already saved
            System.out.println("Failed to send welcome email for user {}: {}", user.getId(), e.getMessage());
            // Optionally, store in a retry queue or database for later processing
        }
    }

    public User login(String username, String password) {
        return userRepository.findByUsernameAndPassword(username, password)
            .orElseThrow(() -> new RuntimeException("Credenciales incorrectas"));
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

package com.smartlogix.ms_user.service;

import com.smartlogix.ms_user.model.User;
import java.util.List;

import com.smartlogix.ms_user.repository.UserRepository;

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
        User nuevoUsuario = userRepository.save(user);

        try {
            emailService.enviarCorreoBienvenida(nuevoUsuario.getEmail(), nuevoUsuario.getUsername());
        } catch (Exception e) {
            // No queremos que falle el registro si el correo no se pudo enviar
            System.err.println("No se pudo enviar el correo de bienvenida: " + e.getMessage());
        }

        return nuevoUsuario;
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

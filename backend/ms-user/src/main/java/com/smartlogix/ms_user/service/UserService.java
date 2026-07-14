package com.smartlogix.ms_user.service;

import com.smartlogix.ms_user.model.User;
import java.util.List;

import com.smartlogix.ms_user.repository.UserRepository;

import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;

/**
 * Servicio que contiene la lógica de negocio para la gestión de cuentas de usuario.
 * <p>
 * Encapsula las operaciones de registro, autenticación, consulta, actualización y
 * eliminación de usuarios, delegando la persistencia en {@link UserRepository} y
 * el envío de notificaciones en {@link EmailService}.
 */
@Service
public class UserService {

    private final UserRepository userRepository;
    private final EmailService emailService;

    /**
     * Crea una nueva instancia del servicio de usuarios.
     *
     * @param userRepository repositorio utilizado para acceder y persistir los usuarios
     * @param emailService servicio utilizado para enviar correos de notificación
     */
    public UserService(UserRepository userRepository, EmailService emailService) {
        this.userRepository = userRepository;
        this.emailService = emailService;
    }

    /**
     * Obtiene el listado completo de usuarios registrados.
     *
     * @return lista con todos los usuarios existentes
     */
    public List<User> listarUsuarios() {
        return userRepository.findAll();
    }

    /**
     * Busca un usuario por su identificador único.
     *
     * @param id identificador del usuario a buscar
     * @return el usuario encontrado
     * @throws RuntimeException si no existe un usuario con el id indicado
     */
    public User obtenerPorId(Long id) {
        return userRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
    }

    /**
     * Registra un nuevo usuario y le envía un correo de bienvenida.
     * <p>
     * El envío del correo se realiza de forma segura: si falla, no interrumpe
     * el registro del usuario.
     *
     * @param user datos del usuario a registrar
     * @return el usuario creado, ya persistido con su identificador asignado
     * @throws RuntimeException si ocurre un error de acceso a datos al registrar el usuario
     */
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
    
    /**
     * Envía el correo de bienvenida a un usuario recién registrado, capturando
     * cualquier excepción para que una falla en el envío no afecte el registro.
     *
     * @param user usuario al que se le enviará el correo de bienvenida
     */
    private void sendWelcomeEmailSafely(User user) {
        try {
            emailService.enviarCorreoBienvenida(user.getEmail(), user.getUsername());
        } catch (Exception e) {
            System.out.println("Failed to send welcome email for user " + user.getId_user() + ": " + e.getMessage());
            // Optionally, store in a retry queue or database for later processing
        }
    }

    /**
     * Valida las credenciales de un usuario para iniciar sesión.
     *
     * @param username nombre de usuario
     * @param password contraseña asociada
     * @return el usuario autenticado
     * @throws RuntimeException si las credenciales no coinciden con ningún usuario
     */
    public User login(String username, String password) {
        return userRepository.findByUsernameAndPassword(username, password)
            .orElseThrow(() -> new RuntimeException("Credenciales incorrectas"));
    }

    /**
     * Actualiza el nombre de usuario de una cuenta existente.
     *
     * @param id identificador del usuario a actualizar
     * @param nuevoUsername nuevo username a asignar
     * @return el usuario actualizado
     * @throws RuntimeException si no existe un usuario con el id indicado
     */
    public User actualizarUsername(Long id, String nuevoUsername) {
        User user = obtenerPorId(id);
        user.setUsername(nuevoUsername);
        return userRepository.save(user);
    }

    /**
     * Elimina una cuenta de usuario a partir de su identificador.
     *
     * @param id identificador del usuario a eliminar
     */
    public void eliminarUsuario(Long id) {
        userRepository.deleteById(id);
    }
}
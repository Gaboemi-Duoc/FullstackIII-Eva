package alumnoduoc.user_service;

import java.util.List;

import org.springframework.stereotype.Service;

@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public List<User> listarUsuarios() {
        return userRepository.findAll();
    }

    public User obtenerPorId(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
    }

    public User registrarUsuario(User user) {
        return userRepository.save(user);
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

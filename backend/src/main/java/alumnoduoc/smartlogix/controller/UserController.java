package alumnoduoc.smartlogix.controller;

import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import alumnoduoc.smartlogix.model.User;
import alumnoduoc.smartlogix.service.UserService;


@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = "http://localhost:5173")

public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public ResponseEntity<List<User>> listarUsuarios() {
        return ResponseEntity.ok(userService.listarUsuarios());
    }

    @GetMapping("/{id}")
    public ResponseEntity<User> obtenerUsuario(@PathVariable Long id) {
        return ResponseEntity.ok(userService.obtenerPorId(id));
    }

    @PostMapping("/register")
    public ResponseEntity<User> registrarUsuario(@RequestBody User user) {
        return ResponseEntity.ok(userService.registrarUsuario(user));
    }

    @PostMapping("/login")
    public ResponseEntity<User> login(@RequestBody Map<String, String> datos) {
        String username = datos.get("username");
        String password = datos.get("password");

        User user = userService.login(username, password);

        return ResponseEntity.ok(user);
    }

    @PutMapping("/{id}/username")
    public ResponseEntity<User> actualizarUsername(
            @PathVariable Long id,
            @RequestBody Map<String, String> datos) {

        String nuevoUsername = datos.get("username");

        User userActualizado = userService.actualizarUsername(id, nuevoUsername);

        return ResponseEntity.ok(userActualizado);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarUsuario(@PathVariable Long id) {
        userService.eliminarUsuario(id);
        return ResponseEntity.noContent().build();
    }
}
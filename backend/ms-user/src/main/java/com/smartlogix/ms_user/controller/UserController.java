package com.smartlogix.ms_user.controller;

import com.smartlogix.ms_user.model.User;
import java.util.List;
import java.util.Map;

import com.smartlogix.ms_user.service.UserService;

import com.smartlogix.ms_user.dto.RegisterUserRequest;
import com.smartlogix.ms_user.dto.LoginRequest;
import com.smartlogix.ms_user.dto.UpdateUsernameRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import org.springframework.validation.annotation.Validated;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = "*")
@Validated
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/{id}")
    public ResponseEntity<User> obtenerUsuario(@PathVariable @Min(1) Long id) {
        return ResponseEntity.ok(userService.obtenerPorId(id));
    }

    @PostMapping("/register")
    public ResponseEntity<User> registrarUsuario(@Valid @RequestBody RegisterUserRequest request) {
        User user = new User();
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setPassword(request.getPassword());

        return ResponseEntity.ok(userService.registrarUsuario(user));
    }

    @PostMapping("/login")
    public ResponseEntity<User> login(@Valid @RequestBody LoginRequest request) {
        User user = userService.login(request.getUsername(), request.getPassword());
        return ResponseEntity.ok(user);
    }

    @PutMapping("/{id}/username")
    public ResponseEntity<User> actualizarUsername(
            @PathVariable @Min(1) Long id,
            @Valid @RequestBody UpdateUsernameRequest request) {

        User userActualizado = userService.actualizarUsername(id, request.getUsername());
        return ResponseEntity.ok(userActualizado);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarUsuario(@PathVariable @Min(1) Long id) {
        userService.eliminarUsuario(id);
        return ResponseEntity.noContent().build();
    }

}

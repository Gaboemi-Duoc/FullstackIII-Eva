package com.smartlogix.ms_user.controller;

import com.smartlogix.ms_user.model.User;
import java.util.List;
import java.util.Map;

import com.smartlogix.ms_user.service.UserService;

import com.smartlogix.ms_user.dto.RegisterUserRequest;
import com.smartlogix.ms_user.dto.LoginRequest;
import com.smartlogix.ms_user.dto.UpdateUsernameRequest;
import com.smartlogix.ms_user.dto.UserResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import org.springframework.validation.annotation.Validated;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = "*")
@Validated
@Tag(name = "Gestion de Usuarios", description = "Registro, autenticación y administración de perfiles de usuario")
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @Operation(summary = "Obtener usuario por id", description = "Retorna un usuario según su identificador único.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Usuario encontrado"),
            @ApiResponse(responseCode = "404", description = "Usuario no encontrado")
    })
    @GetMapping("/{id}")
    public ResponseEntity<UserResponse> obtenerUsuario(@PathVariable @Min(1) Long id) {
        return ResponseEntity.ok(UserResponse.from(userService.obtenerPorId(id)));
    }

    @Operation(summary = "Registrar usuario", description = "Crea una nueva cuenta de usuario en la plataforma.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Usuario registrado correctamente"),
            @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos"),
            @ApiResponse(responseCode = "409", description = "El usuario ya existe")
    })
    @PostMapping("/register")
    public ResponseEntity<UserResponse> registrarUsuario(@Valid @RequestBody RegisterUserRequest request) {
        User user = new User();
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setPassword(request.getPassword());

        return ResponseEntity.ok(UserResponse.from(userService.registrarUsuario(user)));
    }

    @Operation(summary = "Login de usuario", description = "Valida las credenciales de un usuario existente.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Login exitoso"),
            @ApiResponse(responseCode = "401", description = "Credenciales inválidas")
    })
    @PostMapping("/login")
    public ResponseEntity<UserResponse> login(@Valid @RequestBody LoginRequest request) {
        User user = userService.login(request.getUsername(), request.getPassword());
        return ResponseEntity.ok(UserResponse.from(user));
    }

    @Operation(summary = "Actualizar username", description = "Modifica el nombre de usuario de una cuenta existente.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Username actualizado correctamente"),
            @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos"),
            @ApiResponse(responseCode = "404", description = "Usuario no encontrado")
    })
    @PutMapping("/{id}/username")
    public ResponseEntity<UserResponse> actualizarUsername(
            @PathVariable @Min(1) Long id,
            @Valid @RequestBody UpdateUsernameRequest request) {

        User userActualizado = userService.actualizarUsername(id, request.getUsername());
        return ResponseEntity.ok(UserResponse.from(userActualizado));
    }

    @Operation(summary = "Eliminar usuario", description = "Elimina una cuenta de usuario según su identificador.")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Usuario eliminado correctamente"),
            @ApiResponse(responseCode = "404", description = "Usuario no encontrado")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarUsuario(@PathVariable @Min(1) Long id) {
        userService.eliminarUsuario(id);
        return ResponseEntity.noContent().build();
    }

}
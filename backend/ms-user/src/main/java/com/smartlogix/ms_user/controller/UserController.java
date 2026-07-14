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

/**
 * Controlador REST encargado de exponer las operaciones de gestión de usuarios.
 * <p>
 * Provee endpoints para el registro, autenticación, consulta, actualización y
 * eliminación de cuentas de usuario. Las rutas expuestas por este controlador
 * están bajo el prefijo {@code /api/users}.
 */
@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = "*")
@Validated
@Tag(name = "Gestion de Usuarios", description = "Registro, autenticación y administración de perfiles de usuario")
public class UserController {
    private final UserService userService;

    /**
     * Crea una nueva instancia del controlador de usuarios.
     *
     * @param userService servicio que contiene la lógica de negocio de los usuarios
     */
    public UserController(UserService userService) {
        this.userService = userService;
    }

    /**
     * Obtiene un usuario a partir de su identificador único.
     *
     * @param id identificador del usuario, debe ser mayor o igual a 1
     * @return el usuario encontrado envuelto en un {@link ResponseEntity} con estado 200
     */
    @Operation(summary = "Obtener usuario por id", description = "Retorna un usuario según su identificador único.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Usuario encontrado"),
            @ApiResponse(responseCode = "404", description = "Usuario no encontrado")
    })
    @GetMapping("/{id}")
    public ResponseEntity<UserResponse> obtenerUsuario(@PathVariable @Min(1) Long id) {
        return ResponseEntity.ok(UserResponse.from(userService.obtenerPorId(id)));
    }

    /**
     * Registra una nueva cuenta de usuario en la plataforma.
     *
     * @param request datos necesarios para crear la cuenta (username, email y contraseña)
     * @return el usuario registrado envuelto en un {@link ResponseEntity} con estado 200
     */
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

    /**
     * Valida las credenciales de un usuario existente para iniciar sesión.
     *
     * @param request credenciales de acceso (username y contraseña)
     * @return el usuario autenticado envuelto en un {@link ResponseEntity} con estado 200
     */
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

    /**
     * Actualiza el nombre de usuario de una cuenta existente.
     *
     * @param id identificador del usuario a actualizar, debe ser mayor o igual a 1
     * @param request datos con el nuevo username a asignar
     * @return el usuario actualizado envuelto en un {@link ResponseEntity} con estado 200
     */
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

    /**
     * Elimina una cuenta de usuario a partir de su identificador.
     *
     * @param id identificador del usuario a eliminar, debe ser mayor o igual a 1
     * @return respuesta sin contenido envuelta en un {@link ResponseEntity} con estado 204
     */
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
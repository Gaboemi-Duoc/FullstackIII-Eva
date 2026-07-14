package com.smartlogix.ms_user.dto;

import com.smartlogix.ms_user.model.User;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO de salida para exponer datos de usuario sin información sensible
 * (nunca incluye el password). Se usa en todas las respuestas de la API
 * en vez de exponer directamente la entidad {@link User}.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserResponse {

    @Schema(description = "Identificador único del usuario", example = "1")
    private Long id_user;

    @Schema(description = "Nombre de usuario", example = "jperez")
    private String username;

    @Schema(description = "Correo electrónico del usuario", example = "jperez@example.com")
    private String email;

    /**
     * Crea un {@link UserResponse} a partir de una entidad {@link User}, omitiendo
     * cualquier información sensible como la contraseña.
     *
     * @param user entidad de usuario de origen
     * @return un nuevo DTO con los datos públicos del usuario
     */
    public static UserResponse from(User user) {
        return new UserResponse(user.getId_user(), user.getUsername(), user.getEmail());
    }
}
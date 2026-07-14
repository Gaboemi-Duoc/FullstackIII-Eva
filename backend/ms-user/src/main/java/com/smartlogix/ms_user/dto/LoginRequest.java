package com.smartlogix.ms_user.dto;

import jakarta.validation.constraints.NotBlank;

/**
 * DTO utilizado para recibir las credenciales de acceso al iniciar sesión.
 */
public class LoginRequest {

    /** Nombre de usuario de la cuenta. */
    @NotBlank(message = "El username es obligatorio")
    private String username;

    /** Contraseña de la cuenta. */
    @NotBlank(message = "La contraseña es obligatoria")
    private String password;

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
}
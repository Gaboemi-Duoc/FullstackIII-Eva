package com.smartlogix.ms_user.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class UpdateUsernameRequest {

    @NotBlank(message = "El username es obligatorio")
    @Size(min = 3, message = "El username debe tener al menos 3 caracteres")
    private String username;

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
}
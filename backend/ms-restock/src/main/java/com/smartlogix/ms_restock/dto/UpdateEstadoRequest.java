package com.smartlogix.ms_restock.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * DTO de entrada para actualizar el estado de una solicitud existente.
 * Body esperado: { "estado": "APROBADA" }
 */
@Data
public class UpdateEstadoRequest {

    @NotBlank(message = "estado es obligatorio")
    private String estado;
}
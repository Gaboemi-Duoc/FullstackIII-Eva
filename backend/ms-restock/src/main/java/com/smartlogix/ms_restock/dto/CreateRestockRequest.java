package com.smartlogix.ms_restock.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * DTO de entrada para crear una nueva solicitud de restock.
 * No incluye 'estado' ni fechas: esos los controla el backend.
 */
@Data
public class CreateRestockRequest {

    @NotNull(message = "id_item es obligatorio")
    private Long id_item;

    @NotBlank(message = "nombre_item es obligatorio")
    private String nombre_item;

    @NotBlank(message = "bodega es obligatoria")
    private String bodega;

    @NotNull(message = "cantidad_solicitada es obligatoria")
    @Min(value = 1, message = "cantidad_solicitada debe ser mayor a 0")
    private Integer cantidad_solicitada;
}

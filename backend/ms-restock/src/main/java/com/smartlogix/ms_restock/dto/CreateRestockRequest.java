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

    @NotNull(message = "idItem es obligatorio")
    private Long idItem;

    @NotBlank(message = "nombreItem es obligatorio")
    private String nombreItem;

    @NotBlank(message = "bodega es obligatoria")
    private String bodega;

    @NotNull(message = "cantidadSolicitada es obligatoria")
    @Min(value = 1, message = "cantidadSolicitada debe ser mayor a 0")
    private Integer cantidadSolicitada;
}

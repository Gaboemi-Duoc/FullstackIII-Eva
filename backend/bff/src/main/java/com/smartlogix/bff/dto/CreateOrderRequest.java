package com.smartlogix.bff.dto;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class CreateOrderRequest {

    @NotBlank(message = "El nombre del cliente es obligatorio")
    private String customerName;

    @Email(message = "Correo inválido")
    @NotBlank(message = "El correo es obligatorio")
    private String customerEmail;

    @NotBlank(message = "La dirección es obligatoria")
    private String deliveryAddress;

    @NotNull(message = "El total es obligatorio")
    @Positive(message = "El total debe ser mayor a 0")
    private Double total;

    @NotNull(message = "El id del item es obligatorio")
    private Long idItem;

    @NotNull(message = "La cantidad solicitada es obligatoria")
    @Positive(message = "La cantidad debe ser mayor a 0")
    private Integer cantidadSolicitada;
}
package com.smartlogix.ms_inventory.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public class UpdatePrecioRequest {

    @NotNull(message = "El precio es obligatorio")
    @Positive(message = "El precio debe ser mayor a 0")
    private Double precio;

    public Double getPrecio() { return precio; }
    public void setPrecio(Double precio) { this.precio = precio; }
}
package com.smartlogix.ms_inventory.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

/**
 * DTO utilizado para actualizar el precio unitario de un item de inventario.
 */
public class UpdatePrecioRequest {

    /** Nuevo precio a asignar al item, debe ser mayor a 0. */
    @NotNull(message = "El precio es obligatorio")
    @Positive(message = "El precio debe ser mayor a 0")
    private Double precio;

    public Double getPrecio() { return precio; }
    public void setPrecio(Double precio) { this.precio = precio; }
}
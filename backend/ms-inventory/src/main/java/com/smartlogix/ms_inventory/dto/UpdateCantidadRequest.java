package com.smartlogix.ms_inventory.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

/**
 * DTO utilizado para actualizar la cantidad en stock de un item de inventario.
 */
public class UpdateCantidadRequest {

    /** Nueva cantidad a asignar al item, no puede ser negativa. */
    @NotNull(message = "La cantidad es obligatoria")
    @Min(value = 0, message = "La cantidad no puede ser negativa")
    private Integer cantidad;

    public Integer getCantidad() { return cantidad; }
    public void setCantidad(Integer cantidad) { this.cantidad = cantidad; }
}
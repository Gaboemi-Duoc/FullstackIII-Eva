package com.smartlogix.ms_inventory.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

/**
 * DTO utilizado para recibir los datos necesarios al crear un nuevo item de inventario.
 */
public class CreateItemRequest {

    /** Nombre del item a crear. */
    @NotBlank(message = "El nombre es obligatorio")
    private String nombre;

    /** Descripción del item a crear. */
    @NotBlank(message = "La descripción es obligatoria")
    private String descripcion;

    /** Cantidad inicial en stock del item, no puede ser negativa. */
    @NotNull(message = "La cantidad es obligatoria")
    @Min(value = 0, message = "La cantidad no puede ser negativa")
    private Integer cantidad;

    /** Precio unitario del item, debe ser mayor a 0. */
    @NotNull(message = "El precio es obligatorio")
    @Positive(message = "El precio debe ser mayor a 0")
    private Double precio;

    /** Bodega en la que se almacenará el item. */
    @NotBlank(message = "La bodega es obligatoria")
    private String bodega;

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

    public Integer getCantidad() { return cantidad; }
    public void setCantidad(Integer cantidad) { this.cantidad = cantidad; }

    public Double getPrecio() { return precio; }
    public void setPrecio(Double precio) { this.precio = precio; }

    public String getBodega() { return bodega; }
    public void setBodega(String bodega) { this.bodega = bodega; }
}
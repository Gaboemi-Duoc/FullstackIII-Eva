package com.smartlogix.ms_inventory.model;

import jakarta.persistence.*;
import lombok.*;

/**
 * Entidad JPA que representa un item de inventario dentro de la plataforma SmartLogix.
 * <p>
 * Se mapea a la tabla {@code inventory_item} y almacena información de stock,
 * precio y bodega asociada al item.
 */
@Entity
@Table(name = "inventory_item")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Item {

    /** Identificador único del item, autogenerado por la base de datos. */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id_item;

    /** Nombre del item. */
    @Column(nullable = false)
    private String nombre;

    /** Descripción del item. */
    @Column(nullable = false)
    private String descripcion;

    /** Cantidad actual en stock del item. */
    @Column(nullable = false)
    private Integer cantidad;

    /** Precio unitario del item. */
    @Column(nullable = false)
    private Double precio;

    /**
     * Bodega en la que se almacena el item. (RF13)
     * Ej: "Bodega Central", "Bodega Norte", "Bodega Sur".
     */
    @Column(nullable = false)
    private String bodega;
}
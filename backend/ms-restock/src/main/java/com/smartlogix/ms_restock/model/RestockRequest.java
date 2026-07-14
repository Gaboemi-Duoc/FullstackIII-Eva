package com.smartlogix.ms_restock.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

/**
 * Entidad JPA que representa una solicitud de reabastecimiento de stock dentro
 * de la plataforma SmartLogix.
 * <p>
 * Se mapea a la tabla {@code restock_request} y almacena el item solicitado,
 * la bodega de destino, la cantidad, el estado y las fechas de seguimiento.
 */
@Entity
@Table(name = "restock_request")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class RestockRequest {

    /** Identificador único de la solicitud, autogenerado por la base de datos. */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_restock")
    private Long idRestock;

    /** Identificador del item de inventario a reabastecer. */
    @Column(name = "id_item", nullable = false)
    private Long idItem;

    /** Nombre del item de inventario a reabastecer. */
    @Column(name = "nombre_item", nullable = false)
    private String nombreItem;

    /** Bodega a la que se destina el reabastecimiento. */
    @Column(nullable = false)
    private String bodega;

    /** Cantidad solicitada de reabastecimiento. */
    @Column(name = "cantidad_solicitada", nullable = false)
    private Integer cantidadSolicitada;

    /** Estado actual de la solicitud, inicializado en {@link EstadoRestock#PENDIENTE}. */
    @Column(nullable = false)
    private String estado = EstadoRestock.PENDIENTE.name();

    /** Fecha y hora en que se creó la solicitud. */
    @Column(name = "fecha_solicitud", nullable = false)
    private LocalDateTime fechaSolicitud = LocalDateTime.now();

    /** Fecha y hora de la última actualización de estado de la solicitud. */
    @Column(name = "fecha_actualizacion")
    private LocalDateTime fechaActualizacion;

    /**
     * Representa los posibles estados de una solicitud de restock.
     */
    public enum EstadoRestock {
        /** La solicitud fue creada y está pendiente de revisión. */
        PENDIENTE,
        /** La solicitud fue aprobada. */
        APROBADA,
        /** La solicitud fue rechazada. */
        RECHAZADA,
        /** La solicitud fue completada. */
        COMPLETADA
    }
}
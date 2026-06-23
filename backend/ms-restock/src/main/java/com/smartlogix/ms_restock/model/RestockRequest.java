package com.smartlogix.ms_restock.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "restock_request")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class RestockRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_restock")
    private Long idRestock;

    @Column(name = "id_item", nullable = false)
    private Long idItem;

    @Column(name = "nombre_item", nullable = false)
    private String nombreItem;

    @Column(nullable = false)
    private String bodega;

    @Column(name = "cantidad_solicitada", nullable = false)
    private Integer cantidadSolicitada;

    @Column(nullable = false)
    private String estado = EstadoRestock.PENDIENTE.name();

    @Column(name = "fecha_solicitud", nullable = false)
    private LocalDateTime fechaSolicitud = LocalDateTime.now();

    @Column(name = "fecha_actualizacion")
    private LocalDateTime fechaActualizacion;

    public enum EstadoRestock {
        PENDIENTE, APROBADA, RECHAZADA, COMPLETADA
    }
}
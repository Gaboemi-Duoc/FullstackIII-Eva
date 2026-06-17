package com.smartlogix.ms_restock.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

/**
 * Entidad que representa una solicitud de reposición de stock.
 *
 * Patrones aplicados:
 *  - Entity (JPA): mapea la tabla restock_request.
 *  - Lombok @Data: genera getters, setters, equals, hashCode y toString.
 */
@Entity
@Table(name = "restock_request")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class RestockRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id_restock;

    /** FK lógica al ítem en ms-inventory (no hay FK física entre servicios). */
    @Column(nullable = false)
    private Long id_item;

    /** Nombre desnormalizado del ítem para evitar llamadas inter-servicio en lectura. */
    @Column(nullable = false)
    private String nombre_item;

    /** Bodega que genera la solicitud de restock. */
    @Column(nullable = false)
    private String bodega;

    /** Unidades que se solicitan reponer. Debe ser positivo. */
    @Column(nullable = false)
    private Integer cantidad_solicitada;

    /**
     * Estado del ciclo de vida de la solicitud.
     * Valores válidos: PENDIENTE, APROBADA, RECHAZADA, COMPLETADA
     */
    @Column(nullable = false)
    private String estado = EstadoRestock.PENDIENTE.name();

    @Column(nullable = false)
    private LocalDateTime fecha_solicitud = LocalDateTime.now();

    /** Se actualiza cada vez que cambia el estado. */
    private LocalDateTime fecha_actualizacion;

    public enum EstadoRestock {
        PENDIENTE, APROBADA, RECHAZADA, COMPLETADA
    }
}

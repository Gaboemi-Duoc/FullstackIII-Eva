package com.smartlogix.bff.model;

import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RestockRequest {

    private Long id_restock;
    private Long id_item;
    private String nombre_item;
    private String bodega;
    private Integer cantidad_solicitada;
    private String estado;
    private LocalDateTime fecha_solicitud;
    private LocalDateTime fecha_actualizacion;
}

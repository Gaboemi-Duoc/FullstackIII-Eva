package com.smartlogix.bff.model;

import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RestockRequest {

    private Long idRestock;
    private Long idItem;
    private String nombreItem;
    private String bodega;
    private Integer cantidadSolicitada;
    private String estado;
    private LocalDateTime fechaSolicitud;
    private LocalDateTime fechaActualizacion;
}
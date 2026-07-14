package com.smartlogix.bff.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class Item {
    @JsonProperty("id")
    private Long id_item;
    
    private String nombre;
    private String descripcion;
    private Integer cantidad;
    private Double precio;
    private String bodega;
}
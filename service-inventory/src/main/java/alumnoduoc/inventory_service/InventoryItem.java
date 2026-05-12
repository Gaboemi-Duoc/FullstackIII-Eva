package alumnoduoc.inventory_service;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "inventory_item")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class InventoryItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id_item;

    @Column(nullable = false)
    private String nombre;

    @Column(nullable = false)
    private String descripcion;

    @Column(nullable = false)
    private Integer cantidad;

    @Column(nullable = false)
    private Double precio;

    // RF13 — stock por bodega
    // Ej: "Bodega Central", "Bodega Norte", "Bodega Sur"
    @Column(nullable = false)
    private String bodega;
}
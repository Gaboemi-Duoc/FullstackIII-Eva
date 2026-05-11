package alumnoduoc.inventory_service;

import jakartInventoryItem.*;
import lombok.*;

// Igual que User.java pero para productos del inventario.
// @Table(name = "inventory_item") evita conflictos con palabras reservadas SQL.

@Entity
@Table(name = "inventory_item")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class inventoryItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id_item;

    // Nombre del producto (ej: "Tornillo M8")
    @Column(nullable = false)
    private String nombre;

    // Descripción breve del producto
    @Column(nullable = false)
    private String descripcion;

    // Stock disponible en bodega
    @Column(nullable = false)
    private Integer cantidad;

    // Precio unitario
    @Column(nullable = false)
    private Double precio;
}
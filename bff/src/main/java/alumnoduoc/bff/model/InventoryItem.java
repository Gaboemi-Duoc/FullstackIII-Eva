package alumnoduoc.bff.model;

import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class InventoryItem {
    private Long id_item;
    private String nombre;
    private String descripcion;
    private Integer cantidad;
    private Double precio;
}
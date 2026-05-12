package alumnoduoc.inventory_service;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface InventoryRepository extends JpaRepository<InventoryItem, Long> {

    // Busca por nombre exacto
    Optional<InventoryItem> findByNombre(String nombre);

    // Ítems con stock bajo el umbral
    List<InventoryItem> findByCantidadLessThan(Integer cantidad);

    // RF13 — todos los ítems de una bodega específica
    // Spring genera: SELECT * FROM inventory_item WHERE bodega = ?
    List<InventoryItem> findByBodega(String bodega);

    // RF13 — stock total agrupado por bodega
    // Suma todas las cantidades de cada bodega
    // Retorna Object[]: [0] = nombre bodega, [1] = suma cantidad
    @Query("SELECT i.bodega, SUM(i.cantidad) FROM InventoryItem i GROUP BY i.bodega")
    List<Object[]> stockTotalPorBodega();
}

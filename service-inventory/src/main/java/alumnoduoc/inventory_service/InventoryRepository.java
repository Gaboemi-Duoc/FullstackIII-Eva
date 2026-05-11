package alumnoduoc.inventory_service;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

// JpaRepository<InventoryItem, Long> provee gratis:
// findAll(), findById(), save(), deleteById(), count(), etc.

@Repository
public interface InventoryRepository extends JpaRepository<InventoryItem, Long> {

    // Spring genera: SELECT * FROM inventory_item WHERE nombre = ?
    // Igual al findByEmail de UserRepository, derivado del nombre del método
    Optional<InventoryItem> findByNombre(String nombre);

    // SELECT * FROM inventory_item WHERE cantidad < ?
    // Útil para detectar productos que necesitan restock
    List<InventoryItem> findByCantidadLessThan(Integer cantidad);
}

package alumnoduoc.inventory_service.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import alumnoduoc.inventory_service.model.Item;

import java.util.List;
import java.util.Optional;

@Repository
public interface InventoryRepository extends JpaRepository<Item, Long> {

    Optional<Item> findByNombre(String nombre);

    List<Item> findByCantidadLessThan(Integer cantidad);

    List<Item> findByBodega(String bodega);

    // CORREGIDO: antes decía InventoryItem, pero tu entidad se llama Item
    @Query("SELECT i.bodega, SUM(i.cantidad) FROM Item i GROUP BY i.bodega")
    List<Object[]> stockTotalPorBodega();
}
package com.smartlogix.ms_inventory.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.smartlogix.ms_inventory.model.Item;

import java.util.List;
import java.util.Optional;

/**
 * Repositorio JPA para el acceso y persistencia de la entidad {@link Item}.
 * <p>
 * Extiende {@link JpaRepository} para heredar las operaciones CRUD estándar y
 * define consultas derivadas y personalizadas adicionales.
 */
@Repository
public interface InventoryRepository extends JpaRepository<Item, Long> {

    /**
     * Busca un item por su nombre exacto.
     *
     * @param nombre nombre del item a buscar
     * @return un {@link Optional} con el item encontrado, o vacío si no existe
     */
    Optional<Item> findByNombre(String nombre);

    /**
     * Busca los items cuya cantidad en stock es menor al valor indicado.
     *
     * @param cantidad valor límite (exclusivo) de cantidad
     * @return lista de items con cantidad por debajo del valor indicado
     */
    List<Item> findByCantidadLessThan(Integer cantidad);

    /**
     * Busca los items que pertenecen a una bodega específica.
     *
     * @param bodega nombre de la bodega
     * @return lista de items asociados a la bodega indicada
     */
    List<Item> findByBodega(String bodega);

    /**
     * Calcula el stock total agrupado por bodega.
     *
     * @return lista de arreglos donde cada elemento contiene el nombre de la bodega
     *         en la posición 0 y la suma de cantidades en la posición 1
     */
    // CORREGIDO: antes decía InventoryItem, pero tu entidad se llama Item
    @Query("SELECT i.bodega, SUM(i.cantidad) FROM Item i GROUP BY i.bodega")
    List<Object[]> stockTotalPorBodega();
}
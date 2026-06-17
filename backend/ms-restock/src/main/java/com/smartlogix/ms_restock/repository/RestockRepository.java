package com.smartlogix.ms_restock.repository;

import com.smartlogix.ms_restock.model.RestockRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository Pattern: extiende JpaRepository para operaciones CRUD estándar.
 * Agrega consultas derivadas y JPQL para los casos de uso del servicio.
 */
@Repository
public interface RestockRepository extends JpaRepository<RestockRequest, Long> {

    /** Todas las solicitudes de un estado dado (ej: "PENDIENTE"). */
    List<RestockRequest> findByEstado(String estado);

    /** Historial de solicitudes para un ítem específico. */
    List<RestockRequest> findByIdItem(Long id_item);

    /** Solicitudes por bodega (útil para vistas de administración por sede). */
    List<RestockRequest> findByBodega(String bodega);

    /** Solicitudes pendientes de una bodega concreta. */
    List<RestockRequest> findByBodegaAndEstado(String bodega, String estado);

    /**
     * Resumen de solicitudes agrupadas por estado.
     * Retorna: [ ["PENDIENTE", 5], ["APROBADA", 3], ... ]
     */
    @Query("SELECT r.estado, COUNT(r) FROM RestockRequest r GROUP BY r.estado")
    List<Object[]> contarPorEstado();
}
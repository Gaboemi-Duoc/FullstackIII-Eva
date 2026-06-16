package alumnoduoc.restock_service.service;

import alumnoduoc.restock_service.model.RestockRequest;
import alumnoduoc.restock_service.repository.RestockRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Service Layer: encapsula toda la lógica de negocio del microservicio.
 * El controlador sólo delega en esta capa; nunca accede al repositorio directamente.
 *
 * Patrones aplicados:
 *  - Service Layer
 *  - Repository Pattern (a través de RestockRepository)
 */
@Service
public class RestockService {

    private final RestockRepository restockRepository;

    // Inyección por constructor (buena práctica frente a @Autowired en campo)
    public RestockService(RestockRepository restockRepository) {
        this.restockRepository = restockRepository;
    }

    // ─── Consultas ────────────────────────────────────────────────────────────

    /** Lista todas las solicitudes de restock sin filtro. */
    public List<RestockRequest> listarSolicitudes() {
        return restockRepository.findAll();
    }

    /** Busca una solicitud por ID; lanza excepción si no existe. */
    public RestockRequest obtenerPorId(Long id) {
        return restockRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Solicitud no encontrada con id: " + id));
    }

    /** Retorna todas las solicitudes con un estado específico. */
    public List<RestockRequest> listarPorEstado(String estado) {
        validarEstado(estado);
        return restockRepository.findByEstado(estado.toUpperCase());
    }

    /** Retorna el historial de solicitudes para un ítem. */
    public List<RestockRequest> listarPorItem(Long id_item) {
        return restockRepository.findByIdItem(id_item);
    }

    /** Retorna las solicitudes de una bodega (opcionalmente filtradas por estado). */
    public List<RestockRequest> listarPorBodega(String bodega) {
        return restockRepository.findByBodega(bodega);
    }

    /** Retorna solicitudes pendientes de una bodega específica. */
    public List<RestockRequest> pendientesPorBodega(String bodega) {
        return restockRepository.findByBodegaAndEstado(bodega, RestockRequest.EstadoRestock.PENDIENTE.name());
    }

    /**
     * Resumen de solicitudes agrupadas por estado.
     * Ejemplo de retorno: { "PENDIENTE": 4, "APROBADA": 2 }
     */
    public Map<String, Long> resumenPorEstado() {
        List<Object[]> filas = restockRepository.contarPorEstado();
        Map<String, Long> resumen = new HashMap<>();
        for (Object[] fila : filas) {
            String estado = (String) fila[0];
            Long total   = (Long)   fila[1];
            resumen.put(estado, total);
        }
        return resumen;
    }

    // ─── Creación ─────────────────────────────────────────────────────────────

    /**
     * Crea una nueva solicitud de restock.
     * Fuerza el estado inicial a PENDIENTE y registra la fecha de creación.
     */
    public RestockRequest crearSolicitud(RestockRequest solicitud) {
        solicitud.setEstado(RestockRequest.EstadoRestock.PENDIENTE.name());
        solicitud.setFecha_solicitud(LocalDateTime.now());
        solicitud.setFecha_actualizacion(null);
        return restockRepository.save(solicitud);
    }

    // ─── Actualización de estado ──────────────────────────────────────────────

    /**
     * Actualiza el estado de una solicitud existente.
     * Registra el timestamp de la actualización y valida que el nuevo estado sea válido.
     */
    public RestockRequest actualizarEstado(Long id, String nuevoEstado) {
        validarEstado(nuevoEstado);
        RestockRequest solicitud = obtenerPorId(id);
        solicitud.setEstado(nuevoEstado.toUpperCase());
        solicitud.setFecha_actualizacion(LocalDateTime.now());
        return restockRepository.save(solicitud);
    }

    // ─── Eliminación ──────────────────────────────────────────────────────────

    /** Elimina una solicitud por ID. */
    public void eliminarSolicitud(Long id) {
        // Verifica existencia antes de eliminar
        obtenerPorId(id);
        restockRepository.deleteById(id);
    }

    // ─── Validación interna ───────────────────────────────────────────────────

    /**
     * Valida que el estado proporcionado pertenezca al enum EstadoRestock.
     * Lanza RuntimeException con mensaje claro si no es válido.
     */
    private void validarEstado(String estado) {
        try {
            RestockRequest.EstadoRestock.valueOf(estado.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new RuntimeException(
                "Estado inválido: '" + estado + "'. " +
                "Valores permitidos: PENDIENTE, APROBADA, RECHAZADA, COMPLETADA"
            );
        }
    }
}

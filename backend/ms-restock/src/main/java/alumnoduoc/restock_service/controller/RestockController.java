package alumnoduoc.restock_service.controller;

import alumnoduoc.restock_service.model.RestockRequest;
import alumnoduoc.restock_service.service.RestockService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * Controlador REST del microservicio de restock.
 * Sigue el patrón MVC: sólo delega en RestockService, sin lógica propia.
 *
 * Base path: /api/restock
 * Puerto:    9092
 */
@RestController
@RequestMapping("/api/restock")
@CrossOrigin(origins = "*")
public class RestockController {

    private final RestockService restockService;

    public RestockController(RestockService restockService) {
        this.restockService = restockService;
    }

    // ─── GET ──────────────────────────────────────────────────────────────────

    /** Lista todas las solicitudes de restock. */
    @GetMapping
    public ResponseEntity<List<RestockRequest>> listarSolicitudes() {
        return ResponseEntity.ok(restockService.listarSolicitudes());
    }

    /** Obtiene una solicitud específica por su ID. */
    @GetMapping("/{id}")
    public ResponseEntity<RestockRequest> obtenerSolicitud(@PathVariable Long id) {
        return ResponseEntity.ok(restockService.obtenerPorId(id));
    }

    /**
     * Filtra solicitudes por estado.
     * Ej: GET /api/restock/estado?valor=PENDIENTE
     */
    @GetMapping("/estado")
    public ResponseEntity<List<RestockRequest>> listarPorEstado(@RequestParam String valor) {
        return ResponseEntity.ok(restockService.listarPorEstado(valor));
    }

    /**
     * Retorna el historial de solicitudes de un ítem específico.
     * Ej: GET /api/restock/item/5
     */
    @GetMapping("/item/{id_item}")
    public ResponseEntity<List<RestockRequest>> listarPorItem(@PathVariable Long id_item) {
        return ResponseEntity.ok(restockService.listarPorItem(id_item));
    }

    /**
     * Retorna todas las solicitudes de una bodega.
     * Ej: GET /api/restock/bodega?nombre=Bodega+Central
     */
    @GetMapping("/bodega")
    public ResponseEntity<List<RestockRequest>> listarPorBodega(@RequestParam String nombre) {
        return ResponseEntity.ok(restockService.listarPorBodega(nombre));
    }

    /**
     * Retorna solicitudes PENDIENTES de una bodega.
     * Ej: GET /api/restock/bodega/pendientes?nombre=Bodega+Norte
     */
    @GetMapping("/bodega/pendientes")
    public ResponseEntity<List<RestockRequest>> pendientesPorBodega(@RequestParam String nombre) {
        return ResponseEntity.ok(restockService.pendientesPorBodega(nombre));
    }

    /**
     * Resumen de solicitudes agrupadas por estado.
     * Ej: GET /api/restock/resumen → { "PENDIENTE": 4, "APROBADA": 2 }
     */
    @GetMapping("/resumen")
    public ResponseEntity<Map<String, Long>> resumenPorEstado() {
        return ResponseEntity.ok(restockService.resumenPorEstado());
    }

    // ─── POST ─────────────────────────────────────────────────────────────────

    /**
     * Crea una nueva solicitud de restock.
     * El estado se fija automáticamente a PENDIENTE en la capa de servicio.
     */
    @PostMapping
    public ResponseEntity<RestockRequest> crearSolicitud(@RequestBody RestockRequest solicitud) {
        return ResponseEntity.ok(restockService.crearSolicitud(solicitud));
    }

    // ─── PUT ──────────────────────────────────────────────────────────────────

    /**
     * Actualiza el estado de una solicitud existente.
     * Body: { "estado": "APROBADA" }
     */
    @PutMapping("/{id}/estado")
    public ResponseEntity<RestockRequest> actualizarEstado(
            @PathVariable Long id,
            @RequestBody Map<String, String> datos) {

        String nuevoEstado = datos.get("estado");
        return ResponseEntity.ok(restockService.actualizarEstado(id, nuevoEstado));
    }

    // ─── DELETE ───────────────────────────────────────────────────────────────

    /** Elimina una solicitud por ID. */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarSolicitud(@PathVariable Long id) {
        restockService.eliminarSolicitud(id);
        return ResponseEntity.noContent().build();
    }
}

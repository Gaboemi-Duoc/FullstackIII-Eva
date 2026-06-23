package com.smartlogix.ms_restock.controller;

import com.smartlogix.ms_restock.dto.CreateRestockRequest;
import com.smartlogix.ms_restock.dto.UpdateEstadoRequest;
import com.smartlogix.ms_restock.model.RestockRequest;
import com.smartlogix.ms_restock.service.RestockService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * Controlador REST del microservicio de restock.
 * Sigue el patrón MVC: sólo delega en RestockService, sin lógica propia.
 *
 * Base path: /api/restock
 * Puerto:    9093
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

    @GetMapping
    public ResponseEntity<List<RestockRequest>> listarSolicitudes() {
        return ResponseEntity.ok(restockService.listarSolicitudes());
    }

    @GetMapping("/{id}")
    public ResponseEntity<RestockRequest> obtenerSolicitud(@PathVariable Long id) {
        return ResponseEntity.ok(restockService.obtenerPorId(id));
    }

    @GetMapping("/estado")
    public ResponseEntity<List<RestockRequest>> listarPorEstado(@RequestParam String valor) {
        return ResponseEntity.ok(restockService.listarPorEstado(valor));
    }

    // Cambiamos la ruta y la variable a CamelCase (idItem)
    @GetMapping("/item/{idItem}")
    public ResponseEntity<List<RestockRequest>> listarPorItem(@PathVariable Long idItem) {
        return ResponseEntity.ok(restockService.listarPorItem(idItem));
    }

    @GetMapping("/bodega")
    public ResponseEntity<List<RestockRequest>> listarPorBodega(@RequestParam String nombre) {
        return ResponseEntity.ok(restockService.listarPorBodega(nombre));
    }

    @GetMapping("/bodega/pendientes")
    public ResponseEntity<List<RestockRequest>> pendientesPorBodega(@RequestParam String nombre) {
        return ResponseEntity.ok(restockService.pendientesPorBodega(nombre));
    }

    @GetMapping("/resumen")
    public ResponseEntity<Map<String, Long>> resumenPorEstado() {
        return ResponseEntity.ok(restockService.resumenPorEstado());
    }

    // ─── POST ─────────────────────────────────────────────────────────────────

    /**
     * Crea una nueva solicitud de restock a partir del DTO de entrada.
     * El estado inicial (PENDIENTE) y las fechas se fijan en RestockService.
     */
    @PostMapping
    public ResponseEntity<RestockRequest> crearSolicitud(@Valid @RequestBody CreateRestockRequest dto) {

        System.out.println("idItem = " + dto.getIdItem());
        System.out.println("nombreItem = " + dto.getNombreItem());
        System.out.println("bodega = " + dto.getBodega());
        System.out.println("cantidad = " + dto.getCantidadSolicitada());

        RestockRequest solicitud = new RestockRequest();
        
        // Usamos los nuevos Setters generados por Lombok y los Getters del DTO
        solicitud.setIdItem(dto.getIdItem());
        solicitud.setNombreItem(dto.getNombreItem());
        solicitud.setBodega(dto.getBodega());
        solicitud.setCantidadSolicitada(dto.getCantidadSolicitada());

        return ResponseEntity.ok(restockService.crearSolicitud(solicitud));
    }

    // ─── PUT ──────────────────────────────────────────────────────────────────

    @PutMapping("/{id}/estado")
    public ResponseEntity<RestockRequest> actualizarEstado(
            @PathVariable Long id,
            @Valid @RequestBody UpdateEstadoRequest dto) {

        return ResponseEntity.ok(restockService.actualizarEstado(id, dto.getEstado()));
    }

    // ─── DELETE ───────────────────────────────────────────────────────────────

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarSolicitud(@PathVariable Long id) {
        restockService.eliminarSolicitud(id);
        return ResponseEntity.noContent().build();
    }
}
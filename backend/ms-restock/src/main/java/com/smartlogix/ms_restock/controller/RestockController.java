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

    @GetMapping("/item/{id_item}")
    public ResponseEntity<List<RestockRequest>> listarPorItem(@PathVariable Long id_item) {
        return ResponseEntity.ok(restockService.listarPorItem(id_item));
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
    public ResponseEntity<RestockRequest>
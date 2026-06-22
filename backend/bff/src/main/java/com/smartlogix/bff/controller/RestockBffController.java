package com.smartlogix.bff.controller;

import com.smartlogix.bff.client.InventoryServiceClient;
import com.smartlogix.bff.client.RestockServiceClient;
import com.smartlogix.bff.model.Item;
import com.smartlogix.bff.model.RestockRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/bff/restock")
@CrossOrigin(origins = "http://localhost:5173")
@Tag(name = "Gestion de Restock", description = "Endpoints para gestionar solicitudes de reposición de stock")
public class RestockBffController {

    private final RestockServiceClient restockServiceClient;
    private final InventoryServiceClient inventoryServiceClient;

    public RestockBffController(
            RestockServiceClient restockServiceClient,
            InventoryServiceClient inventoryServiceClient) {
        this.restockServiceClient = restockServiceClient;
        this.inventoryServiceClient = inventoryServiceClient;
    }

    // ─── GET ──────────────────────────────────────────────────────────────────

    @GetMapping
    @Operation(
        summary = "Listar todas las solicitudes de restock",
        description = "Retorna la lista completa de solicitudes de reposición de stock.",
        security = @SecurityRequirement(name = "Bearer Authentication")
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Solicitudes obtenidas correctamente",
            content = @Content(mediaType = "application/json",
                schema = @Schema(implementation = RestockRequest.class))),
        @ApiResponse(responseCode = "401", description = "No autorizado",
            content = @Content(mediaType = "application/json"))
    })
    public ResponseEntity<List<RestockRequest>> listarSolicitudes() {
        return ResponseEntity.ok(restockServiceClient.listarSolicitudes());
    }

    @GetMapping("/{id}")
    @Operation(
        summary = "Obtener solicitud por ID",
        security = @SecurityRequirement(name = "Bearer Authentication")
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Solicitud encontrada",
            content = @Content(mediaType = "application/json",
                schema = @Schema(implementation = RestockRequest.class))),
        @ApiResponse(responseCode = "404", description = "Solicitud no encontrada",
            content = @Content(mediaType = "application/json"))
    })
    public ResponseEntity<RestockRequest> obtenerSolicitud(
            @Parameter(description = "ID de la solicitud", required = true, example = "1")
            @PathVariable Long id) {
        return ResponseEntity.ok(restockServiceClient.obtenerPorId(id));
    }

    @GetMapping("/estado")
    @Operation(
        summary = "Filtrar solicitudes por estado",
        description = "Estados válidos: PENDIENTE, APROBADA, RECHAZADA, COMPLETADA",
        security = @SecurityRequirement(name = "Bearer Authentication")
    )
    public ResponseEntity<List<RestockRequest>> listarPorEstado(
            @Parameter(description = "Estado a filtrar", example = "PENDIENTE")
            @RequestParam String valor) {
        return ResponseEntity.ok(restockServiceClient.listarPorEstado(valor));
    }

    @GetMapping("/item/{id_item}")
    @Operation(
        summary = "Historial de solicitudes por ítem",
        security = @SecurityRequirement(name = "Bearer Authentication")
    )
    public ResponseEntity<List<RestockRequest>> listarPorItem(
            @Parameter(description = "ID del ítem en ms-inventory", example = "5")
            @PathVariable Long id_item) {
        return ResponseEntity.ok(restockServiceClient.listarPorItem(id_item));
    }

    @GetMapping("/bodega")
    @Operation(
        summary = "Solicitudes por bodega",
        security = @SecurityRequirement(name = "Bearer Authentication")
    )
    public ResponseEntity<List<RestockRequest>> listarPorBodega(
            @Parameter(description = "Nombre de la bodega", example = "Bodega Central")
            @RequestParam String nombre) {
        return ResponseEntity.ok(restockServiceClient.listarPorBodega(nombre));
    }

    @GetMapping("/bodega/pendientes")
    @Operation(
        summary = "Solicitudes pendientes de una bodega",
        security = @SecurityRequirement(name = "Bearer Authentication")
    )
    public ResponseEntity<List<RestockRequest>> pendientesPorBodega(
            @Parameter(description = "Nombre de la bodega", example = "Bodega Norte")
            @RequestParam String nombre) {
        return ResponseEntity.ok(restockServiceClient.pendientesPorBodega(nombre));
    }

    @GetMapping("/resumen")
    @Operation(
        summary = "Resumen de solicitudes agrupadas por estado",
        security = @SecurityRequirement(name = "Bearer Authentication")
    )
    public ResponseEntity<Map<String, Long>> resumenPorEstado() {
        return ResponseEntity.ok(restockServiceClient.resumenPorEstado());
    }

    // ─── POST ─────────────────────────────────────────────────────────────────

    @PostMapping
    @Operation(
        summary = "Crear nueva solicitud de restock",
        description = "El estado inicial se fija automáticamente a PENDIENTE.",
        security = @SecurityRequirement(name = "Bearer Authentication")
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Solicitud creada correctamente",
            content = @Content(mediaType = "application/json",
                schema = @Schema(implementation = RestockRequest.class))),
        @ApiResponse(responseCode = "400", description = "Datos inválidos",
            content = @Content(mediaType = "application/json"))
    })

    public ResponseEntity<RestockRequest> crearSolicitud(
            @RequestBody Map<String, Object> datos) {

        return ResponseEntity.ok(restockServiceClient.crearSolicitud(datos));
    }

    // ─── PUT ──────────────────────────────────────────────────────────────────

    @PutMapping("/{id}/estado")
    public ResponseEntity<RestockRequest> actualizarEstado(
            @PathVariable Long id,
            @RequestBody Map<String, String> datos) {

        RestockRequest solicitudActual = restockServiceClient.obtenerPorId(id);

        RestockRequest solicitudActualizada =
                restockServiceClient.actualizarEstado(id, datos);

        String nuevoEstado = datos.get("estado");

        if ("COMPLETADA".equalsIgnoreCase(nuevoEstado)) {
            Item item = inventoryServiceClient.getItemById(solicitudActual.getIdItem());

            Map<String, Integer> cantidadActualizada = Map.of(
                    "cantidad",
                    item.getCantidad() + solicitudActual.getCantidadSolicitada()
            );

            inventoryServiceClient.actualizarCantidad(
                    solicitudActual.getIdItem(),
                    cantidadActualizada
            );
        }

        return ResponseEntity.ok(solicitudActualizada);
    }

    // ─── DELETE ───────────────────────────────────────────────────────────────

    @DeleteMapping("/{id}")
    @Operation(
        summary = "Eliminar solicitud de restock",
        security = @SecurityRequirement(name = "Bearer Authentication")
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Solicitud eliminada correctamente"),
        @ApiResponse(responseCode = "404", description = "Solicitud no encontrada",
            content = @Content(mediaType = "application/json"))
    })
    public ResponseEntity<Void> eliminarSolicitud(
            @Parameter(description = "ID de la solicitud a eliminar", required = true, example = "1")
            @PathVariable Long id) {
        restockServiceClient.eliminarSolicitud(id);
        return ResponseEntity.noContent().build();
    }
}

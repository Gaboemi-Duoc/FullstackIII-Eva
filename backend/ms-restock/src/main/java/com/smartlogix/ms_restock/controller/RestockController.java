package com.smartlogix.ms_restock.controller;

import com.smartlogix.ms_restock.dto.CreateRestockRequest;
import com.smartlogix.ms_restock.dto.UpdateEstadoRequest;
import com.smartlogix.ms_restock.model.RestockRequest;
import com.smartlogix.ms_restock.service.RestockService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "Gestion de Reabastecimiento", description = "Operaciones sobre solicitudes de restock de stock")
public class RestockController {

    private final RestockService restockService;

    /**
     * Crea una nueva instancia del controlador de restock.
     *
     * @param restockService servicio que contiene la lógica de negocio de las solicitudes de restock
     */
    public RestockController(RestockService restockService) {
        this.restockService = restockService;
    }

    // ─── GET ──────────────────────────────────────────────────────────────────

    /**
     * Lista todas las solicitudes de restock registradas.
     *
     * @return listado completo de solicitudes envuelto en un {@link ResponseEntity} con estado 200
     */
    @Operation(summary = "Listar todas las solicitudes", description = "Retorna el listado completo de solicitudes de restock.")
    @ApiResponse(responseCode = "200", description = "Listado obtenido correctamente")
    @GetMapping
    public ResponseEntity<List<RestockRequest>> listarSolicitudes() {
        return ResponseEntity.ok(restockService.listarSolicitudes());
    }

    /**
     * Obtiene una solicitud de restock a partir de su identificador.
     *
     * @param id identificador de la solicitud
     * @return la solicitud encontrada envuelta en un {@link ResponseEntity} con estado 200
     */
    @Operation(summary = "Obtener solicitud por id", description = "Retorna una solicitud de restock según su identificador.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Solicitud encontrada"),
            @ApiResponse(responseCode = "404", description = "Solicitud no encontrada")
    })
    @GetMapping("/{id}")
    public ResponseEntity<RestockRequest> obtenerSolicitud(@PathVariable Long id) {
        return ResponseEntity.ok(restockService.obtenerPorId(id));
    }

    /**
     * Lista las solicitudes de restock filtradas por estado.
     *
     * @param valor valor del estado a filtrar
     * @return listado de solicitudes en el estado indicado, envuelto en un {@link ResponseEntity} con estado 200
     */
    @Operation(summary = "Listar solicitudes por estado", description = "Retorna las solicitudes de restock filtradas por estado.")
    @ApiResponse(responseCode = "200", description = "Listado obtenido correctamente")
    @GetMapping("/estado")
    public ResponseEntity<List<RestockRequest>> listarPorEstado(@RequestParam String valor) {
        return ResponseEntity.ok(restockService.listarPorEstado(valor));
    }

    /**
     * Lista las solicitudes de restock asociadas a un item de inventario.
     *
     * @param idItem identificador del item de inventario
     * @return listado de solicitudes asociadas al item, envuelto en un {@link ResponseEntity} con estado 200
     */
    // Cambiamos la ruta y la variable a CamelCase (idItem)
    @Operation(summary = "Listar solicitudes por item", description = "Retorna las solicitudes de restock asociadas a un item de inventario.")
    @ApiResponse(responseCode = "200", description = "Listado obtenido correctamente")
    @GetMapping("/item/{idItem}")
    public ResponseEntity<List<RestockRequest>> listarPorItem(@PathVariable Long idItem) {
        return ResponseEntity.ok(restockService.listarPorItem(idItem));
    }

    /**
     * Lista las solicitudes de restock asociadas a una bodega específica.
     *
     * @param nombre nombre de la bodega
     * @return listado de solicitudes de la bodega, envuelto en un {@link ResponseEntity} con estado 200
     */
    @Operation(summary = "Listar solicitudes por bodega", description = "Retorna las solicitudes de restock asociadas a una bodega.")
    @ApiResponse(responseCode = "200", description = "Listado obtenido correctamente")
    @GetMapping("/bodega")
    public ResponseEntity<List<RestockRequest>> listarPorBodega(@RequestParam String nombre) {
        return ResponseEntity.ok(restockService.listarPorBodega(nombre));
    }

    /**
     * Lista las solicitudes de restock en estado pendiente de una bodega específica.
     *
     * @param nombre nombre de la bodega
     * @return listado de solicitudes pendientes de la bodega, envuelto en un {@link ResponseEntity} con estado 200
     */
    @Operation(summary = "Listar solicitudes pendientes por bodega", description = "Retorna las solicitudes de restock en estado pendiente de una bodega.")
    @ApiResponse(responseCode = "200", description = "Listado obtenido correctamente")
    @GetMapping("/bodega/pendientes")
    public ResponseEntity<List<RestockRequest>> pendientesPorBodega(@RequestParam String nombre) {
        return ResponseEntity.ok(restockService.pendientesPorBodega(nombre));
    }

    /**
     * Obtiene un resumen con el conteo de solicitudes de restock agrupadas por estado.
     *
     * @return mapa de estado y cantidad de solicitudes, envuelto en un {@link ResponseEntity} con estado 200
     */
    @Operation(summary = "Resumen de solicitudes por estado", description = "Retorna el conteo de solicitudes de restock agrupadas por estado.")
    @ApiResponse(responseCode = "200", description = "Resumen obtenido correctamente")
    @GetMapping("/resumen")
    public ResponseEntity<Map<String, Long>> resumenPorEstado() {
        return ResponseEntity.ok(restockService.resumenPorEstado());
    }

    // ─── POST ─────────────────────────────────────────────────────────────────

    /**
     * Crea una nueva solicitud de restock a partir del DTO de entrada.
     * El estado inicial (PENDIENTE) y las fechas se fijan en RestockService.
     *
     * @param dto datos necesarios para crear la solicitud de restock
     * @return la solicitud creada envuelta en un {@link ResponseEntity} con estado 200
     */
    @Operation(summary = "Crear solicitud de restock", description = "Registra una nueva solicitud de reabastecimiento de stock.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Solicitud creada correctamente"),
            @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos")
    })
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

    /**
     * Actualiza el estado de una solicitud de restock existente.
     *
     * @param id identificador de la solicitud a actualizar
     * @param dto datos con el nuevo estado a asignar
     * @return la solicitud actualizada envuelta en un {@link ResponseEntity} con estado 200
     */
    @Operation(summary = "Actualizar estado de una solicitud", description = "Cambia el estado de una solicitud de restock existente.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Estado actualizado correctamente"),
            @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos"),
            @ApiResponse(responseCode = "404", description = "Solicitud no encontrada")
    })
    @PutMapping("/{id}/estado")
    public ResponseEntity<RestockRequest> actualizarEstado(
            @PathVariable Long id,
            @Valid @RequestBody UpdateEstadoRequest dto) {

        return ResponseEntity.ok(restockService.actualizarEstado(id, dto.getEstado()));
    }

    // ─── DELETE ───────────────────────────────────────────────────────────────

    /**
     * Elimina una solicitud de restock a partir de su identificador.
     *
     * @param id identificador de la solicitud a eliminar
     * @return respuesta sin contenido envuelta en un {@link ResponseEntity} con estado 204
     */
    @Operation(summary = "Eliminar solicitud", description = "Elimina una solicitud de restock según su identificador.")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Solicitud eliminada correctamente"),
            @ApiResponse(responseCode = "404", description = "Solicitud no encontrada")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarSolicitud(@PathVariable Long id) {
        restockService.eliminarSolicitud(id);
        return ResponseEntity.noContent().build();
    }
}
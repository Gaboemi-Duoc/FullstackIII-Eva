package com.smartlogix.bff.controller;

import com.smartlogix.bff.dto.DtoApiResponse;
import com.smartlogix.bff.model.RestockRequest;
import com.smartlogix.bff.service.RestockBffService;
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
@Tag(name = "Restock Management", description = "Endpoints for managing stock replenishment requests in the SmartLogix logistics platform")
public class RestockBffController {

    private final RestockBffService restockBffService;

    public RestockBffController(RestockBffService restockBffService) {
        this.restockBffService = restockBffService;
    }

    @GetMapping
    @Operation(
        summary = "List all restock requests",
        description = "Retrieves a complete list of all stock replenishment requests in the system. Results can be filtered by status or warehouse (see separate endpoints).",
        security = @SecurityRequirement(name = "Bearer Authentication")
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Restock requests retrieved successfully",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = RestockRequest.class))
        ),
        @ApiResponse(
            responseCode = "401",
            description = "Unauthorized - Valid authentication token required",
            content = @Content(mediaType = "application/json")
        ),
        @ApiResponse(
            responseCode = "500",
            description = "Internal server error",
            content = @Content(mediaType = "application/json")
        )
    })
    public ResponseEntity<DtoApiResponse<List<RestockRequest>>> listarSolicitudes() {
        DtoApiResponse<List<RestockRequest>> response = restockBffService.getAllRestockRequests();
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }

    @GetMapping("/{id}")
    @Operation(
        summary = "Get restock request by ID",
        description = "Retrieves detailed information about a specific stock replenishment request including item details, quantity, and current status.",
        security = @SecurityRequirement(name = "Bearer Authentication")
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Restock request found and retrieved successfully",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = RestockRequest.class))
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Restock request not found",
            content = @Content(mediaType = "application/json")
        ),
        @ApiResponse(
            responseCode = "401",
            description = "Unauthorized - Valid authentication token required",
            content = @Content(mediaType = "application/json")
        )
    })
    public ResponseEntity<DtoApiResponse<RestockRequest>> obtenerSolicitud(
            @Parameter(description = "Unique identifier of the restock request", required = true, example = "1")
            @PathVariable Long id) {
        DtoApiResponse<RestockRequest> response = restockBffService.getRestockRequestById(id);
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }

    @GetMapping("/estado")
    @Operation(
        summary = "Filter restock requests by status",
        description = "Retrieves all restock requests that match the specified status. Valid statuses: PENDIENTE (Pending), APROBADA (Approved), RECHAZADA (Rejected), COMPLETADA (Completed).",
        security = @SecurityRequirement(name = "Bearer Authentication")
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Restock requests retrieved successfully",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = RestockRequest.class))
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Invalid status value",
            content = @Content(mediaType = "application/json")
        ),
        @ApiResponse(
            responseCode = "401",
            description = "Unauthorized - Valid authentication token required",
            content = @Content(mediaType = "application/json")
        )
    })
    public ResponseEntity<DtoApiResponse<List<RestockRequest>>> listarPorEstado(
            @Parameter(
                description = "Status to filter by. Valid values: PENDIENTE, APROBADA, RECHAZADA, COMPLETADA",
                required = true,
                example = "PENDIENTE"
            )
            @RequestParam String valor) {
        DtoApiResponse<List<RestockRequest>> response = restockBffService.getRestockRequestsByStatus(valor);
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }

    @GetMapping("/item/{id_item}")
    @Operation(
        summary = "Get restock history by item",
        description = "Retrieves the complete restock request history for a specific inventory item. Useful for tracking replenishment patterns.",
        security = @SecurityRequirement(name = "Bearer Authentication")
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Restock requests retrieved successfully",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = RestockRequest.class))
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Item not found",
            content = @Content(mediaType = "application/json")
        ),
        @ApiResponse(
            responseCode = "401",
            description = "Unauthorized - Valid authentication token required",
            content = @Content(mediaType = "application/json")
        )
    })
    public ResponseEntity<DtoApiResponse<List<RestockRequest>>> listarPorItem(
            @Parameter(description = "ID of the inventory item", required = true, example = "5")
            @PathVariable Long id_item) {
        DtoApiResponse<List<RestockRequest>> response = restockBffService.getRestockRequestsByItem(id_item);
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }

    @GetMapping("/bodega")
    @Operation(
        summary = "Get restock requests by warehouse",
        description = "Retrieves all restock requests for a specific warehouse. Useful for warehouse-specific inventory management.",
        security = @SecurityRequirement(name = "Bearer Authentication")
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Restock requests retrieved successfully",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = RestockRequest.class))
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Warehouse not found",
            content = @Content(mediaType = "application/json")
        ),
        @ApiResponse(
            responseCode = "401",
            description = "Unauthorized - Valid authentication token required",
            content = @Content(mediaType = "application/json")
        )
    })
    public ResponseEntity<DtoApiResponse<List<RestockRequest>>> listarPorBodega(
            @Parameter(description = "Name of the warehouse", required = true, example = "Bodega Central")
            @RequestParam String nombre) {
        DtoApiResponse<List<RestockRequest>> response = restockBffService.getRestockRequestsByWarehouse(nombre);
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }

    @GetMapping("/bodega/pendientes")
    @Operation(
        summary = "Get pending restock requests by warehouse",
        description = "Retrieves all pending restock requests for a specific warehouse. Useful for identifying immediate restock needs.",
        security = @SecurityRequirement(name = "Bearer Authentication")
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Pending restock requests retrieved successfully",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = RestockRequest.class))
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Warehouse not found",
            content = @Content(mediaType = "application/json")
        ),
        @ApiResponse(
            responseCode = "401",
            description = "Unauthorized - Valid authentication token required",
            content = @Content(mediaType = "application/json")
        )
    })
    public ResponseEntity<DtoApiResponse<List<RestockRequest>>> pendientesPorBodega(
            @Parameter(description = "Name of the warehouse", required = true, example = "Bodega Norte")
            @RequestParam String nombre) {
        DtoApiResponse<List<RestockRequest>> response = restockBffService.getPendingRequestsByWarehouse(nombre);
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }

    @GetMapping("/resumen")
    @Operation(
        summary = "Get restock summary by status",
        description = "Provides a summary count of restock requests grouped by status. Useful for dashboard and reporting purposes.",
        security = @SecurityRequirement(name = "Bearer Authentication")
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Summary retrieved successfully",
            content = @Content(mediaType = "application/json")
        ),
        @ApiResponse(
            responseCode = "401",
            description = "Unauthorized - Valid authentication token required",
            content = @Content(mediaType = "application/json")
        ),
        @ApiResponse(
            responseCode = "500",
            description = "Internal server error",
            content = @Content(mediaType = "application/json")
        )
    })
    public ResponseEntity<DtoApiResponse<Map<String, Long>>> resumenPorEstado() {
        DtoApiResponse<Map<String, Long>> response = restockBffService.getRestockSummary();
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }

    @PostMapping
    @Operation(
        summary = "Create a new restock request",
        description = """
            Creates a new stock replenishment request. The request will be created with status 'PENDIENTE' (Pending).
            
            Required fields:
            - idItem: ID of the inventory item
            - cantidadSolicitada: Requested quantity
            - bodega: Warehouse name
            - nombreItem: Item name (for display purposes)
            
            The request must be approved by a warehouse manager before stock is added to inventory.
            """,
        security = @SecurityRequirement(name = "Bearer Authentication")
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Restock request created successfully",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = RestockRequest.class))
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Validation error - Invalid request data",
            content = @Content(mediaType = "application/json")
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Item not found",
            content = @Content(mediaType = "application/json")
        ),
        @ApiResponse(
            responseCode = "401",
            description = "Unauthorized - Valid authentication token required",
            content = @Content(mediaType = "application/json")
        ),
        @ApiResponse(
            responseCode = "409",
            description = "Duplicate request - A pending request already exists for this item",
            content = @Content(mediaType = "application/json")
        ),
        @ApiResponse(
            responseCode = "500",
            description = "Internal server error",
            content = @Content(mediaType = "application/json")
        )
    })
    public ResponseEntity<DtoApiResponse<RestockRequest>> crearSolicitud(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                description = """
                    Restock request data. Example:
                    {
                        "idItem": 5,
                        "nombreItem": "Laptop Dell XPS",
                        "bodega": "Bodega Central",
                        "cantidadSolicitada": 20
                    }
                    """,
                required = true,
                content = @Content(schema = @Schema(example = "{\"idItem\": 5, \"nombreItem\": \"Laptop Dell XPS\", \"bodega\": \"Bodega Central\", \"cantidadSolicitada\": 20}"))
            )
            @RequestBody Map<String, Object> datos) {
        DtoApiResponse<RestockRequest> response = restockBffService.createRestockRequest(datos);
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }

    @PutMapping("/{id}/estado")
    @Operation(
        summary = "Update restock request status",
        description = """
            Updates the status of a restock request. Valid status transitions:
            
            PENDIENTE → APROBADA or RECHAZADA
            APROBADA → COMPLETADA
            RECHAZADA → No further changes allowed
            COMPLETADA → No further changes allowed
            
            When a request is marked as 'COMPLETADA', the inventory stock will be automatically updated
            with the requested quantity.
            """,
        security = @SecurityRequirement(name = "Bearer Authentication")
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Restock request status updated successfully",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = RestockRequest.class))
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Restock request not found",
            content = @Content(mediaType = "application/json")
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Invalid status transition",
            content = @Content(mediaType = "application/json")
        ),
        @ApiResponse(
            responseCode = "401",
            description = "Unauthorized - Valid authentication token required",
            content = @Content(mediaType = "application/json")
        ),
        @ApiResponse(
            responseCode = "403",
            description = "Forbidden - Insufficient permissions for status change",
            content = @Content(mediaType = "application/json")
        ),
        @ApiResponse(
            responseCode = "500",
            description = "Internal server error",
            content = @Content(mediaType = "application/json")
        )
    })
    public ResponseEntity<DtoApiResponse<RestockRequest>> actualizarEstado(
            @Parameter(description = "ID of the restock request to update", required = true, example = "1")
            @PathVariable Long id,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                description = "Request containing the new status. Example: {\"estado\": \"APROBADA\"}",
                required = true,
                content = @Content(schema = @Schema(example = "{\"estado\": \"APROBADA\"}"))
            )
            @RequestBody Map<String, String> datos) {
        DtoApiResponse<RestockRequest> response = restockBffService.updateRestockStatus(id, datos);
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }

    @DeleteMapping("/{id}")
    @Operation(
        summary = "Delete restock request",
        description = "Permanently removes a restock request from the system. This action cannot be undone. Only requests with status 'PENDIENTE' or 'RECHAZADA' can be deleted.",
        security = @SecurityRequirement(name = "Bearer Authentication")
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "204",
            description = "Restock request deleted successfully",
            content = @Content
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Restock request not found",
            content = @Content(mediaType = "application/json")
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Restock request cannot be deleted in its current status",
            content = @Content(mediaType = "application/json")
        ),
        @ApiResponse(
            responseCode = "401",
            description = "Unauthorized - Valid authentication token required",
            content = @Content(mediaType = "application/json")
        ),
        @ApiResponse(
            responseCode = "403",
            description = "Forbidden - Insufficient permissions",
            content = @Content(mediaType = "application/json")
        )
    })
    public ResponseEntity<DtoApiResponse<Void>> eliminarSolicitud(
            @Parameter(description = "ID of the restock request to delete", required = true, example = "1")
            @PathVariable Long id) {
        DtoApiResponse<Void> response = restockBffService.deleteRestockRequest(id);
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }
}
package com.smartlogix.bff.controller;

import com.smartlogix.bff.dto.DtoApiResponse;
import com.smartlogix.bff.model.Item;
import com.smartlogix.bff.service.InventoryBffService;
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
@RequestMapping("/api/bff/inventory")
@Tag(name = "Gestion de Inventario", description = "Endpoints for managing warehouse inventory items, stock levels, and pricing")
public class InventoryBffController {

    private final InventoryBffService inventoryBffService;

    public InventoryBffController(InventoryBffService inventoryBffService) {
        this.inventoryBffService = inventoryBffService;
    }

    @GetMapping
    @Operation(
        summary = "List all inventory items",
        description = "Retrieves a complete list of all items in the inventory system. Supports pagination via query parameters.",
        security = @SecurityRequirement(name = "Bearer Authentication")
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Items retrieved successfully",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = Item.class))
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
    public ResponseEntity<DtoApiResponse<List<Item>>> listarItems() {
        DtoApiResponse<List<Item>> response = inventoryBffService.getAllItems();
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }

    @GetMapping("/{id}")
    @Operation(
        summary = "Get item by ID",
        description = "Retrieves detailed information about a specific inventory item by its unique identifier.",
        security = @SecurityRequirement(name = "Bearer Authentication")
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Item found and retrieved successfully",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = Item.class))
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
    public ResponseEntity<DtoApiResponse<Item>> obtenerItem(
            @Parameter(description = "Unique identifier of the item", required = true, example = "1001")
            @PathVariable Long id) {
        DtoApiResponse<Item> response = inventoryBffService.getItemById(id);
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }

    @GetMapping("/stock-bajo")
    @Operation(
        summary = "Get low stock items",
        description = "Retrieves all items with stock levels below the specified threshold. Useful for restock planning.",
        security = @SecurityRequirement(name = "Bearer Authentication")
    )
    public ResponseEntity<DtoApiResponse<List<Item>>> stockBajo(
            @Parameter(description = "Stock threshold value - items with stock ≤ this value will be returned", required = true, example = "10")
            @RequestParam Integer umbral) {
        DtoApiResponse<List<Item>> response = inventoryBffService.getLowStockItems(umbral);
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }

    @PostMapping
    @Operation(
        summary = "Create new inventory item",
        description = "Adds a new item to the inventory system. All fields must be provided.",
        security = @SecurityRequirement(name = "Bearer Authentication")
    )
    public ResponseEntity<DtoApiResponse<Item>> crearItem(
            @RequestBody Item item) {
        DtoApiResponse<Item> response = inventoryBffService.createItem(item);
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }

    @PutMapping("/{id}/cantidad")
    @Operation(
        summary = "Update item quantity",
        description = "Updates the stock quantity for a specific inventory item. Supports both absolute and delta updates.",
        security = @SecurityRequirement(name = "Bearer Authentication")
    )
    public ResponseEntity<DtoApiResponse<Item>> actualizarCantidad(
            @Parameter(description = "ID of the item to update", required = true, example = "1001")
            @PathVariable Long id,
            @RequestBody Map<String, Integer> datos) {
        DtoApiResponse<Item> response = inventoryBffService.updateItemQuantity(id, datos);
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }

    @PutMapping("/{id}/precio")
    @Operation(
        summary = "Update item price",
        description = "Updates the price for a specific inventory item. Supports both absolute and percentage updates.",
        security = @SecurityRequirement(name = "Bearer Authentication")
    )
    public ResponseEntity<DtoApiResponse<Item>> actualizarPrecio(
            @Parameter(description = "ID of the item to update", required = true, example = "1001")
            @PathVariable Long id,
            @RequestBody Map<String, Double> datos) {
        DtoApiResponse<Item> response = inventoryBffService.updateItemPrice(id, datos);
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }

    @DeleteMapping("/{id}")
    @Operation(
        summary = "Delete inventory item",
        description = "Permanently removes an item from the inventory system. This action cannot be undone.",
        security = @SecurityRequirement(name = "Bearer Authentication")
    )
    public ResponseEntity<DtoApiResponse<Void>> eliminarItem(
            @Parameter(description = "ID of the item to delete", required = true, example = "1001")
            @PathVariable Long id) {
        DtoApiResponse<Void> response = inventoryBffService.deleteItem(id);
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }
}
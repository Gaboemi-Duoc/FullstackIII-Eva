package com.smartlogix.bff.controller;

import com.smartlogix.bff.client.InventoryServiceClient;
import com.smartlogix.bff.model.Item;
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

    private final InventoryServiceClient inventoryServiceClient;

    public InventoryBffController(InventoryServiceClient inventoryServiceClient) {
        this.inventoryServiceClient = inventoryServiceClient;
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
    public ResponseEntity<List<Item>> listarItems() {
        return ResponseEntity.ok(inventoryServiceClient.getAllItems());
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
    public ResponseEntity<Item> obtenerItem(
            @Parameter(description = "Unique identifier of the item", required = true, example = "1001")
            @PathVariable Long id) {
        return ResponseEntity.ok(inventoryServiceClient.getItemById(id));
    }

    @GetMapping("/stock-bajo")
    @Operation(
        summary = "Get low stock items",
        description = "Retrieves all items with stock levels below the specified threshold. Useful for restock planning.",
        security = @SecurityRequirement(name = "Bearer Authentication")
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Low stock items retrieved successfully",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = Item.class))
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Invalid threshold value",
            content = @Content(mediaType = "application/json")
        ),
        @ApiResponse(
            responseCode = "401",
            description = "Unauthorized - Valid authentication token required",
            content = @Content(mediaType = "application/json")
        )
    })
    public ResponseEntity<List<Item>> stockBajo(
            @Parameter(description = "Stock threshold value - items with stock ≤ this value will be returned", required = true, example = "10")
            @RequestParam Integer umbral) {
        return ResponseEntity.ok(inventoryServiceClient.getStockBajo(umbral));
    }

    @PostMapping
    @Operation(
        summary = "Create new inventory item",
        description = "Adds a new item to the inventory system. All fields must be provided.",
        security = @SecurityRequirement(name = "Bearer Authentication")
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Item created successfully",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = Item.class))
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Validation error - Invalid item data",
            content = @Content(mediaType = "application/json")
        ),
        @ApiResponse(
            responseCode = "409",
            description = "Item already exists (duplicate SKU)",
            content = @Content(mediaType = "application/json")
        ),
        @ApiResponse(
            responseCode = "401",
            description = "Unauthorized - Valid authentication token required",
            content = @Content(mediaType = "application/json")
        )
    })
    public ResponseEntity<Item> crearItem(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                description = "Item object to be created",
                required = true,
                content = @Content(schema = @Schema(implementation = Item.class))
            )
            @RequestBody Item item) {
        return ResponseEntity.ok(inventoryServiceClient.crearItem(item));
    }

    @PutMapping("/{id}/cantidad")
    @Operation(
        summary = "Update item quantity",
        description = "Updates the stock quantity for a specific inventory item. Supports both absolute and delta updates.",
        security = @SecurityRequirement(name = "Bearer Authentication")
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Quantity updated successfully",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = Item.class))
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Item not found",
            content = @Content(mediaType = "application/json")
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Invalid quantity value",
            content = @Content(mediaType = "application/json")
        ),
        @ApiResponse(
            responseCode = "401",
            description = "Unauthorized - Valid authentication token required",
            content = @Content(mediaType = "application/json")
        )
    })
    public ResponseEntity<Item> actualizarCantidad(
            @Parameter(description = "ID of the item to update", required = true, example = "1001")
            @PathVariable Long id,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                description = "Map containing the new quantity value. Example: {\"cantidad\": 50}",
                required = true,
                content = @Content(schema = @Schema(example = "{\"cantidad\": 50}"))
            )
            @RequestBody Map<String, Integer> datos) {
        return ResponseEntity.ok(inventoryServiceClient.actualizarCantidad(id, datos));
    }

    @PutMapping("/{id}/precio")
    @Operation(
        summary = "Update item price",
        description = "Updates the price for a specific inventory item. Supports both absolute and percentage updates.",
        security = @SecurityRequirement(name = "Bearer Authentication")
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Price updated successfully",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = Item.class))
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Item not found",
            content = @Content(mediaType = "application/json")
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Invalid price value",
            content = @Content(mediaType = "application/json")
        ),
        @ApiResponse(
            responseCode = "401",
            description = "Unauthorized - Valid authentication token required",
            content = @Content(mediaType = "application/json")
        )
    })
    public ResponseEntity<Item> actualizarPrecio(
            @Parameter(description = "ID of the item to update", required = true, example = "1001")
            @PathVariable Long id,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                description = "Map containing the new price value. Example: {\"precio\": 29.99}",
                required = true,
                content = @Content(schema = @Schema(example = "{\"precio\": 29.99}"))
            )
            @RequestBody Map<String, Double> datos) {
        return ResponseEntity.ok(inventoryServiceClient.actualizarPrecio(id, datos));
    }

    @DeleteMapping("/{id}")
    @Operation(
        summary = "Delete inventory item",
        description = "Permanently removes an item from the inventory system. This action cannot be undone.",
        security = @SecurityRequirement(name = "Bearer Authentication")
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "204",
            description = "Item deleted successfully - No content returned",
            content = @Content
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
            responseCode = "403",
            description = "Forbidden - Insufficient permissions",
            content = @Content(mediaType = "application/json")
        )
    })
    public ResponseEntity<Void> eliminarItem(
            @Parameter(description = "ID of the item to delete", required = true, example = "1001")
            @PathVariable Long id) {
        inventoryServiceClient.eliminarItem(id);
        return ResponseEntity.noContent().build();
    }
}
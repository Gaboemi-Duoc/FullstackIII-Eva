package com.smartlogix.bff.controller;

import com.smartlogix.bff.model.Item;
import com.smartlogix.bff.service.InventoryBffService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/bff/inventory")
@CrossOrigin(origins = "*")
@Tag(name = "Gestion de Inventario", description = "Endpoints for managing warehouse inventory")
public class InventoryBffController {

    private final InventoryBffService inventoryBffService;

    public InventoryBffController(InventoryBffService inventoryBffService) {
        this.inventoryBffService = inventoryBffService;
    }

    @GetMapping
    @Operation(summary = "List all inventory items")
    public ResponseEntity<List<Item>> listarItems() {
        return ResponseEntity.ok(inventoryBffService.getAllItems());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get item by ID")
    public ResponseEntity<Item> obtenerItem(@PathVariable Long id) {
        return ResponseEntity.ok(inventoryBffService.getItemById(id));
    }

    @GetMapping("/stock-bajo")
    @Operation(summary = "Get low stock items")
    public ResponseEntity<List<Item>> stockBajo(@RequestParam Integer umbral) {
        return ResponseEntity.ok(inventoryBffService.getLowStockItems(umbral));
    }

    @PostMapping
    @Operation(summary = "Create new inventory item")
    public ResponseEntity<Item> crearItem(@RequestBody Item item) {
        return ResponseEntity.ok(inventoryBffService.createItem(item));
    }

    @PutMapping("/{id}/cantidad")
    @Operation(summary = "Update item quantity")
    public ResponseEntity<Item> actualizarCantidad(
            @PathVariable Long id,
            @RequestBody Map<String, Integer> datos) {
        return ResponseEntity.ok(inventoryBffService.updateItemQuantity(id, datos));
    }

    @PutMapping("/{id}/precio")
    @Operation(summary = "Update item price")
    public ResponseEntity<Item> actualizarPrecio(
            @PathVariable Long id,
            @RequestBody Map<String, Double> datos) {
        return ResponseEntity.ok(inventoryBffService.updateItemPrice(id, datos));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete inventory item")
    public ResponseEntity<Void> eliminarItem(@PathVariable Long id) {
        inventoryBffService.deleteItem(id);
        return ResponseEntity.noContent().build();
    }
}
package com.smartlogix.ms_inventory.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.smartlogix.ms_inventory.model.Item;
import com.smartlogix.ms_inventory.service.InventoryService;

import com.smartlogix.ms_inventory.dto.CreateItemRequest;
import com.smartlogix.ms_inventory.dto.UpdateCantidadRequest;
import com.smartlogix.ms_inventory.dto.UpdatePrecioRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import org.springframework.validation.annotation.Validated;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/inventory")
@CrossOrigin(origins = "*") // <-- CORREGIDO
@Validated
public class InventoryController {

    private final InventoryService inventoryService;

    public InventoryController(InventoryService inventoryService) {
        this.inventoryService = inventoryService;
    }

    @GetMapping
    public ResponseEntity<List<Item>> listarItems() {
        return ResponseEntity.ok(inventoryService.listarItems());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Item> obtenerItem(@PathVariable @Min(1) Long id) {
        return ResponseEntity.ok(inventoryService.obtenerPorId(id));
    }

    @GetMapping("/buscar")
    public ResponseEntity<Item> buscarPorNombre(@RequestParam @NotBlank String nombre) {
        return ResponseEntity.ok(inventoryService.buscarPorNombre(nombre));
    }

    @GetMapping("/stock-bajo")
    public ResponseEntity<List<Item>> stockBajo(@RequestParam @Min(0) Integer umbral) {
        return ResponseEntity.ok(inventoryService.itemsConStockBajo(umbral));
    }

    @PostMapping
    public ResponseEntity<Item> crearItem(@Valid @RequestBody CreateItemRequest request) {
        Item item = new Item();
        item.setNombre(request.getNombre());
        item.setDescripcion(request.getDescripcion());
        item.setCantidad(request.getCantidad());
        item.setPrecio(request.getPrecio());
        item.setBodega(request.getBodega());

        return ResponseEntity.ok(inventoryService.crearItem(item));
    }

    @PutMapping("/{id}/cantidad")
    public ResponseEntity<Item> actualizarCantidad(
            @PathVariable @Min(1) Long id,
            @Valid @RequestBody UpdateCantidadRequest request) {

        return ResponseEntity.ok(inventoryService.actualizarCantidad(id, request.getCantidad()));
    }

    @PutMapping("/{id}/precio")
    public ResponseEntity<Item> actualizarPrecio(
            @PathVariable @Min(1) Long id,
            @Valid @RequestBody UpdatePrecioRequest request) {

        return ResponseEntity.ok(inventoryService.actualizarPrecio(id, request.getPrecio()));
    }

    @GetMapping("/bodega")
    public ResponseEntity<List<Item>> itemsPorBodega(@RequestParam @NotBlank String nombre) {
        return ResponseEntity.ok(inventoryService.obtenerItemsPorBodega(nombre));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarItem(@PathVariable @Min(1) Long id) {
        inventoryService.eliminarItem(id);
        return ResponseEntity.noContent().build();
    }
}
package com.smartlogix.bff.service;

import com.smartlogix.bff.client.InventoryServiceClient;
import com.smartlogix.bff.dto.DtoApiResponse;
import com.smartlogix.bff.model.Item;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class InventoryBffService {

    private final InventoryServiceClient inventoryServiceClient;

    public InventoryBffService(InventoryServiceClient inventoryServiceClient) {
        this.inventoryServiceClient = inventoryServiceClient;
    }

    public DtoApiResponse<List<Item>> getAllItems() {
        try {
            log.info("Fetching all inventory items");
            List<Item> items = inventoryServiceClient.getAllItems();
            return new DtoApiResponse<>(true, "Items retrieved successfully", items, 200);
        } catch (Exception e) {
            log.error("Failed to fetch inventory items", e);
            return new DtoApiResponse<>(false, "Failed to retrieve items", null, 500);
        }
    }

    public DtoApiResponse<Item> getItemById(Long id) {
        try {
            log.info("Fetching item with ID: {}", id);
            Item item = inventoryServiceClient.getItemById(id);
            return new DtoApiResponse<>(true, "Item found", item, 200);
        } catch (Exception e) {
            log.error("Item not found with ID: {}", id, e);
            return new DtoApiResponse<>(false, "Item not found", null, 404);
        }
    }

    public DtoApiResponse<List<Item>> getLowStockItems(Integer threshold) {
        try {
            log.info("Fetching items with stock below threshold: {}", threshold);
            List<Item> items = inventoryServiceClient.getStockBajo(threshold);
            return new DtoApiResponse<>(true, "Low stock items retrieved", items, 200);
        } catch (Exception e) {
            log.error("Failed to fetch low stock items with threshold: {}", threshold, e);
            return new DtoApiResponse<>(false, "Failed to retrieve low stock items", null, 500);
        }
    }

    public DtoApiResponse<Item> createItem(Item item) {
        try {
            log.info("Creating new inventory item: {}", item.getNombre());
            Item createdItem = inventoryServiceClient.crearItem(item);
            return new DtoApiResponse<>(true, "Item created successfully", createdItem, 200);
        } catch (Exception e) {
            log.error("Failed to create item: {}", item.getNombre(), e);
            if (e.getMessage().contains("duplicate") || e.getMessage().contains("already exists")) {
                return new DtoApiResponse<>(false, "Item already exists", null, 409);
            }
            return new DtoApiResponse<>(false, "Failed to create item", null, 500);
        }
    }

    public DtoApiResponse<Item> updateItemQuantity(Long id, Map<String, Integer> datos) {
        try {
            log.info("Updating quantity for item ID: {}", id);
            Item updatedItem = inventoryServiceClient.actualizarCantidad(id, datos);
            return new DtoApiResponse<>(true, "Quantity updated successfully", updatedItem, 200);
        } catch (Exception e) {
            log.error("Failed to update quantity for item ID: {}", id, e);
            return new DtoApiResponse<>(false, "Failed to update quantity", null, 500);
        }
    }

    public DtoApiResponse<Item> updateItemPrice(Long id, Map<String, Double> datos) {
        try {
            log.info("Updating price for item ID: {}", id);
            Item updatedItem = inventoryServiceClient.actualizarPrecio(id, datos);
            return new DtoApiResponse<>(true, "Price updated successfully", updatedItem, 200);
        } catch (Exception e) {
            log.error("Failed to update price for item ID: {}", id, e);
            return new DtoApiResponse<>(false, "Failed to update price", null, 500);
        }
    }

    public DtoApiResponse<Void> deleteItem(Long id) {
        try {
            log.info("Deleting item with ID: {}", id);
            inventoryServiceClient.eliminarItem(id);
            return new DtoApiResponse<>(true, "Item deleted successfully", null, 204);
        } catch (Exception e) {
            log.error("Failed to delete item with ID: {}", id, e);
            return new DtoApiResponse<>(false, "Failed to delete item", null, 500);
        }
    }
}
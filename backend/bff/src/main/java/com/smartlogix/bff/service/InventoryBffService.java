package com.smartlogix.bff.service;

import com.smartlogix.bff.client.InventoryServiceClient;
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

    public List<Item> getAllItems() {
        try {
            log.info("Fetching all inventory items");
            return inventoryServiceClient.getAllItems();
        } catch (Exception e) {
            log.error("Failed to fetch inventory items", e);
            throw new RuntimeException("Failed to retrieve items: " + e.getMessage());
        }
    }

    public Item getItemById(Long id) {
        try {
            log.info("Fetching item with ID: {}", id);
            return inventoryServiceClient.getItemById(id);
        } catch (Exception e) {
            log.error("Failed to fetch item with ID: {}", id, e);
            throw new RuntimeException("Item not found with ID: " + id);
        }
    }

    public List<Item> getLowStockItems(Integer threshold) {
        try {
            log.info("Fetching items with stock below threshold: {}", threshold);
            return inventoryServiceClient.getStockBajo(threshold);
        } catch (Exception e) {
            log.error("Failed to fetch low stock items with threshold: {}", threshold, e);
            throw new RuntimeException("Failed to retrieve low stock items");
        }
    }

    public Item createItem(Item item) {
        try {
            log.info("Creating new inventory item: {}", item.getNombre());
            return inventoryServiceClient.crearItem(item);
        } catch (Exception e) {
            log.error("Failed to create item: {}", item.getNombre(), e);
            throw new RuntimeException("Failed to create item: " + e.getMessage());
        }
    }

    public Item updateItemQuantity(Long id, Map<String, Integer> datos) {
        try {
            log.info("Updating quantity for item ID: {}", id);
            return inventoryServiceClient.actualizarCantidad(id, datos);
        } catch (Exception e) {
            log.error("Failed to update quantity for item ID: {}", id, e);
            throw new RuntimeException("Failed to update quantity");
        }
    }

    public Item updateItemPrice(Long id, Map<String, Double> datos) {
        try {
            log.info("Updating price for item ID: {}", id);
            return inventoryServiceClient.actualizarPrecio(id, datos);
        } catch (Exception e) {
            log.error("Failed to update price for item ID: {}", id, e);
            throw new RuntimeException("Failed to update price");
        }
    }

    public void deleteItem(Long id) {
        try {
            log.info("Deleting item with ID: {}", id);
            inventoryServiceClient.eliminarItem(id);
        } catch (Exception e) {
            log.error("Failed to delete item with ID: {}", id, e);
            throw new RuntimeException("Failed to delete item");
        }
    }
}
package com.smartlogix.bff.service;

import com.smartlogix.bff.client.InventoryServiceClient;
import com.smartlogix.bff.client.RestockServiceClient;
import com.smartlogix.bff.dto.DtoApiResponse;
import com.smartlogix.bff.model.Item;
import com.smartlogix.bff.model.RestockRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class RestockBffService {

    private final RestockServiceClient restockServiceClient;
    private final InventoryServiceClient inventoryServiceClient;

    public RestockBffService(RestockServiceClient restockServiceClient,
                             InventoryServiceClient inventoryServiceClient) {
        this.restockServiceClient = restockServiceClient;
        this.inventoryServiceClient = inventoryServiceClient;
    }

    public DtoApiResponse<List<RestockRequest>> getAllRestockRequests() {
        try {
            log.info("Fetching all restock requests");
            List<RestockRequest> requests = restockServiceClient.listarSolicitudes();
            return new DtoApiResponse<>(true, "Restock requests retrieved successfully", requests, 200);
        } catch (Exception e) {
            log.error("Failed to fetch restock requests", e);
            return new DtoApiResponse<>(false, "Failed to retrieve restock requests", null, 500);
        }
    }

    public DtoApiResponse<RestockRequest> getRestockRequestById(Long id) {
        try {
            log.info("Fetching restock request with ID: {}", id);
            RestockRequest request = restockServiceClient.obtenerPorId(id);
            return new DtoApiResponse<>(true, "Restock request found", request, 200);
        } catch (Exception e) {
            log.error("Restock request not found with ID: {}", id, e);
            return new DtoApiResponse<>(false, "Restock request not found", null, 404);
        }
    }

    public DtoApiResponse<List<RestockRequest>> getRestockRequestsByStatus(String status) {
        try {
            log.info("Fetching restock requests with status: {}", status);
            List<RestockRequest> requests = restockServiceClient.listarPorEstado(status);
            return new DtoApiResponse<>(true, "Restock requests retrieved by status", requests, 200);
        } catch (Exception e) {
            log.error("Failed to fetch restock requests with status: {}", status, e);
            return new DtoApiResponse<>(false, "Failed to retrieve restock requests", null, 500);
        }
    }

    public DtoApiResponse<List<RestockRequest>> getRestockRequestsByItem(Long itemId) {
        try {
            log.info("Fetching restock requests for item ID: {}", itemId);
            List<RestockRequest> requests = restockServiceClient.listarPorItem(itemId);
            return new DtoApiResponse<>(true, "Restock requests retrieved by item", requests, 200);
        } catch (Exception e) {
            log.error("Failed to fetch restock requests for item ID: {}", itemId, e);
            return new DtoApiResponse<>(false, "Failed to retrieve restock requests", null, 500);
        }
    }

    public DtoApiResponse<List<RestockRequest>> getRestockRequestsByWarehouse(String warehouseName) {
        try {
            log.info("Fetching restock requests for warehouse: {}", warehouseName);
            List<RestockRequest> requests = restockServiceClient.listarPorBodega(warehouseName);
            return new DtoApiResponse<>(true, "Restock requests retrieved by warehouse", requests, 200);
        } catch (Exception e) {
            log.error("Failed to fetch restock requests for warehouse: {}", warehouseName, e);
            return new DtoApiResponse<>(false, "Failed to retrieve restock requests", null, 500);
        }
    }

    public DtoApiResponse<List<RestockRequest>> getPendingRequestsByWarehouse(String warehouseName) {
        try {
            log.info("Fetching pending restock requests for warehouse: {}", warehouseName);
            List<RestockRequest> requests = restockServiceClient.pendientesPorBodega(warehouseName);
            return new DtoApiResponse<>(true, "Pending restock requests retrieved", requests, 200);
        } catch (Exception e) {
            log.error("Failed to fetch pending restock requests for warehouse: {}", warehouseName, e);
            return new DtoApiResponse<>(false, "Failed to retrieve pending restock requests", null, 500);
        }
    }

    public DtoApiResponse<Map<String, Long>> getRestockSummary() {
        try {
            log.info("Fetching restock summary by status");
            Map<String, Long> summary = restockServiceClient.resumenPorEstado();
            return new DtoApiResponse<>(true, "Restock summary retrieved", summary, 200);
        } catch (Exception e) {
            log.error("Failed to fetch restock summary", e);
            return new DtoApiResponse<>(false, "Failed to retrieve restock summary", null, 500);
        }
    }

    public DtoApiResponse<RestockRequest> createRestockRequest(Map<String, Object> requestData) {
        try {
            log.info("Creating new restock request");
            RestockRequest createdRequest = restockServiceClient.crearSolicitud(requestData);
            return new DtoApiResponse<>(true, "Restock request created successfully", createdRequest, 200);
        } catch (Exception e) {
            log.error("Failed to create restock request", e);
            return new DtoApiResponse<>(false, "Failed to create restock request", null, 500);
        }
    }

    public DtoApiResponse<RestockRequest> updateRestockStatus(Long id, Map<String, String> statusData) {
        try {
            log.info("Updating status for restock request ID: {}", id);
            
            // Get the current request before update
            RestockRequest currentRequest = restockServiceClient.obtenerPorId(id);
            
            // Update the status
            RestockRequest updatedRequest = restockServiceClient.actualizarEstado(id, statusData);
            
            String newStatus = statusData.get("estado");
            
            // If status is COMPLETADA, update inventory stock
            if ("COMPLETADA".equalsIgnoreCase(newStatus)) {
                log.info("Restock request {} completed. Updating inventory.", id);
                
                Item item = inventoryServiceClient.getItemById(currentRequest.getIdItem());
                Map<String, Integer> stockUpdate = Map.of(
                        "cantidad",
                        item.getCantidad() + currentRequest.getCantidadSolicitada()
                );
                
                inventoryServiceClient.actualizarCantidad(
                        currentRequest.getIdItem(),
                        stockUpdate
                );
                
                log.info("Inventory updated for item ID: {}. New quantity: {}", 
                        currentRequest.getIdItem(), 
                        item.getCantidad() + currentRequest.getCantidadSolicitada());
            }
            
            return new DtoApiResponse<>(true, "Restock request status updated", updatedRequest, 200);
            
        } catch (Exception e) {
            log.error("Failed to update status for restock request ID: {}", id, e);
            return new DtoApiResponse<>(false, "Failed to update restock request status", null, 500);
        }
    }

    public DtoApiResponse<Void> deleteRestockRequest(Long id) {
        try {
            log.info("Deleting restock request with ID: {}", id);
            restockServiceClient.eliminarSolicitud(id);
            return new DtoApiResponse<>(true, "Restock request deleted successfully", null, 204);
        } catch (Exception e) {
            log.error("Failed to delete restock request with ID: {}", id, e);
            return new DtoApiResponse<>(false, "Failed to delete restock request", null, 500);
        }
    }
}
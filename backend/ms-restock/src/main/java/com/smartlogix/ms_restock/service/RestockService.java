package com.smartlogix.ms_restock.service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.smartlogix.ms_restock.model.RestockRequest;
import com.smartlogix.ms_restock.repository.RestockRepository;

@Service
public class RestockService {

    private final RestockRepository restockRepository;
    private final RestTemplate restTemplate;

    @Value("${inventory-service.url:http://localhost:8080}") // Agregamos un valor por defecto por si falla en los tests
    private String inventoryServiceUrl;

    // Aquí está la Buena Práctica: Inyección de Dependencias mediante el constructor
    public RestockService(RestockRepository restockRepository, RestTemplate restTemplate) {
        this.restockRepository = restockRepository;
        this.restTemplate = restTemplate;
    }

    // ─── Consultas ────────────────────────────────────────────────────────────
    // ... (TODO TU CÓDIGO HACIA ABAJO QUEDA EXACTAMENTE IGUAL) ...
    // ─── Consultas ────────────────────────────────────────────────────────────

    public List<RestockRequest> listarSolicitudes() {
        return restockRepository.findAll();
    }

    public RestockRequest obtenerPorId(Long id) {
        return restockRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Solicitud no encontrada con id: " + id));
    }

    public List<RestockRequest> listarPorEstado(String estado) {
        validarEstado(estado);
        return restockRepository.findByEstado(estado.toUpperCase());
    }

    public List<RestockRequest> listarPorItem(Long idItem) {
        return restockRepository.findByIdItem(idItem);
    }

    public List<RestockRequest> listarPorBodega(String bodega) {
        return restockRepository.findByBodega(bodega);
    }

    public List<RestockRequest> pendientesPorBodega(String bodega) {
        return restockRepository.findByBodegaAndEstado(bodega, RestockRequest.EstadoRestock.PENDIENTE.name());
    }

    public Map<String, Long> resumenPorEstado() {
        List<Object[]> filas = restockRepository.contarPorEstado();
        Map<String, Long> resumen = new HashMap<>();
        for (Object[] fila : filas) {
            String estado = (String) fila[0];
            Long total   = (Long)   fila[1];
            resumen.put(estado, total);
        }
        return resumen;
    }

    // ─── Creación ─────────────────────────────────────────────────────────────

    public RestockRequest crearSolicitud(RestockRequest solicitud) {
        // Validar que el ítem existe en ms-inventory
        try {
            restTemplate.getForObject(
                inventoryServiceUrl + "/api/inventory/" + solicitud.getIdItem(),
                Map.class
            );
        } catch (Exception e) {
            throw new RuntimeException(
                "No se pudo crear la solicitud: el ítem con id " +
                solicitud.getIdItem() + " no existe en el inventario."
            );
        }

        solicitud.setEstado(RestockRequest.EstadoRestock.PENDIENTE.name());
        solicitud.setFechaSolicitud(LocalDateTime.now());
        solicitud.setFechaActualizacion(null);
        return restockRepository.save(solicitud);
    }

    // ─── Actualización de estado ──────────────────────────────────────────────

    public RestockRequest actualizarEstado(Long id, String nuevoEstado) {
        validarEstado(nuevoEstado);
        RestockRequest solicitud = obtenerPorId(id);

        // Si se aprueba, actualizar stock en ms-inventory
        if (nuevoEstado.equalsIgnoreCase(RestockRequest.EstadoRestock.APROBADA.name())) {
            actualizarStockEnInventario(solicitud);
        }

        solicitud.setEstado(nuevoEstado.toUpperCase());
        solicitud.setFechaActualizacion(LocalDateTime.now());
        return restockRepository.save(solicitud);
    }

    // ─── Eliminación ──────────────────────────────────────────────────────────

    public void eliminarSolicitud(Long id) {
        obtenerPorId(id);
        restockRepository.deleteById(id);
    }

    // ─── Integración con ms-inventory ────────────────────────────────────────

    @SuppressWarnings("unchecked")
    private void actualizarStockEnInventario(RestockRequest solicitud) {
        try {
            // Obtener cantidad actual del ítem
            Map<String, Object> item = restTemplate.getForObject(
                inventoryServiceUrl + "/api/inventory/" + solicitud.getIdItem(),
                Map.class
            );

            if (item == null) throw new RuntimeException("Item no encontrado");

            int cantidadActual = ((Number) item.get("cantidad")).intValue();
            int nuevaCantidad  = cantidadActual + solicitud.getCantidadSolicitada();

            // Actualizar cantidad en ms-inventory
            Map<String, Integer> body = new HashMap<>();
            body.put("cantidad", nuevaCantidad);

            restTemplate.put(
                inventoryServiceUrl + "/api/inventory/" + solicitud.getIdItem() + "/cantidad",
                body
            );
        } catch (Exception e) {
            throw new RuntimeException(
                "No se pudo actualizar el stock en ms-inventory: " + e.getMessage()
            );
        }
    }

    // ─── Validación interna ───────────────────────────────────────────────────

    private void validarEstado(String estado) {
        try {
            RestockRequest.EstadoRestock.valueOf(estado.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new RuntimeException(
                "Estado inválido: '" + estado + "'. " +
                "Valores permitidos: PENDIENTE, APROBADA, RECHAZADA, COMPLETADA"
            );
        }
    }
}

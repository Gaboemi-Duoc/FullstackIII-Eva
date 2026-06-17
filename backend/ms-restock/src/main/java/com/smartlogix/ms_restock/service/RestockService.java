package com.smartlogix.ms_restock.service;

import com.smartlogix.ms_restock.model.RestockRequest;
import com.smartlogix.ms_restock.repository.RestockRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Service Layer: encapsula toda la lógica de negocio del microservicio.
 * El controlador sólo delega en esta capa; nunca accede al repositorio directamente.
 */
@Service
public class RestockService {

    private final RestockRepository restockRepository;

    public RestockService(RestockRepository restockRepository) {
        this.restockRepository = restockRepository;
    }

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

    public List<RestockRequest> listarPorItem(Long id_item) {
        return restockRepository.findByIdItem(id_item);
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
        solicitud.setEstado(RestockRequest.EstadoRestock.PENDIENTE.name());
        solicitud.setFecha_solicitud(LocalDateTime.now());
        solicitud.setFecha_actualizacion(null);
        return restockRepository.save(solicitud);
    }

    // ─── Actualización de estado ──────────────────────────────────────────────

    public RestockRequest actualizarEstado(Long id, String nuevoEstado) {
        validarEstado(nuevoEstado);
        RestockRequest solicitud = obtenerPorId(id);
        solicitud.setEstado(nuevoEstado.toUpperCase());
        solicitud.setFecha_actualizacion(LocalDateTime.now());
        return restockRepository.save(solicitud);
    }

    // ─── Eliminación ──────────────────────────────────────────────────────────

    public void eliminarSolicitud(Long id) {
        obtenerPorId(id);
        restockRepository.deleteById(id);
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

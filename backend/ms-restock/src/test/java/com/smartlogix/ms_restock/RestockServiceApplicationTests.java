package com.smartlogix.ms_restock;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestTemplate;

import com.smartlogix.ms_restock.model.RestockRequest;
import com.smartlogix.ms_restock.repository.RestockRepository;
import com.smartlogix.ms_restock.service.RestockService;

@ExtendWith(MockitoExtension.class)
class RestockServiceApplicationTests {

    @Mock
    private RestockRepository restockRepository;

    @Mock
    private RestTemplate restTemplate; // Mockito ahora puede inyectar esto limpiamente

    @InjectMocks
    private RestockService restockService;

    private RestockRequest solicitudMock;

    @BeforeEach
    void setUp() {
        // Solo inyectamos la variable de entorno que no levanta Mockito
        ReflectionTestUtils.setField(restockService, "inventoryServiceUrl", "http://mock-url.com");

        solicitudMock = new RestockRequest();
        solicitudMock.setIdRestock(1L);
        solicitudMock.setIdItem(10L);
        solicitudMock.setNombreItem("Caja A");
        solicitudMock.setBodega("Bodega Central");
        solicitudMock.setCantidadSolicitada(50);
        solicitudMock.setEstado("PENDIENTE");
        solicitudMock.setFechaSolicitud(LocalDateTime.now());
    }

    // ─── listarSolicitudes ────────────────────────────────────────────────────

    @Test
    @DisplayName("listarSolicitudes: retorna lista de solicitudes")
    void listarSolicitudes_retornaLista() {
        when(restockRepository.findAll()).thenReturn(Arrays.asList(solicitudMock));

        List<RestockRequest> resultado = restockService.listarSolicitudes();

        assertEquals(1, resultado.size());
        assertEquals("Caja A", resultado.get(0).getNombreItem());
    }

    // ─── obtenerPorId ─────────────────────────────────────────────────────────

    @Test
    @DisplayName("obtenerPorId: retorna solicitud cuando existe")
    void obtenerPorId_existente_retornaSolicitud() {
        when(restockRepository.findById(1L)).thenReturn(Optional.of(solicitudMock));

        RestockRequest resultado = restockService.obtenerPorId(1L);

        assertNotNull(resultado);
        assertEquals(1L, resultado.getIdRestock());
    }

    @Test
    @DisplayName("obtenerPorId: lanza excepcion cuando no existe")
    void obtenerPorId_noExistente_lanzaExcepcion() {
        when(restockRepository.findById(99L)).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> restockService.obtenerPorId(99L));

        assertTrue(ex.getMessage().contains("99"));
    }

    // ─── crearSolicitud ───────────────────────────────────────────────────────

    @Test
    @DisplayName("crearSolicitud: fuerza estado PENDIENTE y persiste")
    void crearSolicitud_fuerzaEstadoPendiente() {
        RestockRequest nueva = new RestockRequest();
        nueva.setIdItem(5L);
        nueva.setNombreItem("Pallet B");
        nueva.setBodega("Bodega Norte");
        nueva.setCantidadSolicitada(20);
        nueva.setEstado("APROBADA");

        // Simular que el ítem existe en ms-inventory (devuelve un Map vacío)
        lenient().when(restTemplate.getForObject(anyString(), eq(Map.class)))
                 .thenReturn(new HashMap<>());

        when(restockRepository.save(any(RestockRequest.class)))
                .thenAnswer(inv -> inv.getArgument(0));

        RestockRequest resultado = restockService.crearSolicitud(nueva);

        assertEquals("PENDIENTE", resultado.getEstado());
        assertNotNull(resultado.getFechaSolicitud());
        assertNull(resultado.getFechaActualizacion());
    }

    // ─── actualizarEstado ─────────────────────────────────────────────────────

    @Test
    @DisplayName("actualizarEstado: actualiza a APROBADA y registra timestamp")
    void actualizarEstado_estadoValido_actualizaCorrectamente() {
        when(restockRepository.findById(1L)).thenReturn(Optional.of(solicitudMock));
        when(restockRepository.save(any(RestockRequest.class)))
                .thenAnswer(inv -> inv.getArgument(0));

        // Simular la respuesta del ms-inventory con la cantidad actual del ítem
        Map<String, Object> itemResponse = new HashMap<>();
        itemResponse.put("cantidad", 100); 
        
        lenient().when(restTemplate.getForObject(anyString(), eq(Map.class)))
                 .thenReturn(itemResponse);
                 
        // Simular que el método PUT no hace nada (es exitoso)
        lenient().doNothing().when(restTemplate).put(anyString(), any());

        RestockRequest resultado = restockService.actualizarEstado(1L, "APROBADA");

        assertEquals("APROBADA", resultado.getEstado());
        assertNotNull(resultado.getFechaActualizacion());
    }

    @Test
    @DisplayName("actualizarEstado: lanza excepcion con estado invalido")
    void actualizarEstado_estadoInvalido_lanzaExcepcion() {
        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> restockService.actualizarEstado(1L, "INVALIDO"));

        assertTrue(ex.getMessage().contains("Estado inválido"));
    }

    // ─── listarPorEstado ──────────────────────────────────────────────────────

    @Test
    @DisplayName("listarPorEstado: retorna solicitudes filtradas por estado")
    void listarPorEstado_estadoValido_retornaLista() {
        when(restockRepository.findByEstado("PENDIENTE"))
                .thenReturn(Arrays.asList(solicitudMock));

        List<RestockRequest> resultado = restockService.listarPorEstado("PENDIENTE");

        assertEquals(1, resultado.size());
        assertEquals("PENDIENTE", resultado.get(0).getEstado());
    }

    @Test
    @DisplayName("listarPorEstado: lanza excepcion con estado invalido")
    void listarPorEstado_estadoInvalido_lanzaExcepcion() {
        assertThrows(RuntimeException.class,
                () -> restockService.listarPorEstado("DESCONOCIDO"));
    }

    // ─── eliminarSolicitud ────────────────────────────────────────────────────

    @Test
    @DisplayName("eliminarSolicitud: llama deleteById cuando la solicitud existe")
    void eliminarSolicitud_existente_eliminaCorrectamente() {
        when(restockRepository.findById(1L)).thenReturn(Optional.of(solicitudMock));
        doNothing().when(restockRepository).deleteById(1L);

        assertDoesNotThrow(() -> restockService.eliminarSolicitud(1L));
        verify(restockRepository, times(1)).deleteById(1L);
    }

    @Test
    @DisplayName("eliminarSolicitud: lanza excepcion si no existe")
    void eliminarSolicitud_noExistente_lanzaExcepcion() {
        when(restockRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class,
                () -> restockService.eliminarSolicitud(99L));
    }

    // ─── listarPorBodega ──────────────────────────────────────────────────────

    @Test
    @DisplayName("listarPorBodega: retorna solicitudes de la bodega indicada")
    void listarPorBodega_retornaSolicitudesDeBodega() {
        when(restockRepository.findByBodega("Bodega Central"))
                .thenReturn(Arrays.asList(solicitudMock));

        List<RestockRequest> resultado = restockService.listarPorBodega("Bodega Central");

        assertEquals(1, resultado.size());
        assertEquals("Bodega Central", resultado.get(0).getBodega());
    }
}
package com.smartlogix.ms_user;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;

import com.smartlogix.ms_user.model.User;
import com.smartlogix.ms_user.repository.UserRepository;
import com.smartlogix.ms_user.service.UserService;

@ExtendWith(MockitoExtension.class)
class UserServiceApplicationTests {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    private User usuarioMock;

    @BeforeEach
    void setUp() {
        usuarioMock = new User();
        usuarioMock.setId_user(1L);
        usuarioMock.setUsername("admin");
        usuarioMock.setEmail("admin@smartlogix.com");
        usuarioMock.setPassword("admin123");
    }

    @Test
    @DisplayName("listarUsuarios: retorna lista de usuarios")
    void listarUsuarios_retornaLista() {
        when(userRepository.findAll()).thenReturn(Arrays.asList(usuarioMock));

        List<User> resultado = userService.listarUsuarios();

        assertEquals(1, resultado.size());
        assertEquals("admin", resultado.get(0).getUsername());
        verify(userRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("obtenerPorId: retorna usuario cuando existe")
    void obtenerPorId_existente_retornaUsuario() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(usuarioMock));

        User resultado = userService.obtenerPorId(1L);

        assertNotNull(resultado);
        assertEquals(1L, resultado.getId_user());
    }

    @Test
    @DisplayName("obtenerPorId: lanza excepcion cuando no existe")
    void obtenerPorId_noExistente_lanzaExcepcion() {
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> userService.obtenerPorId(99L));

        assertTrue(ex.getMessage().contains("Usuario no encontrado"));
    }

    @Test
    @DisplayName("registrarUsuario: guarda y retorna el usuario")
    void registrarUsuario_guardaCorrectamente() {
        when(userRepository.save(any(User.class))).thenReturn(usuarioMock);

        User resultado = userService.registrarUsuario(usuarioMock);

        assertNotNull(resultado);
        assertEquals("admin@smartlogix.com", resultado.getEmail());
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    @DisplayName("login: retorna usuario con credenciales correctas")
    void login_credencialesCorrectas_retornaUsuario() {
        when(userRepository.findByUsernameAndPassword("admin", "admin123"))
                .thenReturn(Optional.of(usuarioMock));

        User resultado = userService.login("admin", "admin123");

        assertNotNull(resultado);
        assertEquals("admin", resultado.getUsername());
    }

    @Test
    @DisplayName("login: lanza excepcion con credenciales incorrectas")
    void login_credencialesIncorrectas_lanzaExcepcion() {
        when(userRepository.findByUsernameAndPassword("admin", "wrongpass"))
                .thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> userService.login("admin", "wrongpass"));

        assertTrue(ex.getMessage().contains("Credenciales incorrectas"));
    }

    @Test
    @DisplayName("actualizarUsername: actualiza y retorna usuario modificado")
    void actualizarUsername_actualizaCorrectamente() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(usuarioMock));
        when(userRepository.save(any(User.class))).thenAnswer(inv -> inv.getArgument(0));

        User resultado = userService.actualizarUsername(1L, "nuevo_admin");

        assertEquals("nuevo_admin", resultado.getUsername());
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    @DisplayName("eliminarUsuario: llama deleteById correctamente")
    void eliminarUsuario_eliminaCorrectamente() {
        doNothing().when(userRepository).deleteById(1L);

        assertDoesNotThrow(() -> userService.eliminarUsuario(1L));
        verify(userRepository, times(1)).deleteById(1L);
    }
}

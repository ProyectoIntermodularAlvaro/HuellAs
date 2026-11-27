package huellas;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import huellas.controller.HuellasController;
import huellas.model.Animal;
import huellas.model.Dueno;
import huellas.net.ConexionServidor;
import huellas.net.MensajeRequest;
import huellas.net.Servidor;
import huellas.repository.AnimalRepository;
import huellas.repository.DuenoRepository;
import huellas.service.HuellasService;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import org.junit.jupiter.api.io.TempDir;

@Timeout(60)
class ServidorTest {

    @TempDir
    Path carpeta;

    private Servidor servidor;
    private HuellasController controlador;

    @BeforeEach
    void arrancar() throws IOException {
        HuellasService servicio = new HuellasService(new DuenoRepository(carpeta.toString()),
                new AnimalRepository(carpeta.toString()));
        servidor = new Servidor(servicio);
        servidor.iniciar(0);
        controlador = new HuellasController(conectar());
    }

    @AfterEach
    void parar() throws IOException {
        controlador.cerrar();
        servidor.detener();
    }

    private ConexionServidor conectar() throws IOException {
        return new ConexionServidor("localhost", servidor.getPuerto());
    }

    private void altaYLogin() throws IOException {
        controlador.registrar("Alvaro", "alvaro@huellas.es", "Huellas2026", "926123456");
        controlador.login("alvaro@huellas.es", "Huellas2026");
    }

    @Test
    void registrarEIniciarSesionPorLaRed() throws IOException {
        assertTrue(controlador.registrar("Alvaro", "alvaro@huellas.es", "Huellas2026", "926123456")
                .isCorrecto());
        assertTrue(controlador.login("alvaro@huellas.es", "Huellas2026").isCorrecto());
        assertEquals("Alvaro", controlador.getUsuario().getNombre());
    }

    @Test
    void laContrasenaNoLlegaAlCliente() throws IOException {
        altaYLogin();
        assertNull(controlador.getUsuario().getPassword());
    }

    @Test
    void losErroresDeValidacionLleganAlCliente() throws IOException {
        assertEquals("El email no es válido",
                controlador.registrar("Alvaro", "no-es-email", "Huellas2026", "926123456").getMensaje());
        assertEquals("Email o contraseña incorrectos",
                controlador.login("nadie@huellas.es", "Huellas2026").getMensaje());
    }

    @Test
    void altaYListadoDeMascotasPorLaRed() throws IOException {
        altaYLogin();
        assertTrue(controlador.listarMascotas().isEmpty());
        assertTrue(controlador.nuevaMascota("Toby", "Perro", "Mestizo", 3).isCorrecto());

        List<Animal> mascotas = controlador.listarMascotas();
        assertEquals(1, mascotas.size());
        assertEquals("Toby", mascotas.get(0).getNombre());
    }

    @Test
    void sinIniciarSesionNoSePuedeOperar() {
        IOException e = assertThrows(IOException.class, () -> controlador.listarMascotas());
        assertEquals("Debes iniciar sesión", e.getMessage());
    }

    @Test
    void cerrarSesionBloqueaLasOperaciones() throws IOException {
        altaYLogin();
        controlador.logout();
        assertNull(controlador.getUsuario());
        assertThrows(IOException.class, () -> controlador.listarMascotas());
    }

    @Test
    void cadaConexionTieneSuPropiaSesion() throws IOException {
        altaYLogin();
        HuellasController otro = new HuellasController(conectar());
        assertThrows(IOException.class, () -> otro.listarMascotas());
        assertEquals(0, controlador.listarMascotas().size());
        otro.cerrar();
    }

    @Test
    void losDatosSonComunesATodosLosClientes() throws IOException {
        controlador.registrar("Alvaro", "alvaro@huellas.es", "Huellas2026", "926123456");
        HuellasController otro = new HuellasController(conectar());
        assertTrue(otro.login("alvaro@huellas.es", "Huellas2026").isCorrecto());
        otro.cerrar();
    }

    @Test
    void unErrorNoCortaLaConexion() throws IOException {
        assertFalse(controlador.login("nadie@huellas.es", "Huellas2026").isCorrecto());
        assertTrue(controlador.registrar("Alvaro", "alvaro@huellas.es", "Huellas2026", "926123456")
                .isCorrecto());
    }

    @Test
    void unaAccionDesconocidaSeResponseConError() throws IOException {
        ConexionServidor conexion = conectar();
        assertEquals("Acción desconocida",
                conexion.enviar(new MensajeRequest("BORRAR_TODO")).getMensaje());
        conexion.close();
    }

    @Test
    void detenerCierraLasConexionesAbiertas() throws IOException {
        altaYLogin();
        servidor.detener();
        assertThrows(IOException.class, () -> controlador.listarMascotas());
    }

    @Test
    void elUsuarioQueLlegaEsUnDueno() throws IOException {
        altaYLogin();
        assertTrue(controlador.getUsuario() instanceof Dueno);
    }
}

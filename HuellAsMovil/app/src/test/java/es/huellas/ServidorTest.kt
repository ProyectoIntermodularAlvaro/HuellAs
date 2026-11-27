package es.huellas

import es.huellas.controller.HuellasController
import es.huellas.model.Dueno
import es.huellas.net.ConexionServidor
import es.huellas.net.MensajeRequest
import es.huellas.net.Servidor
import es.huellas.repository.AnimalRepository
import es.huellas.repository.DuenoRepository
import es.huellas.service.HuellasService
import java.io.IOException
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder

class ServidorTest {

    @get:Rule
    val carpeta = TemporaryFolder()

    private lateinit var servidor: Servidor
    private lateinit var controlador: HuellasController

    @Before
    fun arrancar() {
        val servicio = HuellasService(DuenoRepository(carpeta.root), AnimalRepository(carpeta.root))
        servidor = Servidor(servicio)
        servidor.iniciar(0)
        controlador = HuellasController(conectar())
    }

    @After
    fun parar() {
        runCatching { controlador.cerrar() }
        servidor.detener()
    }

    private fun conectar() = ConexionServidor("127.0.0.1", servidor.puerto)

    private fun altaYLogin() {
        controlador.registrar("Alvaro", "alvaro@huellas.es", "Huellas2026", "926123456")
        controlador.login("alvaro@huellas.es", "Huellas2026")
    }

    @Test
    fun registrarEIniciarSesionPorLaRed() {
        assertTrue(controlador.registrar("Alvaro", "alvaro@huellas.es", "Huellas2026", "926123456").correcto)
        assertTrue(controlador.login("alvaro@huellas.es", "Huellas2026").correcto)
        assertEquals("Alvaro", controlador.usuario?.nombre)
    }

    @Test
    fun laContrasenaNoLlegaAlCliente() {
        altaYLogin()
        assertNull(controlador.usuario?.password)
    }

    @Test
    fun losErroresDeValidacionLleganAlCliente() {
        assertEquals("El email no es válido",
            controlador.registrar("Alvaro", "no-es-email", "Huellas2026", "926123456").mensaje)
        assertEquals("Email o contraseña incorrectos",
            controlador.login("nadie@huellas.es", "Huellas2026").mensaje)
    }

    @Test
    fun altaYListadoDeMascotasPorLaRed() {
        altaYLogin()
        assertTrue(controlador.listarMascotas().isEmpty())
        assertTrue(controlador.nuevaMascota("Toby", "Perro", "Mestizo", 3).correcto)

        val mascotas = controlador.listarMascotas()
        assertEquals(1, mascotas.size)
        assertEquals("Toby", mascotas[0].nombre)
    }

    @Test
    fun sinIniciarSesionNoSePuedeOperar() {
        try {
            controlador.listarMascotas()
            throw AssertionError("deberia exigir sesion")
        } catch (e: IOException) {
            assertEquals("Debes iniciar sesión", e.message)
        }
    }

    @Test(expected = IOException::class)
    fun cerrarSesionBloqueaLasOperaciones() {
        altaYLogin()
        controlador.logout()
        assertNull(controlador.usuario)
        controlador.listarMascotas()
    }

    @Test
    fun cadaConexionTieneSuPropiaSesion() {
        altaYLogin()
        val otro = HuellasController(conectar())
        try {
            otro.listarMascotas()
            throw AssertionError("la segunda conexion no deberia tener sesion")
        } catch (e: IOException) {
            assertEquals("Debes iniciar sesión", e.message)
        }
        assertEquals(0, controlador.listarMascotas().size)
        otro.cerrar()
    }

    @Test
    fun losDatosSonComunesATodosLosClientes() {
        controlador.registrar("Alvaro", "alvaro@huellas.es", "Huellas2026", "926123456")
        val otro = HuellasController(conectar())
        assertTrue(otro.login("alvaro@huellas.es", "Huellas2026").correcto)
        otro.cerrar()
    }

    @Test
    fun unErrorNoCortaLaConexion() {
        assertFalse(controlador.login("nadie@huellas.es", "Huellas2026").correcto)
        assertTrue(controlador.registrar("Alvaro", "alvaro@huellas.es", "Huellas2026", "926123456").correcto)
    }

    @Test
    fun unaAccionDesconocidaSeRespondeConError() {
        val conexion = conectar()
        assertEquals("Acción desconocida",
            conexion.enviar(MensajeRequest("BORRAR_TODO")).mensaje)
        conexion.close()
    }

    @Test(expected = IOException::class)
    fun detenerCierraLasConexionesAbiertas() {
        altaYLogin()
        servidor.detener()
        controlador.listarMascotas()
    }

    @Test
    fun elUsuarioQueLlegaEsUnDueno() {
        altaYLogin()
        assertTrue(controlador.usuario is Dueno)
    }
}

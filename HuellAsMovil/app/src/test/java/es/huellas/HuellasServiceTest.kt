package es.huellas

import es.huellas.repository.AnimalRepository
import es.huellas.repository.DuenoRepository
import es.huellas.service.HuellasService
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder

class HuellasServiceTest {

    @get:Rule
    val carpeta = TemporaryFolder()

    private lateinit var servicio: HuellasService

    @Before
    fun preparar() {
        servicio = nuevoServicio()
    }

    private fun nuevoServicio() =
        HuellasService(DuenoRepository(carpeta.root), AnimalRepository(carpeta.root))

    private fun errorAlRegistrar(nombre: String?, email: String?, password: String?, telefono: String?) =
        try {
            servicio.registrar(nombre, email, password, telefono)
            "no fallo"
        } catch (e: IllegalArgumentException) {
            e.message
        }

    @Test
    fun registroCorrecto() {
        val dueno = servicio.registrar("Alvaro", "Alvaro@Huellas.ES", "Huellas2026", "926 123 456")
        assertEquals(1, dueno.id)
        assertEquals("alvaro@huellas.es", dueno.email)
        assertEquals("926123456", dueno.telefono)
    }

    @Test
    fun losIdsSonCorrelativos() {
        assertEquals(1, servicio.registrar("Alvaro", "a@huellas.es", "Clave1234", "926123456").id)
        assertEquals(2, servicio.registrar("Ana", "b@huellas.es", "Clave1234", "926123456").id)
    }

    @Test
    fun noSeAdmitenDosCuentasConElMismoEmail() {
        servicio.registrar("Alvaro", "a@huellas.es", "Clave1234", "926123456")
        assertEquals("Ya existe un usuario con ese email",
            errorAlRegistrar("Otro", "A@HUELLAS.ES", "Clave1234", "926123456"))
    }

    @Test
    fun emailInvalido() {
        listOf("sin-arroba", "a@", "@huellas.es", "a@huellas").forEach {
            assertEquals("El email no es válido",
                errorAlRegistrar("Alvaro", it, "Clave1234", "926123456"))
        }
    }

    @Test
    fun contrasenaCorta() {
        assertEquals("La contraseña debe tener al menos 8 caracteres",
            errorAlRegistrar("Alvaro", "a@huellas.es", "corta", "926123456"))
    }

    @Test
    fun telefonoInvalido() {
        assertEquals("El teléfono debe tener entre 9 y 15 dígitos",
            errorAlRegistrar("Alvaro", "a@huellas.es", "Clave1234", "12345678"))
        assertEquals("El teléfono debe tener entre 9 y 15 dígitos",
            errorAlRegistrar("Alvaro", "a@huellas.es", "Clave1234", "letras"))
    }

    @Test
    fun nombreObligatorio() {
        assertEquals("El nombre es obligatorio",
            errorAlRegistrar("  ", "a@huellas.es", "Clave1234", "926123456"))
    }

    @Test
    fun loginCorrecto() {
        val alta = servicio.registrar("Alvaro", "a@huellas.es", "Clave1234", "926123456")
        assertEquals(alta.id, servicio.login(" A@Huellas.ES ", "Clave1234").id)
    }

    @Test
    fun elErrorDeLoginEsIgualFalleElEmailOLaPassword() {
        servicio.registrar("Alvaro", "a@huellas.es", "Clave1234", "926123456")
        val passwordMala = try {
            servicio.login("a@huellas.es", "otra"); ""
        } catch (e: IllegalArgumentException) {
            e.message
        }
        val emailInexistente = try {
            servicio.login("nadie@huellas.es", "Clave1234"); ""
        } catch (e: IllegalArgumentException) {
            e.message
        }
        assertEquals(passwordMala, emailInexistente)
        assertEquals("Email o contraseña incorrectos", passwordMala)
    }

    @Test
    fun altaYListadoDeMascotas() {
        val dueno = servicio.registrar("Alvaro", "a@huellas.es", "Clave1234", "926123456")
        servicio.registrarMascota(dueno.id, "Toby", "Perro", "Mestizo", 3)
        servicio.registrarMascota(dueno.id, "Luna", "Gato", "", 2)

        assertEquals(2, servicio.listarMascotas(dueno.id).size)
        assertEquals("Toby", servicio.listarMascotas(dueno.id)[0].nombre)
    }

    @Test
    fun cadaDuenoVeSoloSusMascotas() {
        val alvaro = servicio.registrar("Alvaro", "a@huellas.es", "Clave1234", "926123456")
        val ana = servicio.registrar("Ana", "b@huellas.es", "Clave1234", "926123456")
        servicio.registrarMascota(alvaro.id, "Toby", "Perro", "", 3)

        assertEquals(1, servicio.listarMascotas(alvaro.id).size)
        assertTrue(servicio.listarMascotas(ana.id).isEmpty())
    }

    @Test(expected = IllegalArgumentException::class)
    fun mascotaDeUnDuenoQueNoExiste() {
        servicio.registrarMascota(99, "Toby", "Perro", "", 3)
    }

    @Test
    fun unAltaRechazadaNoDejaRastro() {
        errorAlRegistrar("Alvaro", "mal-email", "Clave1234", "926123456")
        assertEquals(1, servicio.registrar("Alvaro", "a@huellas.es", "Clave1234", "926123456").id)
    }

    @Test
    fun losDatosSobrevivenAlReinicio() {
        val dueno = servicio.registrar("Alvaro", "a@huellas.es", "Clave1234", "926123456")
        servicio.registrarMascota(dueno.id, "Toby", "Perro", "Mestizo", 3)

        val otro = nuevoServicio()
        val recuperado = otro.login("a@huellas.es", "Clave1234")
        assertEquals(1, otro.listarMascotas(recuperado.id).size)
        assertEquals("Toby", otro.listarMascotas(recuperado.id)[0].nombre)
    }
}

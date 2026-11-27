package es.huellas

import es.huellas.model.Dueno
import es.huellas.model.EstadoAnimal
import es.huellas.model.Usuario
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class UsuarioTest {

    private val dueno = Dueno(1, "Alvaro", "alvaro@huellas.es", "Huellas2026", "926123456")

    @Test
    fun loginCorrecto() {
        assertTrue(dueno.login("alvaro@huellas.es", "Huellas2026"))
    }

    @Test
    fun loginIgnoraMayusculasEnElEmail() {
        assertTrue(dueno.login("ALVARO@HUELLAS.ES", "Huellas2026"))
        assertTrue(dueno.login("  alvaro@huellas.es ", "Huellas2026"))
    }

    @Test
    fun loginFallaConDatosIncorrectos() {
        assertFalse(dueno.login("alvaro@huellas.es", "otra"))
        assertFalse(dueno.login("otro@huellas.es", "Huellas2026"))
    }

    @Test
    fun loginConNulosDevuelveFalse() {
        assertFalse(dueno.login(null, "Huellas2026"))
        assertFalse(dueno.login("alvaro@huellas.es", null))
    }

    @Test
    fun duenoHeredaDeUsuario() {
        val usuario: Usuario = dueno
        assertTrue(usuario.login("alvaro@huellas.es", "Huellas2026"))
        assertEquals("Alvaro", usuario.nombre)
    }

    @Test
    fun registrarMascotaDevuelveAnimalEnCasa() {
        val toby = dueno.registrarMascota(1, "Toby", "Perro", "Mestizo", 3)
        assertEquals("Toby", toby.nombre)
        assertEquals("Perro", toby.especie)
        assertEquals(3, toby.edad)
        assertEquals(EstadoAnimal.EN_CASA, toby.estado)
        assertEquals(dueno.id, toby.idDueno)
    }

    @Test
    fun registrarMascotaSinRazaLaDejaVacia() {
        assertEquals("", dueno.registrarMascota(1, "Toby", "Perro", null, 3).raza)
    }

    @Test(expected = IllegalArgumentException::class)
    fun registrarMascotaExigeNombre() {
        dueno.registrarMascota(1, "  ", "Perro", "", 3)
    }

    @Test(expected = IllegalArgumentException::class)
    fun registrarMascotaExigeEspecie() {
        dueno.registrarMascota(1, "Toby", null, "", 3)
    }

    @Test
    fun seAceptanLasEdadesLimite() {
        assertEquals(0, dueno.registrarMascota(1, "Toby", "Perro", "", 0).edad)
        assertEquals(40, dueno.registrarMascota(2, "Tortuga", "Tortuga", "", 40).edad)
    }

    @Test(expected = IllegalArgumentException::class)
    fun edadNegativa() {
        dueno.registrarMascota(1, "Toby", "Perro", "", -1)
    }

    @Test(expected = IllegalArgumentException::class)
    fun edadDemasiadoAlta() {
        dueno.registrarMascota(1, "Toby", "Perro", "", 41)
    }
}

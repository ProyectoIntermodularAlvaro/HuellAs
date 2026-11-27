package es.huellas

import es.huellas.model.Animal
import es.huellas.model.Dueno
import es.huellas.repository.AnimalRepository
import es.huellas.repository.DuenoRepository
import es.huellas.repository.EscritorObjetos
import es.huellas.repository.LectorObjetos
import java.io.File
import java.io.IOException
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder

class RepositorioTest {

    @get:Rule
    val carpeta = TemporaryFolder()

    private fun fichero(nombre: String) = File(carpeta.root, nombre)

    @Test
    fun loQueSeEscribeSeVuelveALeer() {
        val lista = listOf(
            Dueno(1, "Alvaro", "alvaro@huellas.es", "Huellas2026", "926123456"),
            Dueno(2, "Ana", "ana@huellas.es", "Clave1234", "600111222")
        )
        EscritorObjetos<Dueno>(fichero("duenos.dat")).escribe(lista)

        val leidos = LectorObjetos<Dueno>(fichero("duenos.dat")).lee()
        assertEquals(2, leidos.size)
        assertEquals("Alvaro", leidos[0].nombre)
        assertTrue(leidos[0].login("alvaro@huellas.es", "Huellas2026"))
    }

    @Test
    fun ficheroQueNoExisteDevuelveListaVacia() {
        val lector = LectorObjetos<Dueno>(fichero("nada.dat"))
        assertFalse(lector.existe())
        assertTrue(lector.lee().isEmpty())
    }

    @Test
    fun listaVaciaSeGuardaYSeLeeVacia() {
        EscritorObjetos<Dueno>(fichero("duenos.dat")).escribe(emptyList())
        assertTrue(LectorObjetos<Dueno>(fichero("duenos.dat")).lee().isEmpty())
    }

    @Test
    fun escribirDosVecesSustituyeElContenido() {
        val escritor = EscritorObjetos<Dueno>(fichero("duenos.dat"))
        escritor.escribe(listOf(Dueno(1, "Alvaro", "alvaro@huellas.es", "Huellas2026", "926123456")))
        escritor.escribe(listOf(Dueno(2, "Ana", "ana@huellas.es", "Clave1234", "600111222")))

        val leidos = LectorObjetos<Dueno>(fichero("duenos.dat")).lee()
        assertEquals(1, leidos.size)
        assertEquals("Ana", leidos[0].nombre)
    }

    @Test
    fun noQuedaFicheroTemporal() {
        EscritorObjetos<Dueno>(fichero("duenos.dat"))
            .escribe(listOf(Dueno(1, "Alvaro", "alvaro@huellas.es", "Huellas2026", "926123456")))
        assertFalse(fichero("duenos.dat.tmp").exists())
    }

    @Test(expected = IOException::class)
    fun ficheroDanadoLanzaExcepcion() {
        val roto = fichero("roto.dat")
        roto.writeBytes(byteArrayOf(1, 2, 3, 4))
        LectorObjetos<Dueno>(roto).lee()
    }

    @Test
    fun elRepositorioRecuperaLosDatosAlVolverAAbrirse() {
        val repo = DuenoRepository(carpeta.root)
        assertEquals(1, repo.siguienteId())
        repo.guardar(Dueno(1, "Alvaro", "alvaro@huellas.es", "Huellas2026", "926123456"))

        val otro = DuenoRepository(carpeta.root)
        assertEquals(1, otro.listar().size)
        assertEquals(2, otro.siguienteId())
        assertEquals("Alvaro", otro.buscarPorEmail("ALVARO@HUELLAS.ES")?.nombre)
        assertNull(otro.buscarPorEmail("nadie@huellas.es"))
    }

    @Test
    fun elRepositorioDeAnimalesFiltraPorDueno() {
        val repo = AnimalRepository(carpeta.root)
        repo.guardar(Animal(1, "Toby", "Perro", "Mestizo", 3, 1))
        repo.guardar(Animal(2, "Luna", "Gato", "", 2, 2))
        repo.guardar(Animal(3, "Rex", "Perro", "", 5, 1))

        assertEquals(2, repo.listarPorDueno(1).size)
        assertEquals(1, repo.listarPorDueno(2).size)
        assertTrue(repo.listarPorDueno(99).isEmpty())
    }

    @Test
    fun siFallaElDiscoNoSeQuedaElDatoEnMemoria() {
        val repo = DuenoRepository(carpeta.root)
        // Un directorio con el nombre del temporal impide escribirlo.
        fichero("duenos.dat.tmp").mkdirs()

        try {
            repo.guardar(Dueno(1, "Alvaro", "alvaro@huellas.es", "Huellas2026", "926123456"))
            throw AssertionError("deberia haber fallado el guardado")
        } catch (esperado: IOException) {
            // Correcto: el guardado falla.
        }
        assertTrue(repo.listar().isEmpty())
        assertEquals(1, repo.siguienteId())
    }
}

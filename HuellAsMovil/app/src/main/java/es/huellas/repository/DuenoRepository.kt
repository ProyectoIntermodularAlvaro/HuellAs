package es.huellas.repository

import es.huellas.model.Dueno
import java.io.File
import java.io.IOException

class DuenoRepository(carpeta: File) {

    private val fichero = File(carpeta, "duenos.dat")
    private val escritor = EscritorObjetos<Dueno>(fichero)
    private val duenos: MutableList<Dueno> = LectorObjetos<Dueno>(fichero).lee()

    fun guardar(dueno: Dueno) {
        duenos.add(dueno)
        try {
            escritor.escribe(duenos)
        } catch (e: IOException) {
            duenos.remove(dueno)
            throw e
        }
    }

    fun buscarPorEmail(email: String?): Dueno? =
        email?.let { correo -> duenos.firstOrNull { it.email.equals(correo.trim(), true) } }

    fun buscarPorId(id: Int): Dueno? = duenos.firstOrNull { it.id == id }

    fun listar(): List<Dueno> = duenos.toList()

    fun siguienteId(): Int = (duenos.maxOfOrNull { it.id } ?: 0) + 1
}

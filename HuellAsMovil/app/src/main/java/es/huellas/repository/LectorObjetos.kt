package es.huellas.repository

import java.io.EOFException
import java.io.File
import java.io.IOException
import java.io.ObjectInputStream
import java.io.Serializable

class LectorObjetos<E : Serializable>(private val fichero: File) {

    fun existe() = fichero.isFile

    @Suppress("UNCHECKED_CAST")
    fun lee(): MutableList<E> {
        val lista = mutableListOf<E>()
        if (!existe()) return lista
        try {
            ObjectInputStream(fichero.inputStream()).use { entrada ->
                while (true) {
                    lista.add(entrada.readObject() as E)
                }
            }
        } catch (fin: EOFException) {
            // Fin del fichero: ya se han leído todos los objetos.
            return lista
        } catch (e: ClassNotFoundException) {
            throw IOException("Fichero no reconocido: ${fichero.name}", e)
        }
        return lista
    }
}

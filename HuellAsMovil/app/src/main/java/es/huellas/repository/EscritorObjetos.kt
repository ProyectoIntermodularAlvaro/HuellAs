package es.huellas.repository

import java.io.File
import java.io.ObjectOutputStream
import java.io.Serializable

class EscritorObjetos<E : Serializable>(private val fichero: File) {

    fun escribe(lista: List<E>) {
        fichero.parentFile?.mkdirs()

        // Se escribe en un temporal y se renombra al final, para no dejar el fichero
        // a medias si la aplicación se cierra durante el guardado.
        val temporal = File(fichero.absolutePath + ".tmp")
        ObjectOutputStream(temporal.outputStream()).use { salida ->
            lista.forEach { salida.writeObject(it) }
        }
        if (!temporal.renameTo(fichero)) {
            fichero.delete()
            if (!temporal.renameTo(fichero)) {
                temporal.delete()
                throw java.io.IOException("No se pudo guardar ${fichero.name}")
            }
        }
    }
}

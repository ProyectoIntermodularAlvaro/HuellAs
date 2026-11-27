package es.huellas.repository

import es.huellas.model.Animal
import java.io.File
import java.io.IOException

class AnimalRepository(carpeta: File) {

    private val fichero = File(carpeta, "animales.dat")
    private val escritor = EscritorObjetos<Animal>(fichero)
    private val animales: MutableList<Animal> = LectorObjetos<Animal>(fichero).lee()

    fun guardar(animal: Animal) {
        animales.add(animal)
        try {
            escritor.escribe(animales)
        } catch (e: IOException) {
            animales.remove(animal)
            throw e
        }
    }

    fun listarPorDueno(idDueno: Int): List<Animal> = animales.filter { it.idDueno == idDueno }

    fun listar(): List<Animal> = animales.toList()

    fun siguienteId(): Int = (animales.maxOfOrNull { it.id } ?: 0) + 1
}

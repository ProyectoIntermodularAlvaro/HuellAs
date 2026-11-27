package es.huellas.model

import java.io.Serializable

class Animal(
    var id: Int = 0,
    var nombre: String = "",
    var especie: String = "",
    var raza: String = "",
    var edad: Int = 0,
    var idDueno: Int = 0,
    var estado: EstadoAnimal = EstadoAnimal.EN_CASA
) : Serializable {

    override fun toString() = "$nombre - $especie ($edad años)"

    companion object {
        private const val serialVersionUID: Long = 1L
    }
}

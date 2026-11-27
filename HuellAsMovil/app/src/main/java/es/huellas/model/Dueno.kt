package es.huellas.model

class Dueno(
    id: Int = 0,
    nombre: String = "",
    email: String = "",
    password: String? = null,
    telefono: String = ""
) : Usuario(id, nombre, email, password, telefono) {

    fun registrarMascota(idAnimal: Int, nombre: String?, especie: String?, raza: String?, edad: Int): Animal {
        require(!nombre.isNullOrBlank()) { "El nombre del animal es obligatorio" }
        require(!especie.isNullOrBlank()) { "La especie del animal es obligatoria" }
        require(edad in 0..EDAD_MAXIMA) { "La edad debe estar entre 0 y $EDAD_MAXIMA años" }
        return Animal(idAnimal, nombre.trim(), especie.trim(), raza?.trim() ?: "", edad, this.id)
    }

    companion object {
        const val EDAD_MAXIMA = 40
        private const val serialVersionUID: Long = 1L
    }
}

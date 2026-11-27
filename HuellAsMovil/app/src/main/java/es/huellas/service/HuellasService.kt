package es.huellas.service

import es.huellas.model.Animal
import es.huellas.model.Dueno
import es.huellas.repository.AnimalRepository
import es.huellas.repository.DuenoRepository

class HuellasService(
    private val duenos: DuenoRepository,
    private val animales: AnimalRepository
) {

    // Los metodos son synchronized porque el servidor atiende a varias conexiones a la vez
    // y todas trabajan sobre los mismos repositorios.

    @Synchronized
    fun registrar(nombre: String?, email: String?, password: String?, telefono: String?): Dueno {
        require(!nombre.isNullOrBlank()) { "El nombre es obligatorio" }
        require(email != null && EMAIL.matches(email.trim())) { "El email no es válido" }
        require(password != null && password.length >= 8) {
            "La contraseña debe tener al menos 8 caracteres"
        }
        val tel = telefono?.replace(" ", "") ?: ""
        require(TELEFONO.matches(tel)) { "El teléfono debe tener entre 9 y 15 dígitos" }
        require(duenos.buscarPorEmail(email) == null) { "Ya existe un usuario con ese email" }

        val dueno = Dueno(duenos.siguienteId(), nombre.trim(), email.trim().lowercase(), password, tel)
        duenos.guardar(dueno)
        return dueno
    }

    @Synchronized
    fun login(email: String?, password: String?): Dueno {
        val dueno = duenos.buscarPorEmail(email)
        require(dueno != null && dueno.login(email, password)) { "Email o contraseña incorrectos" }
        return dueno
    }

    @Synchronized
    fun registrarMascota(idDueno: Int, nombre: String?, especie: String?, raza: String?, edad: Int): Animal {
        val dueno = duenos.buscarPorId(idDueno)
        requireNotNull(dueno) { "El dueño no existe" }
        val animal = dueno.registrarMascota(animales.siguienteId(), nombre, especie, raza, edad)
        animales.guardar(animal)
        return animal
    }

    @Synchronized
    fun listarMascotas(idDueno: Int): List<Animal> = animales.listarPorDueno(idDueno)

    private companion object {
        val EMAIL = Regex("^[\\w.%+-]+@[\\w.-]+\\.[A-Za-z]{2,}$")
        val TELEFONO = Regex("^\\d{9,15}$")
    }
}

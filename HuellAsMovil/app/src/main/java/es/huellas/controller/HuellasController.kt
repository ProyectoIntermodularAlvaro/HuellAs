package es.huellas.controller

import es.huellas.model.Animal
import es.huellas.model.Dueno
import es.huellas.net.ConexionServidor
import es.huellas.net.MensajeRequest
import es.huellas.net.MensajeResponse
import java.io.IOException

/** Une la interfaz con el servidor: traduce las acciones del usuario en peticiones. */
class HuellasController(private val conexion: ConexionServidor) {

    var usuario: Dueno? = null
        private set

    fun registrar(nombre: String, email: String, password: String, telefono: String): MensajeResponse =
        conexion.enviar(
            MensajeRequest(MensajeRequest.REGISTRAR)
                .con("nombre", nombre)
                .con("email", email)
                .con("password", password)
                .con("telefono", telefono)
        )

    fun login(email: String, password: String): MensajeResponse {
        val respuesta = conexion.enviar(
            MensajeRequest(MensajeRequest.LOGIN).con("email", email).con("password", password)
        )
        if (respuesta.correcto) usuario = respuesta.datos as? Dueno
        return respuesta
    }

    fun logout() {
        conexion.enviar(MensajeRequest(MensajeRequest.LOGOUT))
        usuario = null
    }

    fun nuevaMascota(nombre: String, especie: String, raza: String, edad: Int): MensajeResponse =
        conexion.enviar(
            MensajeRequest(MensajeRequest.NUEVA_MASCOTA)
                .con("nombre", nombre)
                .con("especie", especie)
                .con("raza", raza)
                .con("edad", edad)
        )

    @Suppress("UNCHECKED_CAST")
    fun listarMascotas(): List<Animal> {
        val respuesta = conexion.enviar(MensajeRequest(MensajeRequest.LISTAR_MASCOTAS))
        if (!respuesta.correcto) throw IOException(respuesta.mensaje)
        return (respuesta.datos as? List<Animal>) ?: emptyList()
    }

    fun cerrar() = conexion.close()
}

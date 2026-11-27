package es.huellas.net

import es.huellas.model.Dueno
import es.huellas.service.HuellasService
import java.io.EOFException
import java.io.IOException
import java.io.ObjectInputStream
import java.io.ObjectOutputStream
import java.net.Socket

internal class ManejadorCliente(
    private val socket: Socket,
    private val servicio: HuellasService
) : Runnable {

    private var sesion: Dueno? = null

    override fun run() {
        try {
            socket.use { s ->
                val salida = ObjectOutputStream(s.getOutputStream()).apply { flush() }
                val entrada = ObjectInputStream(s.getInputStream())
                while (true) {
                    val peticion = entrada.readObject() as MensajeRequest
                    salida.writeObject(procesar(peticion))
                    salida.reset()
                    salida.flush()
                }
            }
        } catch (fin: EOFException) {
            // El cliente ha cerrado la conexión.
        } catch (e: IOException) {
            println("Conexión terminada: ${e.message}")
        } catch (e: ClassNotFoundException) {
            println("Petición no reconocida: ${e.message}")
        }
    }

    internal fun procesar(peticion: MensajeRequest): MensajeResponse = try {
        when (peticion.accion) {
            MensajeRequest.REGISTRAR -> {
                servicio.registrar(
                    peticion.texto("nombre"), peticion.texto("email"),
                    peticion.texto("password"), peticion.texto("telefono")
                )
                MensajeResponse.ok("Cuenta creada correctamente")
            }

            MensajeRequest.LOGIN -> {
                val dueno = servicio.login(peticion.texto("email"), peticion.texto("password"))
                sesion = dueno
                MensajeResponse.ok("Bienvenido", sinPassword(dueno))
            }

            MensajeRequest.LOGOUT -> {
                sesion = null
                MensajeResponse.ok("Sesión cerrada")
            }

            MensajeRequest.NUEVA_MASCOTA -> {
                val dueno = exigirSesion()
                servicio.registrarMascota(
                    dueno.id, peticion.texto("nombre"), peticion.texto("especie"),
                    peticion.texto("raza"), peticion.entero("edad")
                )
                MensajeResponse.ok("Mascota registrada")
            }

            MensajeRequest.LISTAR_MASCOTAS -> {
                val dueno = exigirSesion()
                MensajeResponse.ok("OK", ArrayList(servicio.listarMascotas(dueno.id)))
            }

            else -> MensajeResponse.error("Acción desconocida")
        }
    } catch (e: IllegalArgumentException) {
        MensajeResponse.error(e.message ?: "Datos no válidos")
    } catch (e: IOException) {
        MensajeResponse.error("No se pudieron guardar los datos")
    }

    private fun exigirSesion(): Dueno =
        requireNotNull(sesion) { "Debes iniciar sesión" }

    // La contraseña no sale del servidor.
    private fun sinPassword(dueno: Dueno) =
        Dueno(dueno.id, dueno.nombre, dueno.email, null, dueno.telefono)
}

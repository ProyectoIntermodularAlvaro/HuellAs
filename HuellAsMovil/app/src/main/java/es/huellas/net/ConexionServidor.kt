package es.huellas.net

import java.io.Closeable
import java.io.IOException
import java.io.ObjectInputStream
import java.io.ObjectOutputStream
import java.net.Socket

class ConexionServidor(host: String, puerto: Int) : Closeable {

    private val socket = Socket(host, puerto)

    // El flujo de salida se crea y se vacia primero: si los dos extremos crearan antes
    // el de entrada, ambos quedarian esperando la cabecera del otro.
    private val salida = ObjectOutputStream(socket.getOutputStream()).apply { flush() }
    private val entrada = ObjectInputStream(socket.getInputStream())

    @Synchronized
    fun enviar(peticion: MensajeRequest): MensajeResponse {
        salida.writeObject(peticion)
        salida.reset()
        salida.flush()
        return entrada.readObject() as? MensajeResponse
            ?: throw IOException("Respuesta no válida del servidor")
    }

    override fun close() = socket.close()

    companion object {
        const val HOST = "127.0.0.1"
        const val PUERTO = 5000
    }
}

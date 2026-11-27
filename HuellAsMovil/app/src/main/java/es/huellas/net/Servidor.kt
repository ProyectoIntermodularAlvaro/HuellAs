package es.huellas.net

import es.huellas.service.HuellasService
import java.io.Closeable
import java.io.IOException
import java.net.ServerSocket
import java.net.Socket
import java.util.concurrent.Executors

class Servidor(private val servicio: HuellasService) {

    private val hilos = Executors.newCachedThreadPool()
    private val clientes = mutableListOf<Socket>()
    private lateinit var serverSocket: ServerSocket

    val puerto: Int get() = serverSocket.localPort

    fun iniciar(puerto: Int) {
        serverSocket = ServerSocket(puerto)
        Thread(::aceptar, "servidor-huellas").start()
    }

    private fun aceptar() {
        while (!serverSocket.isClosed) {
            try {
                val cliente = serverSocket.accept()
                synchronized(clientes) { clientes.add(cliente) }
                hilos.execute(ManejadorCliente(cliente, servicio))
            } catch (e: IOException) {
                if (!serverSocket.isClosed) {
                    println("Error al aceptar un cliente: ${e.message}")
                }
            }
        }
    }

    fun detener() {
        cerrar(serverSocket)
        synchronized(clientes) {
            // Hay que cerrar los sockets: un hilo bloqueado leyendo no atiende a la interrupción.
            clientes.forEach { cerrar(it) }
            clientes.clear()
        }
        hilos.shutdownNow()
    }

    private fun cerrar(recurso: Closeable) {
        try {
            recurso.close()
        } catch (e: IOException) {
            println("Error al cerrar: ${e.message}")
        }
    }
}

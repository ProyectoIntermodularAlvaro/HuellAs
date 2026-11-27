package es.huellas

import android.app.Application
import es.huellas.net.Servidor
import es.huellas.repository.AnimalRepository
import es.huellas.repository.DuenoRepository
import es.huellas.service.HuellasService

/**
 * Arranca el servidor al iniciarse la aplicación, en un puerto libre del propio dispositivo.
 * La interfaz se conecta a él por sockets, de modo que la arquitectura cliente-servidor es real
 * aunque todo se ejecute en el mismo móvil.
 */
class HuellasApplication : Application() {

    var puerto: Int = 0
        private set

    private lateinit var servidor: Servidor

    override fun onCreate() {
        super.onCreate()
        val carpeta = filesDir
        val servicio = HuellasService(DuenoRepository(carpeta), AnimalRepository(carpeta))
        servidor = Servidor(servicio)
        servidor.iniciar(0)
        puerto = servidor.puerto
    }

    override fun onTerminate() {
        super.onTerminate()
        servidor.detener()
    }
}

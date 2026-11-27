package es.huellas.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import es.huellas.HuellasApplication
import es.huellas.controller.HuellasController
import es.huellas.net.ConexionServidor
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class HuellasViewModel(application: Application) : AndroidViewModel(application) {

    private val _estado = MutableStateFlow(EstadoUi())
    val estado: StateFlow<EstadoUi> = _estado.asStateFlow()

    private var controlador: HuellasController? = null

    /**
     * La conexión se abre la primera vez que se necesita, siempre fuera del hilo principal:
     * Android no permite operaciones de red en el hilo que dibuja la interfaz.
     */
    private suspend fun controlador(): HuellasController = withContext(Dispatchers.IO) {
        controlador ?: HuellasController(
            ConexionServidor(ConexionServidor.HOST, (getApplication() as HuellasApplication).puerto)
        ).also { controlador = it }
    }

    fun irA(pantalla: Pantalla) {
        _estado.value = _estado.value.copy(pantalla = pantalla, aviso = null)
    }

    fun entrar(email: String, password: String) {
        if (email.isBlank() || password.isBlank()) {
            mostrarAviso("Rellena el email y la contraseña")
            return
        }
        lanzar {
            val respuesta = withContext(Dispatchers.IO) { controlador().login(email, password) }
            if (respuesta.correcto) {
                _estado.value = _estado.value.copy(
                    pantalla = Pantalla.MASCOTAS,
                    nombreUsuario = controlador?.usuario?.nombre.orEmpty(),
                    aviso = null
                )
                recargar()
            } else {
                mostrarAviso(respuesta.mensaje)
            }
        }
    }

    fun registrar(nombre: String, email: String, password: String, telefono: String) {
        if (nombre.isBlank() || email.isBlank() || password.isBlank() || telefono.isBlank()) {
            mostrarAviso("Rellena todos los campos")
            return
        }
        lanzar {
            val respuesta = withContext(Dispatchers.IO) {
                controlador().registrar(nombre, email, password, telefono)
            }
            if (respuesta.correcto) {
                _estado.value = _estado.value.copy(
                    pantalla = Pantalla.ACCESO,
                    emailRecordado = email,
                    aviso = "Cuenta creada, ya puedes entrar"
                )
            } else {
                mostrarAviso(respuesta.mensaje)
            }
        }
    }

    fun nuevaMascota(nombre: String, especie: String, raza: String, edad: String) {
        val anios = edad.trim().toIntOrNull()
        if (anios == null) {
            mostrarAviso("La edad debe ser un número")
            return
        }
        lanzar {
            val respuesta = withContext(Dispatchers.IO) {
                controlador().nuevaMascota(nombre, especie, raza, anios)
            }
            if (respuesta.correcto) recargar() else mostrarAviso(respuesta.mensaje)
        }
    }

    fun recargar() {
        lanzar {
            val mascotas = withContext(Dispatchers.IO) { controlador().listarMascotas() }
            _estado.value = _estado.value.copy(mascotas = mascotas, aviso = null)
        }
    }

    fun cerrarSesion() {
        lanzar {
            withContext(Dispatchers.IO) { controlador().logout() }
            _estado.value = EstadoUi(emailRecordado = _estado.value.emailRecordado)
        }
    }

    fun avisoMostrado() {
        _estado.value = _estado.value.copy(aviso = null)
    }

    private fun mostrarAviso(texto: String) {
        _estado.value = _estado.value.copy(aviso = texto, cargando = false)
    }

    private fun lanzar(accion: suspend () -> Unit) {
        viewModelScope.launch {
            _estado.value = _estado.value.copy(cargando = true)
            try {
                accion()
            } catch (e: Exception) {
                mostrarAviso(e.message ?: "No se pudo contactar con el servidor")
            } finally {
                _estado.value = _estado.value.copy(cargando = false)
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        runCatching { controlador?.cerrar() }
    }
}

package es.huellas.net

import java.io.Serializable

class MensajeRequest(
    var accion: String = "",
    var parametros: MutableMap<String, Any?> = mutableMapOf()
) : Serializable {

    fun con(nombre: String, valor: Any?): MensajeRequest {
        parametros[nombre] = valor
        return this
    }

    fun texto(nombre: String): String? = parametros[nombre]?.toString()

    fun entero(nombre: String): Int = (parametros[nombre] as? Int) ?: -1

    companion object {
        const val REGISTRAR = "REGISTRAR"
        const val LOGIN = "LOGIN"
        const val LOGOUT = "LOGOUT"
        const val NUEVA_MASCOTA = "NUEVA_MASCOTA"
        const val LISTAR_MASCOTAS = "LISTAR_MASCOTAS"
        private const val serialVersionUID: Long = 1L
    }
}

class MensajeResponse(
    var correcto: Boolean = false,
    var mensaje: String = "",
    var datos: Any? = null
) : Serializable {

    companion object {
        fun ok(mensaje: String, datos: Any? = null) = MensajeResponse(true, mensaje, datos)
        fun error(mensaje: String) = MensajeResponse(false, mensaje, null)
        private const val serialVersionUID: Long = 1L
    }
}

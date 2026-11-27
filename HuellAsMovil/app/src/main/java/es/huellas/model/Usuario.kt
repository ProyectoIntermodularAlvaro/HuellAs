package es.huellas.model

import java.io.Serializable

open class Usuario(
    var id: Int = 0,
    var nombre: String = "",
    var email: String = "",
    var password: String? = null,
    var telefono: String = ""
) : Serializable {

    fun login(email: String?, password: String?): Boolean {
        if (email == null || password == null) return false
        return this.email.equals(email.trim(), ignoreCase = true) && this.password == password
    }

    override fun toString() = "$nombre ($email)"

    companion object {
        private const val serialVersionUID: Long = 1L
    }
}

package es.huellas.viewmodel

import es.huellas.model.Animal

enum class Pantalla { ACCESO, REGISTRO, MASCOTAS }

data class EstadoUi(
    val pantalla: Pantalla = Pantalla.ACCESO,
    val cargando: Boolean = false,
    val aviso: String? = null,
    val nombreUsuario: String = "",
    val emailRecordado: String = "",
    val mascotas: List<Animal> = emptyList()
)

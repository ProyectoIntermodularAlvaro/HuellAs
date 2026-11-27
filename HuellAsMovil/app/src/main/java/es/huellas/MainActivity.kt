package es.huellas

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import es.huellas.ui.HuellAsTema
import es.huellas.ui.PantallaAcceso
import es.huellas.ui.PantallaMascotas
import es.huellas.ui.PantallaRegistro
import es.huellas.viewmodel.HuellasViewModel
import es.huellas.viewmodel.Pantalla

class MainActivity : ComponentActivity() {

    private val modelo: HuellasViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            HuellAsTema {
                AppHuellAs(modelo)
            }
        }
    }
}

@Composable
private fun AppHuellAs(modelo: HuellasViewModel) {
    val estado by modelo.estado.collectAsStateWithLifecycle()
    val avisos = remember { SnackbarHostState() }

    LaunchedEffect(estado.aviso) {
        estado.aviso?.let { texto ->
            avisos.showSnackbar(texto)
            modelo.avisoMostrado()
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        when (estado.pantalla) {
            Pantalla.ACCESO -> PantallaAcceso(
                emailInicial = estado.emailRecordado,
                cargando = estado.cargando,
                onEntrar = modelo::entrar,
                onIrARegistro = { modelo.irA(Pantalla.REGISTRO) }
            )

            Pantalla.REGISTRO -> PantallaRegistro(
                cargando = estado.cargando,
                onRegistrar = modelo::registrar,
                onVolver = { modelo.irA(Pantalla.ACCESO) }
            )

            Pantalla.MASCOTAS -> PantallaMascotas(
                nombreUsuario = estado.nombreUsuario,
                mascotas = estado.mascotas,
                onNuevaMascota = modelo::nuevaMascota,
                onCerrarSesion = modelo::cerrarSesion
            )
        }

        if (estado.cargando) {
            CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
        }

        SnackbarHost(hostState = avisos, modifier = Modifier.align(Alignment.BottomCenter))
    }
}

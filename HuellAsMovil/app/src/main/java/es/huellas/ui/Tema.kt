package es.huellas.ui

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val Morado = Color(0xFF4A4FBF)

private val ColoresClaro = lightColorScheme(primary = Morado)
private val ColoresOscuro = darkColorScheme(primary = Color(0xFF9FA3F0))

@Composable
fun HuellAsTema(oscuro: Boolean = isSystemInDarkTheme(), contenido: @Composable () -> Unit) {
    val contexto = LocalContext.current
    val colores = when {
        Build.VERSION.SDK_INT >= Build.VERSION_CODES.S ->
            if (oscuro) dynamicDarkColorScheme(contexto) else dynamicLightColorScheme(contexto)
        oscuro -> ColoresOscuro
        else -> ColoresClaro
    }
    MaterialTheme(colorScheme = colores, content = contenido)
}

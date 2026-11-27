package es.huellas.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import es.huellas.R
import es.huellas.model.Animal

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaMascotas(
    nombreUsuario: String,
    mascotas: List<Animal>,
    onNuevaMascota: (String, String, String, String) -> Unit,
    onCerrarSesion: () -> Unit,
    modifier: Modifier = Modifier
) {
    var mostrarDialogo by remember { mutableStateOf(false) }

    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.mis_mascotas)) },
                actions = {
                    TextButton(onClick = onCerrarSesion) {
                        Text(stringResource(R.string.cerrar_sesion))
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { mostrarDialogo = true }) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = stringResource(R.string.registrar_mascota)
                )
            }
        }
    ) { relleno ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(relleno)
                .padding(horizontal = 16.dp)
        ) {
            Text(
                text = stringResource(R.string.sesion_de, nombreUsuario),
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(vertical = 8.dp)
            )

            if (mascotas.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(
                        text = stringResource(R.string.sin_mascotas),
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            } else {
                LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(mascotas, key = { it.id }) { animal -> FilaMascota(animal) }
                }
            }
        }
    }

    if (mostrarDialogo) {
        DialogoNuevaMascota(
            onAceptar = { nombre, especie, raza, edad ->
                mostrarDialogo = false
                onNuevaMascota(nombre, especie, raza, edad)
            },
            onCancelar = { mostrarDialogo = false }
        )
    }
}

@Composable
private fun FilaMascota(animal: Animal, modifier: Modifier = Modifier) {
    Card(modifier = modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = animal.nombre, style = MaterialTheme.typography.titleMedium)
            val raza = if (animal.raza.isBlank()) "" else " · ${animal.raza}"
            val unidad = stringResource(if (animal.edad == 1) R.string.anio else R.string.anios)
            Text(
                text = "${animal.especie}$raza · ${animal.edad} $unidad",
                style = MaterialTheme.typography.bodyMedium
            )
            Text(
                text = animal.estado.descripcion,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}

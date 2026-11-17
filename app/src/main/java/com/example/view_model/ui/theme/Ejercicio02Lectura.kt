package com.example.view_model.ui.theme

import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlin.math.roundToInt

enum class Interlineado(val etiqueta: String, val factor: Float) {
    COMPACTO("Compacto", 1.0f),
    NORMAL("Normal", 1.3f),
    AMPLIO("Amplio", 1.6f)
}

data class AjustesLectura(
    val tamano: Float = 18f,
    val interlineado: Interlineado = Interlineado.NORMAL,
    val modoOscuro: Boolean = false,
    val usaSerif: Boolean = false
)

class LecturaViewModel : ViewModel() {

    var ajustes by mutableStateOf(AjustesLectura())
        private set

    fun cambiarTamano(valor: Float) { ajustes = ajustes.copy(tamano = valor) }
    fun cambiarInterlineado(valor: Interlineado) { ajustes = ajustes.copy(interlineado = valor) }
    fun cambiarModoOscuro(valor: Boolean) { ajustes = ajustes.copy(modoOscuro = valor) }
    fun cambiarSerif(valor: Boolean) { ajustes = ajustes.copy(usaSerif = valor) }
    fun restablecer() { ajustes = AjustesLectura() }
}

@Composable
fun Ejercicio02Screen(vm: LecturaViewModel = viewModel()) {
    LecturaContent(
        ajustes = vm.ajustes,
        onCambiarTamano = vm::cambiarTamano,
        onCambiarInterlineado = vm::cambiarInterlineado,
        onCambiarModoOscuro = vm::cambiarModoOscuro,
        onCambiarSerif = vm::cambiarSerif,
        onRestablecer = vm::restablecer
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LecturaContent(
    ajustes: AjustesLectura,
    onCambiarTamano: (Float) -> Unit,
    onCambiarInterlineado: (Interlineado) -> Unit,
    onCambiarModoOscuro: (Boolean) -> Unit,
    onCambiarSerif: (Boolean) -> Unit,
    onRestablecer: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Ajustes de lectura") },
                navigationIcon = {
                    IconButton(onClick = { }) { Icon(Icons.AutoMirrored.Filled.ArrowBack, "Atrás") }
                }
            )
        }
    ) { padding ->
        Column(
            Modifier.padding(padding).padding(horizontal = 20.dp).fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("Tamaño de letra")
                Text("${ajustes.tamano.roundToInt()} sp", color = MaterialTheme.colorScheme.primary)
            }
            Slider(value = ajustes.tamano, onValueChange = onCambiarTamano, valueRange = 14f..32f)

            Text("Interlineado")
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Interlineado.entries.forEach { opcion ->
                    Row(verticalAlignment = androidx.compose.ui.Alignment.CenterVertically) {
                        RadioButton(selected = ajustes.interlineado == opcion, onClick = { onCambiarInterlineado(opcion) })
                        Text(opcion.etiqueta, fontSize = 14.sp)
                    }
                }
            }

            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = androidx.compose.ui.Alignment.CenterVertically) {
                Text("Modo oscuro")
                Switch(checked = ajustes.modoOscuro, onCheckedChange = onCambiarModoOscuro)
            }

            Row(verticalAlignment = androidx.compose.ui.Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("Fuente", modifier = Modifier.weight(1f))
                FilterChip(
                    selected = ajustes.usaSerif, onClick = { onCambiarSerif(true) }, label = { Text("Serif") },
                    leadingIcon = if (ajustes.usaSerif) ({ Icon(Icons.Default.Check, null, Modifier.size(16.dp)) }) else null
                )
                FilterChip(
                    selected = !ajustes.usaSerif, onClick = { onCambiarSerif(false) }, label = { Text("Sans") },
                    leadingIcon = if (!ajustes.usaSerif) ({ Icon(Icons.Default.Check, null, Modifier.size(16.dp)) }) else null
                )
            }
            HorizontalDivider()

            Text("VISTA PREVIA", fontSize = 12.sp)
            VistaPrevia(
                texto = "Era una noche tranquila en el pueblo cuando las campanas comenzaron a sonar, y nadie supo por qué.",
                ajustes = ajustes,
                modifier = Modifier.weight(1f).fillMaxWidth()
            )

            OutlinedButton(onClick = onRestablecer, modifier = Modifier.fillMaxWidth()) { Text("Restablecer") }
            Spacer(Modifier.height(8.dp))
        }
    }
}

@Composable
fun VistaPrevia(texto: String, ajustes: AjustesLectura, modifier: Modifier = Modifier) {
    val fondo = if (ajustes.modoOscuro) Color(0xFF1C1B1F) else Color(0xFFFFFBFE)
    val letra = if (ajustes.modoOscuro) Color(0xFFE6E1E5) else Color(0xFF1C1B1F)
    Box(modifier.clip(RoundedCornerShape(16.dp)).background(fondo).padding(16.dp)) {
        Text(
            text = texto,
            color = letra,
            fontSize = ajustes.tamano.sp,
            lineHeight = (ajustes.tamano * ajustes.interlineado.factor).sp, // <-- ¡Aquí se agregó el operador * faltante!
            fontFamily = if (ajustes.usaSerif) FontFamily.Serif else FontFamily.SansSerif
        )
    }
}

@Preview(showBackground = true, showSystemUi = true, name = "Pantalla completa")
@Composable
private fun PreviewLecturaScreen() {
    MaterialTheme {
        LecturaContent(
            ajustes = AjustesLectura(),
            onCambiarTamano = {},
            onCambiarInterlineado = {},
            onCambiarModoOscuro = {},
            onCambiarSerif = {},
            onRestablecer = {}
        )
    }
}

@Preview(showBackground = true, name = "Vista previa oscura serif")
@Composable
private fun PreviewVistaPreviaOscura() {
    MaterialTheme {
        VistaPrevia(
            texto = "Era una noche tranquila en el pueblo cuando las campanas comenzaron a sonar.",
            ajustes = AjustesLectura(tamano = 22f, interlineado = Interlineado.AMPLIO, modoOscuro = true, usaSerif = true),
            modifier = Modifier.height(200.dp).fillMaxWidth()
        )
    }
}
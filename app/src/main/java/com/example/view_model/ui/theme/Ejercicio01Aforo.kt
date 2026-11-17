package com.example.view_model

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.view_model.ui.theme.ViewmodelTheme
import kotlin.math.roundToInt

const val AFORO_MAX = 50

class AforoViewModel : ViewModel() {

    var actuales by mutableIntStateOf(0)
        private set
    var visitasDelDia by mutableIntStateOf(0)
        private set

    val porcentaje: Float get() = actuales / AFORO_MAX.toFloat()
    val llena: Boolean get() = actuales >= AFORO_MAX

    fun entra() {
        if (!llena) { actuales++; visitasDelDia++ }
    }

    fun sale() {
        if (actuales > 0) actuales--
    }

    fun entraGrupo() {
        val nuevo = (actuales + 5).coerceAtMost(AFORO_MAX)
        visitasDelDia += nuevo - actuales
        actuales = nuevo
    }

    fun cerrarDia() {
        actuales = 0
        visitasDelDia = 0
    }
}

@Composable
fun Ejercicio01Screen(vm: AforoViewModel = viewModel()) {
    AforoContent(
        actuales = vm.actuales,
        visitasDelDia = vm.visitasDelDia,
        porcentaje = vm.porcentaje,
        llena = vm.llena,
        onEntra = vm::entra,
        onSale = vm::sale,
        onEntraGrupo = vm::entraGrupo,
        onCerrarDia = vm::cerrarDia
    )
}

@Composable
fun AforoContent(
    actuales: Int,
    visitasDelDia: Int,
    porcentaje: Float,
    llena: Boolean,
    onEntra: () -> Unit,
    onSale: () -> Unit,
    onEntraGrupo: () -> Unit,
    onCerrarDia: () -> Unit
) {
    val color = when {
        porcentaje >= 1f -> Color(0xFFB3261E)
        porcentaje >= 0.7f -> Color(0xFFF9A825)
        else -> Color(0xFF2E7D32)
    }

    Column(
        Modifier.fillMaxSize().systemBarsPadding().padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text("Biblioteca Central", fontSize = 24.sp)
        Text("Sala de estudio · capacidad $AFORO_MAX", style = MaterialTheme.typography.bodyMedium)

        Card(
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = color.copy(alpha = 0.12f))
        ) {
            Column(
                Modifier.fillMaxWidth().padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text("$actuales / $AFORO_MAX", fontSize = 56.sp, fontWeight = FontWeight.Bold, color = color)
                Text("personas en la sala")
                LinearProgressIndicator(
                    progress = { porcentaje },
                    modifier = Modifier.fillMaxWidth().height(10.dp),
                    color = color
                )
                Text("${(porcentaje * 100).roundToInt()} % de ocupación", color = color, fontWeight = FontWeight.Bold)
                Text(if (llena) "Sala llena" else "Aforo disponible", fontSize = 12.sp)
            }
        }

        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            OutlinedButton(onClick = onSale, enabled = actuales > 0, modifier = Modifier.weight(1f)) {
                Text("−  Sale")
            }
            Button(onClick = onEntra, enabled = !llena, modifier = Modifier.weight(1f)) {
                Text("+  Entra")
            }
        }
        TextButton(onClick = onEntraGrupo, enabled = !llena, modifier = Modifier.align(Alignment.CenterHorizontally)) {
            Text("+5 (grupo)")
        }

        Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)) {
            Row(
                Modifier.fillMaxWidth().padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text("Visitas del día")
                    Text("Solo suma las entradas", fontSize = 12.sp)
                }
                Text("$visitasDelDia", fontSize = 32.sp, fontWeight = FontWeight.Bold)
            }
        }

        Spacer(Modifier.weight(1f))

        OutlinedButton(
            onClick = onCerrarDia,
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFFB3261E))
        ) { Text("Cerrar día") }
    }
}

@Preview(showBackground = true, showSystemUi = true, name = "Vacía")
@Composable
private fun PreviewAforoVacia() {
    ViewmodelTheme {
        AforoContent(0, 0, 0f, false, {}, {}, {}, {})
    }
}

@Preview(showBackground = true, showSystemUi = true, name = "32 / 50 (verde)")
@Composable
private fun PreviewAforoMedia() {
    ViewmodelTheme {
        AforoContent(32, 35, 32f / 50f, false, {}, {}, {}, {})
    }
}

@Preview(showBackground = true, showSystemUi = true, name = "Llena (rojo)")
@Composable
private fun PreviewAforoLlena() {
    ViewmodelTheme {
        AforoContent(50, 55, 1f, true, {}, {}, {}, {})
    }
}
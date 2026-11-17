package com.example.view_model.ui.theme

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel

fun validarNombre(n: String): String? = if (n.trim().length < 3) "Escribe al menos 3 caracteres" else null

fun validarCorreo(c: String): String? {
    val arroba = c.indexOf('@')
    val punto = c.indexOf('.', arroba + 1)
    return if (arroba > 0 && punto > arroba + 1 && !c.endsWith(".")) null else "Ingresa un correo válido"
}

fun validarEdad(e: String): String? {
    val n = e.toIntOrNull()
    return if (n != null && n in 12..99) null else "La edad debe estar entre 12 y 99"
}

class InscripcionViewModel : ViewModel() {

    val talleres = listOf("Pintura", "Fotografía", "Guitarra")

    var nombre by mutableStateOf("")
        private set
    var correo by mutableStateOf("")
        private set
    var edad by mutableStateOf("")
        private set
    var taller by mutableStateOf("Fotografía")
        private set
    var aceptaAviso by mutableStateOf(false)
        private set
    var intentoEnviar by mutableStateOf(false)
        private set
    var inscrito by mutableStateOf(false)
        private set

    val errorNombre: String? get() = if (intentoEnviar) validarNombre(nombre) else null
    val errorCorreo: String? get() = if (intentoEnviar) validarCorreo(correo) else null
    val errorEdad: String? get() = if (intentoEnviar) validarEdad(edad) else null
    val errorAviso: String? get() = if (intentoEnviar && !aceptaAviso) "Debes aceptar el aviso de privacidad" else null

    private val todoValido: Boolean
        get() = validarNombre(nombre) == null && validarCorreo(correo) == null &&
                validarEdad(edad) == null && aceptaAviso

    fun onNombreChange(v: String) { nombre = v }
    fun onCorreoChange(v: String) { correo = v }
    fun onEdadChange(v: String) { edad = v.filter { it.isDigit() }.take(2) }
    fun onTallerChange(v: String) { taller = v }
    fun onAvisoChange(v: Boolean) { aceptaAviso = v }

    fun enviar() {
        intentoEnviar = true
        if (todoValido) inscrito = true
    }

    fun nuevaInscripcion() {
        nombre = ""; correo = ""; edad = ""; taller = talleres[1]
        aceptaAviso = false; intentoEnviar = false; inscrito = false
    }
}

// ───────────── UI Principal conectada al ViewModel ─────────────
@Composable
fun Ejercicio03Screen(vm: InscripcionViewModel = viewModel()) {
    if (vm.inscrito) {
        ConfirmacionScreen(
            nombre = vm.nombre,
            correo = vm.correo,
            taller = vm.taller,
            onNueva = vm::nuevaInscripcion
        )
    } else {
        InscripcionContent(
            nombre = vm.nombre,
            correo = vm.correo,
            edad = vm.edad,
            taller = vm.taller,
            talleres = vm.talleres,
            aceptaAviso = vm.aceptaAviso,
            errorNombre = vm.errorNombre,
            errorCorreo = vm.errorCorreo,
            errorEdad = vm.errorEdad,
            errorAviso = vm.errorAviso,
            onNombreChange = vm::onNombreChange,
            onCorreoChange = vm::onCorreoChange,
            onEdadChange = vm::onEdadChange,
            onTallerChange = vm::onTallerChange,
            onAvisoChange = vm::onAvisoChange,
            onEnviar = vm::enviar
        )
    }
}


@Composable
fun InscripcionContent(nombre: String, correo: String, edad: String,
    taller: String, talleres: List<String>, aceptaAviso: Boolean, errorNombre: String?,
    errorCorreo: String?, errorEdad: String?, errorAviso: String?, onNombreChange: (String) -> Unit,
    onCorreoChange: (String) -> Unit, onEdadChange: (String) -> Unit,
    onTallerChange: (String) -> Unit, onAvisoChange: (Boolean) -> Unit, onEnviar: () -> Unit
) {
    Column(
        Modifier
            .fillMaxSize()
            .systemBarsPadding()
            .padding(20.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text("Inscripción a talleres", fontSize = 24.sp)
        Text("Casa del Lago · cupo limitado", fontSize = 14.sp)

        CampoTexto("Nombre completo", nombre, onNombreChange, errorNombre)
        CampoTexto("Correo", correo, onCorreoChange, errorCorreo, KeyboardType.Email)
        CampoTexto("Edad", edad, onEdadChange, errorEdad, KeyboardType.Number)
        SelectorTaller(taller, talleres, onTallerChange)

        Row(verticalAlignment = Alignment.CenterVertically) {
            Checkbox(checked = aceptaAviso, onCheckedChange = onAvisoChange)
            Text("Acepto el aviso de privacidad")
        }
        errorAviso?.let { Text(it, color = MaterialTheme.colorScheme.error, fontSize = 12.sp) }

        Button(onClick = onEnviar, modifier = Modifier.fillMaxWidth()) { Text("Inscribirme") }
        Text(
            "Quedan 6 lugares en este taller",
            fontSize = 12.sp,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )
    }
}

@Composable
fun CampoTexto(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    error: String?,
    tipoTeclado: KeyboardType = KeyboardType.Text
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        isError = error != null,
        supportingText = { error?.let { Text(it) } },
        keyboardOptions = KeyboardOptions(keyboardType = tipoTeclado),
        singleLine = true,
        modifier = Modifier.fillMaxWidth()
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SelectorTaller(seleccionado: String, opciones: List<String>, onSeleccion: (String) -> Unit) {
    var abierto by remember { mutableStateOf(false) }
    ExposedDropdownMenuBox(expanded = abierto, onExpandedChange = { abierto = it }) {
        OutlinedTextField(
            value = seleccionado,
            onValueChange = {},
            readOnly = true,
            label = { Text("Taller") },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = abierto) },
            modifier = Modifier
                .menuAnchor(MenuAnchorType.PrimaryNotEditable)
                .fillMaxWidth()
        )
        ExposedDropdownMenu(expanded = abierto, onDismissRequest = { abierto = false }) {
            opciones.forEach { opcion ->
                DropdownMenuItem(text = { Text(opcion) },
                    onClick = { onSeleccion(opcion); abierto = false }
                )
            }
        }
    }
}

@Composable
fun ConfirmacionScreen(nombre: String, correo: String, taller: String, onNueva: () -> Unit) {
    Column(
        Modifier
            .fillMaxSize()
            .systemBarsPadding()
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text("¡Inscripción exitosa!", fontSize = 24.sp)
        Card(Modifier.fillMaxWidth()) {
            Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text("Nombre: $nombre")
                Text("Correo: $correo")
                Text("Taller: $taller")
            }
        }
        Button(onClick = onNueva, modifier = Modifier.fillMaxWidth()) { Text("Nueva inscripción") }
    }
}

@Preview(showBackground = true, showSystemUi = true, name = "Formulario vacío")
@Composable
private fun PreviewInscripcionVacio() {
    MaterialTheme {
        InscripcionContent(
            nombre = "",
            correo = "",
            edad = "",
            taller = "Fotografía",
            talleres = listOf("Pintura", "Fotografía", "Guitarra"),
            aceptaAviso = false,
            errorNombre = null,
            errorCorreo = null,
            errorEdad = null,
            errorAviso = null,
            onNombreChange = {},
            onCorreoChange = {},
            onEdadChange = {},
            onTallerChange = {},
            onAvisoChange = {},
            onEnviar = {}
        )
    }
}
@Preview(showBackground = true, name = "Confirmación")
@Composable
private fun PreviewConfirmacion() {
    MaterialTheme { ConfirmacionScreen("Ana López", "ana@correo.com", "Fotografía", onNueva = {}) }
}

@Preview(showBackground = true, name = "Campo con error")
@Composable
private fun PreviewCampoError() {
    MaterialTheme { CampoTexto("Correo", "ana.lopez@", {}, "Ingresa un correo válido") }
}
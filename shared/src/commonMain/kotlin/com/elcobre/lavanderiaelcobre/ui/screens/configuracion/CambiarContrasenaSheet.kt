package com.elcobre.lavanderiaelcobre.ui.screens.configuracion

import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import com.elcobre.lavanderiaelcobre.data.model.Validaciones
import com.elcobre.lavanderiaelcobre.ui.components.CobreButton
import com.elcobre.lavanderiaelcobre.ui.components.CobreIcons
import com.elcobre.lavanderiaelcobre.ui.components.CobreTextField
import com.elcobre.lavanderiaelcobre.ui.components.GlassSheet
import com.elcobre.lavanderiaelcobre.ui.theme.CobreTheme

/** `null` si el cambio de contraseña puede enviarse; si no, el motivo. */
internal fun problemaCambioContrasena(actual: String, nueva: String, confirmacion: String): String? = when {
    actual.isBlank() || nueva.isBlank() || confirmacion.isBlank() -> "Completa los tres campos."
    Validaciones.problemaContrasena(nueva) != null -> Validaciones.problemaContrasena(nueva)
    nueva != confirmacion -> "Las contraseñas no coinciden."
    nueva == actual -> "La nueva contraseña debe ser distinta de la actual."
    else -> null
}

/** Cambio de contraseña con reautenticación, como `CambiarContrasena.tsx` de la web. */
@Composable
internal fun CambiarContrasenaSheet(
    onCambiar: (actual: String, nueva: String, resultado: (String?) -> Unit) -> Unit,
    onCerrar: () -> Unit,
) {
    var actual by remember { mutableStateOf("") }
    var nueva by remember { mutableStateOf("") }
    var confirmacion by remember { mutableStateOf("") }
    var error by remember { mutableStateOf<String?>(null) }
    var guardando by remember { mutableStateOf(false) }
    var listo by remember { mutableStateOf(false) }
    val colors = CobreTheme.colors
    val oculto = PasswordVisualTransformation()
    val teclado = KeyboardOptions(keyboardType = KeyboardType.Password)

    GlassSheet(
        onDismiss = { if (!guardando) onCerrar() },
        title = "Cambiar contraseña",
        subtitle = "Mínimo 8 caracteres, con letras y números.",
    ) {
        if (listo) {
            Text(
                "Tu contraseña se actualizó.",
                color = colors.successText,
                modifier = Modifier.semantics { liveRegion = LiveRegionMode.Polite },
            )
            CobreButton(text = "Listo", onClick = onCerrar, fillWidth = true)
            return@GlassSheet
        }
        CobreTextField(
            value = actual, onValueChange = { actual = it }, label = "Contraseña actual", leadingIcon = CobreIcons.Lock,
            visualTransformation = oculto, keyboardOptions = teclado, enabled = !guardando,
        )
        CobreTextField(
            value = nueva, onValueChange = { nueva = it }, label = "Nueva contraseña", leadingIcon = CobreIcons.Lock,
            visualTransformation = oculto, keyboardOptions = teclado, enabled = !guardando,
            supportingText = nueva.takeIf { it.isNotEmpty() }?.let { Validaciones.problemaContrasena(it) ?: "Contraseña válida" },
        )
        CobreTextField(
            value = confirmacion,
            onValueChange = { confirmacion = it },
            label = "Repite la nueva contraseña",
            leadingIcon = CobreIcons.Lock,
            visualTransformation = oculto, keyboardOptions = teclado, enabled = !guardando,
        )
        error?.let {
            Text(
                it, color = colors.dangerText, style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.semantics { liveRegion = LiveRegionMode.Polite },
            )
        }
        CobreButton(
            text = "Guardar contraseña",
            loading = guardando,
            fillWidth = true,
            onClick = {
                error = problemaCambioContrasena(actual, nueva, confirmacion)
                if (error == null) {
                    guardando = true
                    onCambiar(actual, nueva) { problema ->
                        guardando = false
                        error = problema
                        listo = problema == null
                    }
                }
            },
        )
    }
}

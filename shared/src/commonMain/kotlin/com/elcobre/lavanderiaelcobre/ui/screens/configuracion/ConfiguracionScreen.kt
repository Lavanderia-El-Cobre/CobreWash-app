package com.elcobre.lavanderiaelcobre.ui.screens.configuracion

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.selection.toggleable
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import com.elcobre.lavanderiaelcobre.ui.components.AvisoPrivacidad
import com.elcobre.lavanderiaelcobre.ui.components.ButtonVariant
import com.elcobre.lavanderiaelcobre.ui.components.CobreButton
import com.elcobre.lavanderiaelcobre.ui.components.CobreSwitch
import com.elcobre.lavanderiaelcobre.ui.components.CobreIcons
import com.elcobre.lavanderiaelcobre.ui.components.ContentCard
import com.elcobre.lavanderiaelcobre.ui.components.GlassAlert
import com.elcobre.lavanderiaelcobre.ui.components.GlassSegmentedControl
import com.elcobre.lavanderiaelcobre.ui.components.LargeTitleScreen
import com.elcobre.lavanderiaelcobre.ui.components.SectionLabel
import com.elcobre.lavanderiaelcobre.ui.components.readableWidth
import com.elcobre.lavanderiaelcobre.ui.theme.CobreShapes
import com.elcobre.lavanderiaelcobre.ui.theme.CobreTheme
import com.elcobre.lavanderiaelcobre.ui.theme.ModoTema

/** Ajustes de operario y administración: cuenta, apariencia (Claro/Oscuro/Sistema), accesibilidad y cierre de sesión. */
@Composable
fun ConfiguracionScreen(
    nombre: String,
    rol: String,
    modoTema: ModoTema,
    onCambiarTema: (ModoTema) -> Unit,
    onCerrarSesion: () -> Unit,
    applySystemBarsInsets: Boolean = true,
    reducirTransparencia: Boolean = false,
    onCambiarReducirTransparencia: (Boolean) -> Unit = {},
) {
    var confirmandoSalida by remember { mutableStateOf(false) }
    val haptic = LocalHapticFeedback.current

    LargeTitleScreen(
        title = "Ajustes",
        subtitle = "Tu cuenta, apariencia y sesión.",
        applySystemBarsInsets = applySystemBarsInsets,
    ) {
        item { Seccion("Cuenta") { CuentaCard(nombre, rol) } }
        item { Seccion("Privacidad") { AvisoPrivacidad(mostrarTarjeta = true) } }
        item {
            Seccion("Apariencia") {
                ContentCard {
                    GlassSegmentedControl(
                        options = ModoTema.entries,
                        selected = modoTema,
                        onSelect = {
                            haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                            onCambiarTema(it)
                        },
                        label = { it.etiqueta },
                    )
                    Spacer(Modifier.height(8.dp))
                    Text(modoTema.descripcion, style = MaterialTheme.typography.bodySmall, color = CobreTheme.colors.textSecondary)
                }
            }
        }
        item {
            Seccion("Accesibilidad") {
                ReducirTransparenciaFila(activo = reducirTransparencia, onCambiar = onCambiarReducirTransparencia)
            }
        }
        item {
            CobreButton(
                text = "Cerrar sesión",
                onClick = {
                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                    confirmandoSalida = true
                },
                variant = ButtonVariant.Danger,
                icon = CobreIcons.Logout,
                fillWidth = true,
                modifier = Modifier.readableWidth().padding(top = 8.dp),
            )
        }
    }

    if (confirmandoSalida) {
        GlassAlert(
            title = "¿Cerrar sesión?",
            message = "Deberás volver a ingresar tus datos para acceder de nuevo.",
            confirmText = "Cerrar sesión",
            onConfirm = {
                confirmandoSalida = false
                onCerrarSesion()
            },
            onDismiss = { confirmandoSalida = false },
            destructive = true,
        )
    }
}

/** Grupo con encabezado, como las listas agrupadas de iOS. */
@Composable
private fun Seccion(titulo: String, content: @Composable () -> Unit) {
    Column(modifier = Modifier.readableWidth(), verticalArrangement = Arrangement.spacedBy(8.dp)) {
        SectionLabel(titulo, modifier = Modifier.padding(start = 4.dp))
        content()
    }
}

@Composable
private fun CuentaCard(nombre: String, rol: String) {
    val colors = CobreTheme.colors
    ContentCard {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier.size(48.dp).clip(CobreShapes.full).background(colors.primaryFill),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    nombre.trim().take(1).uppercase().ifEmpty { "?" },
                    style = MaterialTheme.typography.titleLarge,
                    color = colors.onPrimaryFill,
                )
            }
            Spacer(Modifier.width(14.dp))
            Column {
                Text(nombre, style = MaterialTheme.typography.titleMedium, color = colors.textPrimary)
                Text(rol, style = MaterialTheme.typography.bodySmall, color = colors.textSecondary)
            }
        }
    }
}

@Composable
private fun ReducirTransparenciaFila(activo: Boolean, onCambiar: (Boolean) -> Unit) {
    val colors = CobreTheme.colors
    ContentCard(
        modifier = Modifier.toggleable(value = activo, role = Role.Switch, onValueChange = onCambiar),
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Column(modifier = Modifier.weight(1f)) {
                Text("Reducir transparencia", style = MaterialTheme.typography.titleSmall, color = colors.textPrimary)
                Text(
                    "Barras y botones opacos en vez de vidrio. Mejora la lectura y el rendimiento.",
                    style = MaterialTheme.typography.bodySmall,
                    color = colors.textSecondary,
                )
            }
            Spacer(Modifier.width(12.dp))
            CobreSwitch(checked = activo)
        }
    }
}

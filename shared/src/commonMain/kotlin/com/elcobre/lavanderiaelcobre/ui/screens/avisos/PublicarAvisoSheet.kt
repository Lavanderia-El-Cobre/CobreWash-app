package com.elcobre.lavanderiaelcobre.ui.screens.avisos

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.elcobre.lavanderiaelcobre.data.model.CONTENIDO_AVISO_MAXIMO
import com.elcobre.lavanderiaelcobre.data.model.DestinatarioAviso
import com.elcobre.lavanderiaelcobre.data.model.NuevoAvisoInput
import com.elcobre.lavanderiaelcobre.data.model.TITULO_AVISO_MAXIMO
import com.elcobre.lavanderiaelcobre.ui.components.CobreButton
import com.elcobre.lavanderiaelcobre.ui.components.CobreIcons
import com.elcobre.lavanderiaelcobre.ui.components.CobreTextField
import com.elcobre.lavanderiaelcobre.ui.components.GlassSegmentedControl
import com.elcobre.lavanderiaelcobre.ui.components.GlassSheet
import com.elcobre.lavanderiaelcobre.ui.gestion.AvisosEquipoViewModel
import com.elcobre.lavanderiaelcobre.ui.screens.produccion.ErrorHoja
import com.elcobre.lavanderiaelcobre.ui.theme.CobreTheme

/** Publicación de un aviso para todos, operarios o recepción (`/intranet/comunicacion`). */
@Composable
fun PublicarAvisoSheet(vm: AvisosEquipoViewModel, onCerrar: () -> Unit) {
    var titulo by remember { mutableStateOf("") }
    var contenido by remember { mutableStateOf("") }
    var destinatario by remember { mutableStateOf(DestinatarioAviso.TODOS) }
    var error by remember { mutableStateOf<String?>(null) }
    GlassSheet(
        onDismiss = { if (!vm.guardando) onCerrar() },
        title = "Nuevo aviso",
        subtitle = "Se muestra al instante en la app y la intranet.",
    ) {
        Text("Destinatarios", style = MaterialTheme.typography.labelMedium, color = CobreTheme.colors.textSecondary)
        GlassSegmentedControl(DestinatarioAviso.entries, destinatario, { destinatario = it }, { it.etiqueta })
        CobreTextField(
            value = titulo,
            onValueChange = { if (it.length <= TITULO_AVISO_MAXIMO) titulo = it },
            label = "Título",
            supportingText = "${titulo.length}/$TITULO_AVISO_MAXIMO",
        )
        CobreTextField(
            value = contenido,
            onValueChange = { if (it.length <= CONTENIDO_AVISO_MAXIMO) contenido = it },
            label = "Contenido",
            singleLine = false,
            minLines = 4,
            supportingText = "${contenido.length}/$CONTENIDO_AVISO_MAXIMO",
        )
        ErrorHoja(error)
        CobreButton(
            text = "Publicar aviso",
            icon = CobreIcons.Send,
            loading = vm.guardando,
            fillWidth = true,
            onClick = {
                vm.publicar(NuevoAvisoInput(titulo, contenido, destinatario)) { problema ->
                    if (problema == null) onCerrar() else error = problema
                }
            },
        )
    }
}

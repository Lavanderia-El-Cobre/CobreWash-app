package com.elcobre.lavanderiaelcobre.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.elcobre.lavanderiaelcobre.data.model.Telefonos

/**
 * Teléfono con prefijo de Chile o de otro país de Latinoamérica, como `TelefonoInput`
 * de la web: el país se elige en un selector y la persona solo escribe su número
 * nacional. [valor] es el teléfono completo que se guarda (`+56 9 1234 5678`).
 */
@Composable
fun CampoTelefono(
    valor: String,
    onCambiar: (String) -> Unit,
    modifier: Modifier = Modifier,
    etiqueta: String = "Teléfono (opcional)",
    enabled: Boolean = true,
) {
    // El país elegido se conserva aunque el número esté vacío.
    var paisId by remember { mutableStateOf(Telefonos.detectar(valor).pais.id) }
    val detectado = Telefonos.detectar(valor)
    // Sincroniza el selector si el valor cambia desde fuera con otro prefijo reconocido.
    if (valor.isNotBlank() && detectado.reconocido && detectado.pais.codigo != Telefonos.pais(paisId).codigo) {
        paisId = detectado.pais.id
    }
    val pais = Telefonos.pais(paisId)
    val nacional = Telefonos.extraerNacional(pais, valor)
    val error = Telefonos.validarNacional(pais, nacional)
    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(8.dp)) {
        SelectorConBusqueda(
            etiqueta = "País del teléfono",
            valorActual = "${pais.etiqueta} (${pais.prefijoVisible})",
            opciones = Telefonos.paises,
            texto = { "${it.etiqueta} (${it.prefijoVisible})" },
            onSeleccionar = {
                paisId = it.id
                onCambiar(Telefonos.formatear(it.id, Telefonos.extraerNacional(it, valor)))
            },
            enabled = enabled,
        )
        CobreTextField(
            value = Telefonos.formatearNacional(pais, nacional),
            onValueChange = { texto ->
                val (nuevoPais, digitos) = Telefonos.interpretarEntrada(pais, texto)
                paisId = nuevoPais.id
                onCambiar(Telefonos.formatear(nuevoPais.id, digitos))
            },
            label = "$etiqueta · ${pais.prefijoVisible}",
            placeholder = pais.ejemplo,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
            // Solo marca error al alcanzar el largo mínimo para no alertar mientras se escribe.
            isError = nacional.length >= pais.largos.min() && error != null,
            supportingText = when {
                nacional.isEmpty() -> null
                error == null -> Telefonos.describir(pais)
                nacional.length >= pais.largos.min() -> error
                else -> null
            },
            enabled = enabled,
        )
    }
}

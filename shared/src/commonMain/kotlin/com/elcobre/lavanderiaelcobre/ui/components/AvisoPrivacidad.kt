package com.elcobre.lavanderiaelcobre.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.elcobre.lavanderiaelcobre.ui.theme.BackgroundWarm
import com.elcobre.lavanderiaelcobre.ui.theme.CobreShapes
import com.elcobre.lavanderiaelcobre.ui.theme.CobreTheme
import com.elcobre.lavanderiaelcobre.ui.theme.DarkSurface1

private const val ORIGEN_PRIVACIDAD = "https://lavanderia-elcobre.vercel.app"

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AvisoPrivacidad(modifier: Modifier = Modifier, mostrarTarjeta: Boolean = false) {
    var abierto by rememberSaveable { mutableStateOf(false) }
    var errorEnlace by remember { mutableStateOf(false) }
    val enlaces = LocalUriHandler.current

    val abrir = { errorEnlace = false; abierto = true }
    if (mostrarTarjeta) AccesoPrivacidadTarjeta(onClick = abrir, modifier = modifier)
    else AccesoPrivacidadCompacto(onClick = abrir, modifier = modifier)

    if (abierto) ModalBottomSheet(
        onDismissRequest = { abierto = false },
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
        containerColor = CobreTheme.colors.surface,
    ) {
        ContenidoPrivacidad(
            errorEnlace = errorEnlace,
            onCerrar = { abierto = false },
            onAbrirEnlace = { ruta ->
                try {
                    enlaces.openUri(ORIGEN_PRIVACIDAD + ruta)
                } catch (_: Exception) { errorEnlace = true }
            },
        )
    }
}

@Composable
private fun AccesoPrivacidadCompacto(onClick: () -> Unit, modifier: Modifier = Modifier) {
    val accent = CobreTheme.colors.accentText
    OutlinedButton(
        onClick = onClick,
        modifier = modifier.heightIn(min = 48.dp),
        shape = CobreShapes.md,
        border = BorderStroke(1.dp, accent.copy(alpha = 0.22f)),
        colors = ButtonDefaults.outlinedButtonColors(
            contentColor = accent,
            containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.35f),
        ),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
    ) {
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
            Icon(CobreIcons.Lock, contentDescription = null, modifier = Modifier.size(16.dp))
            Text("Privacidad y mis datos", style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.SemiBold)
            Icon(CobreIcons.ArrowForward, contentDescription = null, modifier = Modifier.size(16.dp))
        }
    }
}

@Composable
private fun AccesoPrivacidadTarjeta(onClick: () -> Unit, modifier: Modifier = Modifier) {
    val interaction = remember { MutableInteractionSource() }
    GlassCard(
        modifier = modifier.fillMaxWidth()
            .pressableScale(interaction)
            .clip(CobreShapes.lg)
            .clickable(interactionSource = interaction, indication = null, role = Role.Button, onClick = onClick),
    ) {
        Row(horizontalArrangement = Arrangement.spacedBy(14.dp), verticalAlignment = Alignment.CenterVertically) {
            IconoPrivacidad()
            Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text("Privacidad y mis datos", style = MaterialTheme.typography.titleSmall, color = brandHeadingColor())
                Text(
                    "Conoce cómo se utiliza tu información y accede al canal de derechos.",
                    style = MaterialTheme.typography.bodySmall,
                    color = brandMutedColor(),
                )
            }
            Icon(
                CobreIcons.ArrowForward,
                contentDescription = null,
                tint = CobreTheme.colors.accent,
                modifier = Modifier.size(20.dp),
            )
        }
    }
}

@Composable
private fun IconoPrivacidad() {
    Box(
        modifier = Modifier.size(44.dp).background(BrandCtaGradient, CobreShapes.md),
        contentAlignment = Alignment.Center,
    ) {
        Icon(CobreIcons.Lock, contentDescription = null, tint = Color.White, modifier = Modifier.size(22.dp))
    }
}

@Composable
private fun ContenidoPrivacidad(errorEnlace: Boolean, onCerrar: () -> Unit, onAbrirEnlace: (String) -> Unit) {
    Column(
        modifier = Modifier.fillMaxWidth().verticalScroll(rememberScrollState()).padding(horizontal = 24.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp), verticalAlignment = Alignment.CenterVertically) {
            IconoPrivacidad()
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    "Privacidad y mis datos",
                    style = MaterialTheme.typography.titleLarge,
                    color = brandHeadingColor(),
                    fontWeight = FontWeight.ExtraBold,
                    modifier = Modifier.semantics { heading() },
                )
                Text("Lavandería El Cobre · Gestión interna", style = MaterialTheme.typography.bodySmall, color = brandMutedColor())
            }
            IconButton(onClick = onCerrar) {
                Icon(CobreIcons.Close, contentDescription = "Cerrar privacidad", tint = brandMutedColor())
            }
        }
        Text(
            "Consulta para qué usamos tu información y cómo puedes gestionar tus datos.",
            style = MaterialTheme.typography.bodyMedium,
            color = brandMutedColor(),
        )
        SeccionesPrivacidad()
        if (errorEnlace) {
            Text(
                "No se pudo abrir el navegador. Visita $ORIGEN_PRIVACIDAD/privacidad/derechos.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.semantics { liveRegion = LiveRegionMode.Polite },
            )
        }
        AccionesPrivacidad(onAbrirEnlace)
        Spacer(Modifier.height(12.dp))
    }
}

@Composable
private fun SeccionesPrivacidad() {
    GlassCard(modifier = Modifier.fillMaxWidth(), elevation = 4.dp) {
        Column(verticalArrangement = Arrangement.spacedBy(20.dp)) {
            SeccionPrivacidad(
                CobreIcons.Person, "Tu cuenta y tus operaciones",
                "Utilizamos tu cuenta, rol y operaciones para gestionar el trabajo y proteger el acceso.",
            )
            SeccionPrivacidad(
                CobreIcons.PhotoCamera, "Cámara y fotografías",
                "La cámara se usa al leer QR o capturar inspecciones; no se solicita ubicación. " +
                    "Fotografía solo el vehículo: evita rostros, documentos y objetos personales.",
            )
            SeccionPrivacidad(
                CobreIcons.Schedule, "Capturas temporales",
                "Se conservan mientras se confirma el envío y se limpian al terminar, al cerrar sesión o al iniciar la app " +
                    "si llevan más de 48 horas. Las copias del servidor tienen una conservación independiente.",
            )
            SeccionPrivacidad(
                CobreIcons.Lock, "Ejercer tus derechos (Ley N° 21.719)",
                "Conforme a la ley, puedes pedir acceso, rectificación, supresión, " +
                    "oposición, portabilidad o bloqueo en el formulario de la web. " +
                    "Plazo general: máx. 30 días corridos (bloqueo urgente: 2 días hábiles). " +
                    "Tienes derecho a reclamar ante la Agencia de Protección de Datos Personales " +
                    "dentro de 30 días hábiles.",
            )
            SeccionPrivacidad(
                CobreIcons.Inventory, "Ubicación de servidores y transferencias",
                "Base de datos y fotos en Santiago de Chile (región southamerica-west1); " +
                    "servicios web en São Paulo, Brasil (región gru1) y autenticación en EE.UU. (región us-central1), " +
                    "conforme a las normas de transferencias internacionales (Arts. 27 a 29 de la Ley N° 21.719).",
            )
        }
    }
}

@Composable
private fun SeccionPrivacidad(icon: ImageVector, titulo: String, descripcion: String) {
    val accent = CobreTheme.colors.accentText
    Row(horizontalArrangement = Arrangement.spacedBy(12.dp), verticalAlignment = Alignment.Top) {
        Box(
            modifier = Modifier.size(34.dp).background(CobreTheme.colors.accentContainer, CobreShapes.md),
            contentAlignment = Alignment.Center,
        ) {
            Icon(icon, contentDescription = null, tint = accent, modifier = Modifier.size(18.dp))
        }
        Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(5.dp)) {
            Text(
                titulo, style = MaterialTheme.typography.titleSmall, color = brandHeadingColor(),
                modifier = Modifier.semantics { heading() },
            )
            Text(descripcion, style = MaterialTheme.typography.bodySmall, color = brandMutedColor())
        }
    }
}

@Composable
private fun AccionesPrivacidad(onAbrirEnlace: (String) -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Button(
            onClick = { onAbrirEnlace("/privacidad") },
            modifier = Modifier.fillMaxWidth().heightIn(min = 48.dp).background(BrandCtaGradient, CobreShapes.md),
            shape = CobreShapes.md,
            colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent, contentColor = Color.White),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 14.dp),
        ) {
            Text("Leer aviso de privacidad", fontWeight = FontWeight.Bold)
        }
        OutlinedButton(
            onClick = { onAbrirEnlace("/privacidad/derechos") },
            modifier = Modifier.fillMaxWidth().heightIn(min = 48.dp),
            shape = CobreShapes.md,
            border = BorderStroke(1.dp, CobreTheme.colors.accent.copy(alpha = 0.35f)),
            colors = ButtonDefaults.outlinedButtonColors(contentColor = CobreTheme.colors.accentText),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 14.dp),
        ) {
            Text("Ejercer mis derechos", fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
fun RecordatorioPrivacidadFotos() {
    Row(
        modifier = Modifier.fillMaxWidth().background(CobreTheme.colors.accentContainer, CobreShapes.md).padding(12.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        Icon(
            CobreIcons.Lock, contentDescription = null, tint = CobreTheme.colors.accentText,
            modifier = Modifier.size(16.dp),
        )
        Text(
            "Fotografía solo el vehículo. Evita rostros, documentos y objetos personales ajenos a la inspección.",
            style = MaterialTheme.typography.bodySmall,
            color = brandMutedColor(),
        )
    }
}

@Preview(name = "Privacidad · Claro", widthDp = 360)
@Composable
internal fun PrivacidadClaroPreview() {
    CobreTheme(darkTheme = false) {
        Box(Modifier.background(BackgroundWarm).padding(16.dp)) {
            AccesoPrivacidadTarjeta(onClick = {})
        }
    }
}

@Preview(name = "Privacidad · Oscuro", widthDp = 360)
@Composable
internal fun PrivacidadOscuroPreview() {
    CobreTheme(darkTheme = true) {
        Box(Modifier.background(DarkSurface1).padding(16.dp)) {
            AccesoPrivacidadTarjeta(onClick = {})
        }
    }
}

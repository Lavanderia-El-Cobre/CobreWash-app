package com.elcobre.lavanderiaelcobre.ui.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.graphics.vector.ImageVector
import com.elcobre.lavanderiaelcobre.data.model.EtapaProceso
import com.elcobre.lavanderiaelcobre.data.model.Prioridad
import com.elcobre.lavanderiaelcobre.data.model.SeveridadStock
import com.elcobre.lavanderiaelcobre.data.model.TipoAlerta
import com.elcobre.lavanderiaelcobre.data.model.TipoComanda
import com.elcobre.lavanderiaelcobre.data.model.TipoEvento
import com.elcobre.lavanderiaelcobre.ui.theme.CobreTheme
import com.elcobre.lavanderiaelcobre.ui.theme.Copper600
import com.elcobre.lavanderiaelcobre.ui.theme.Copper700
import com.elcobre.lavanderiaelcobre.ui.theme.Indigo
import com.elcobre.lavanderiaelcobre.ui.theme.IndigoLight
import com.elcobre.lavanderiaelcobre.ui.theme.StatusAmber
import com.elcobre.lavanderiaelcobre.ui.theme.StatusAmberDark
import com.elcobre.lavanderiaelcobre.ui.theme.StatusBlue
import com.elcobre.lavanderiaelcobre.ui.theme.StatusBlueDark
import com.elcobre.lavanderiaelcobre.ui.theme.StatusGreen
import com.elcobre.lavanderiaelcobre.ui.theme.StatusGreenDark
import com.elcobre.lavanderiaelcobre.ui.theme.StatusRed
import com.elcobre.lavanderiaelcobre.ui.theme.StatusRedDark
import com.elcobre.lavanderiaelcobre.ui.theme.StatusRose
import com.elcobre.lavanderiaelcobre.ui.theme.StatusRoseDark
import com.elcobre.lavanderiaelcobre.ui.theme.StatusSlate
import com.elcobre.lavanderiaelcobre.ui.theme.StatusSlateDark
import com.elcobre.lavanderiaelcobre.ui.theme.cobreIsDark

/** Color de título/encabezado (Ink en claro); equivale a `CobreTheme.colors.textPrimary`. */
@Composable
fun brandHeadingColor(): Color = CobreTheme.colors.textPrimary

/** Color de texto secundario/de soporte, AA en ambos temas; equivale a `CobreTheme.colors.textSecondary`. */
@Composable
fun brandMutedColor(): Color = CobreTheme.colors.textSecondary

/**
 * Relleno de los CTA heredados. DESIGN.md es minimal: el degradado cobre pasa a un
 * cobre sólido (4.8:1 con texto blanco, en ambos temas). Los CTA nuevos usan [CobreButton].
 */
val BrandCtaGradient: Brush = SolidColor(Copper600)

/** Títulos destacados heredados: cobre sólido legible como texto, sin degradado. */
val BrandTextGradient: Brush = SolidColor(Copper700)

/**
 * Color resuelto de un estado (etapa, prioridad, alerta), ya adaptado al tema
 * actual: [accent] para íconos/indicadores, [container] para el fondo del
 * chip, [content] para texto legible sobre [container].
 */
data class ChipColors(
    val container: Color,
    val content: Color,
    val accent: Color,
)

/** Deriva un [ChipColors] coherente a partir de un matiz base para claro/oscuro. */
@Composable
private fun statusColors(light: Color, dark: Color): ChipColors {
    val isDark = cobreIsDark()
    return if (isDark) {
        ChipColors(
            container = dark.copy(alpha = 0.22f),
            content = dark,
            accent = dark,
        )
    } else {
        ChipColors(
            container = light.copy(alpha = 0.14f),
            content = lerp(light, Color.Black, 0.28f), // más oscuro => texto AA sobre el tint
            accent = light,
        )
    }
}

/**
 * Color por estado: índigo (secondary de DESIGN.md) mientras está en proceso, verde
 * solo en la etapa final ("Listo"). El cobre queda reservado a las acciones, así que
 * un estado nunca compite visualmente con el botón "Avanzar".
 */
@Composable
fun etapaColors(etapa: EtapaProceso): ChipColors = when (etapa) {
    EtapaProceso.ENTREGA, EtapaProceso.ENTREGA_DOMICILIO -> statusColors(StatusGreen, StatusGreenDark)
    else -> statusColors(Indigo, IndigoLight)
}

/** Ícono del tipo de comanda (tienda vs. domicilio). */
fun tipoIcon(tipo: TipoComanda): ImageVector = when (tipo) {
    TipoComanda.EN_TIENDA -> CobreIcons.Inbox
    TipoComanda.DOMICILIO -> CobreIcons.LocalShipping
}

fun etapaIcon(etapa: EtapaProceso): ImageVector = when (etapa) {
    EtapaProceso.RETIRO -> CobreIcons.LocalShipping
    EtapaProceso.RECEPCION -> CobreIcons.Inbox
    EtapaProceso.RECEPCION_PLANTA -> CobreIcons.Inbox
    EtapaProceso.LAVADO -> CobreIcons.LocalLaundry
    EtapaProceso.SECADO -> CobreIcons.Air
    EtapaProceso.PLANCHADO -> CobreIcons.Checkroom
    EtapaProceso.ENTREGA -> CobreIcons.DoneAll
    EtapaProceso.ENTREGA_DOMICILIO -> CobreIcons.LocalShipping
}

@Composable
fun prioridadColors(prioridad: Prioridad): ChipColors = when (prioridad) {
    Prioridad.ALTA -> statusColors(StatusRed, StatusRedDark)
    Prioridad.MEDIA -> statusColors(StatusAmber, StatusAmberDark)
    Prioridad.BAJA -> statusColors(StatusSlate, StatusSlateDark)
}

/** Color de la severidad de una alerta de stock (ámbar = bajo, rojo = agotado). */
@Composable
fun severidadColors(severidad: SeveridadStock): ChipColors = when (severidad) {
    SeveridadStock.BAJO -> statusColors(StatusAmber, StatusAmberDark)
    SeveridadStock.AGOTADO -> statusColors(StatusRed, StatusRedDark)
}

/** Color de acento de una alerta, ya adaptado al tema. */
@Composable
fun alertaColor(tipo: TipoAlerta): Color {
    val isDark = cobreIsDark()
    return when (tipo) {
        TipoAlerta.RETRASO -> if (isDark) StatusAmberDark else StatusAmber
        TipoAlerta.INCIDENCIA -> if (isDark) StatusRedDark else StatusRed
        TipoAlerta.INSUMO_CRITICO -> if (isDark) StatusRoseDark else StatusRose
    }
}

fun alertaIcon(tipo: TipoAlerta): ImageVector = when (tipo) {
    TipoAlerta.RETRASO -> CobreIcons.Schedule
    TipoAlerta.INCIDENCIA -> CobreIcons.Warning
    TipoAlerta.INSUMO_CRITICO -> CobreIcons.Inventory
}

fun eventoIcon(tipo: TipoEvento): ImageVector = when (tipo) {
    TipoEvento.CAMBIO_ETAPA -> CobreIcons.CheckCircle
    TipoEvento.COMENTARIO -> CobreIcons.Comment
    TipoEvento.ALERTA -> CobreIcons.Warning
    TipoEvento.ALERTA_RESUELTA -> CobreIcons.Check
}

/** Color de acento de un evento del historial, adaptado al tema. */
@Composable
fun eventoColor(tipo: TipoEvento): Color {
    val isDark = cobreIsDark()
    return when (tipo) {
        TipoEvento.CAMBIO_ETAPA -> if (isDark) StatusBlueDark else StatusBlue
        TipoEvento.COMENTARIO -> if (isDark) StatusSlateDark else StatusSlate
        TipoEvento.ALERTA -> if (isDark) StatusRedDark else StatusRed
        TipoEvento.ALERTA_RESUELTA -> if (isDark) StatusGreenDark else StatusGreen
    }
}

/** Tonos semánticos de los chips de estado de las vistas de gestión. */
enum class Tono { NEUTRO, INFO, EXITO, ALERTA, PELIGRO, MARCA }

@Composable
fun tonoColors(tono: Tono): ChipColors = when (tono) {
    Tono.NEUTRO -> statusColors(StatusSlate, StatusSlateDark)
    Tono.INFO -> statusColors(Indigo, IndigoLight)
    Tono.EXITO -> statusColors(StatusGreen, StatusGreenDark)
    Tono.ALERTA -> statusColors(StatusAmber, StatusAmberDark)
    Tono.PELIGRO -> statusColors(StatusRed, StatusRedDark)
    Tono.MARCA -> ChipColors(
        container = CobreTheme.colors.accentContainer,
        content = CobreTheme.colors.accentText,
        accent = CobreTheme.colors.accent,
    )
}

/** Pendiente en ámbar, en proceso en índigo, lista en cobre (hay que entregarla), entregada en verde. */
fun tonoEstadoComanda(estado: com.elcobre.lavanderiaelcobre.data.model.EstadoComanda): Tono = when (estado) {
    com.elcobre.lavanderiaelcobre.data.model.EstadoComanda.PENDIENTE -> Tono.ALERTA
    com.elcobre.lavanderiaelcobre.data.model.EstadoComanda.EN_PROCESO -> Tono.INFO
    com.elcobre.lavanderiaelcobre.data.model.EstadoComanda.FINALIZADA -> Tono.MARCA
    com.elcobre.lavanderiaelcobre.data.model.EstadoComanda.ENTREGADA -> Tono.EXITO
    com.elcobre.lavanderiaelcobre.data.model.EstadoComanda.ANULADA -> Tono.NEUTRO
}

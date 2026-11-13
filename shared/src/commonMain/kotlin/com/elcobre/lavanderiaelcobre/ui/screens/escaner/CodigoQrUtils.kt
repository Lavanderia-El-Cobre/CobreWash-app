package com.elcobre.lavanderiaelcobre.ui.screens.escaner

import com.elcobre.lavanderiaelcobre.data.ORIGEN_WEB
import com.elcobre.lavanderiaelcobre.data.ORIGEN_WEB_ANTERIOR

private const val RUTA_SEGUIMIENTO = "/seguimiento?qr="

/**
 * Prefijos de QR de comanda aceptados: el dominio actual (con y sin `www`) y el anterior,
 * que solo se lee para comprobantes impresos antes del cambio de dominio.
 */
private val PREFIJOS_SEGUIMIENTO = listOf(
    ORIGEN_WEB,
    ORIGEN_WEB.replace("://www.", "://"),
    ORIGEN_WEB_ANTERIOR,
).map { it + RUTA_SEGUIMIENTO }

private val UUID_REGEX = Regex("^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}$")

/**
 * Validar y extraer el UUID de un QR de la comanda.
 * Rechazar HTTP, dominios parecidos, credenciales, otra ruta, parámetro vacío o duplicado.
 */
fun extraerCodigoQr(raw: String): String? {
    val prefix = PREFIJOS_SEGUIMIENTO.firstOrNull { raw.startsWith(it) } ?: return null
    return raw.substringAfter(prefix).trim().takeIf { UUID_REGEX.matches(it) }
}

/** Normaliza el UUID de catálogo, con o sin guiones; no acepta enlaces de comanda. */
fun extraerQrInsumo(raw: String): String? {
    val codigo = raw.trim().lowercase()
    return when {
        codigo.matches(Regex("^[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}$")) -> codigo
        codigo.matches(Regex("^[0-9a-f]{32}$")) -> listOf(
            codigo.substring(0, 8),
            codigo.substring(8, 12),
            codigo.substring(12, 16),
            codigo.substring(16, 20),
            codigo.substring(20, 32),
        ).joinToString("-")
        else -> null
    }
}

private const val URL_SEGUIMIENTO = ORIGEN_WEB + RUTA_SEGUIMIENTO

/**
 * Enlace público que lleva el QR del comprobante, igual que `crearUrlSeguimiento` de la
 * web: UUID canónico en minúsculas. `null` si el código guardado no es un UUID válido.
 */
fun urlSeguimientoComanda(codigoQr: String): String? = extraerQrInsumo(codigoQr)?.let { URL_SEGUIMIENTO + it }

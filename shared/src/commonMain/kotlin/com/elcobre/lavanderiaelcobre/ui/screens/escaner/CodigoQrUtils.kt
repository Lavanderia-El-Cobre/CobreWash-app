package com.elcobre.lavanderiaelcobre.ui.screens.escaner

/**
 * Validar y extraer el UUID de un QR de la comanda.
 * Rechazar HTTP, dominios parecidos, credenciales, otra ruta, parámetro vacío o duplicado.
 */
fun extraerCodigoQr(raw: String): String? {
    val prefix = "https://lavanderia-elcobre.vercel.app/seguimiento?qr="
    if (raw.startsWith(prefix)) {
        val uuid = raw.substringAfter(prefix).trim()
        if (uuid.matches(Regex("^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}$"))) {
            return uuid
        }
    }
    return null
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

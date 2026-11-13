package com.elcobre.lavanderiaelcobre.ui

/** Destinos de navegación. Detalle navega por id. */
sealed interface Pantalla {
    data object Login : Pantalla
    data object Dashboard : Pantalla
    data object Avisos : Pantalla
    data object Escaner : Pantalla
    data object Insumos : Pantalla
    data object Configuracion : Pantalla
    data class Detalle(val pedidoId: String) : Pantalla
    data object Vehiculos : Pantalla

    // Recepción y administración.
    data object Inicio : Pantalla
    data object Comandas : Pantalla
    data object Produccion : Pantalla
    data object Clientes : Pantalla
    data object Stock : Pantalla
    data object Mas : Pantalla

    // Reparto.
    data object Reparto : Pantalla
}

/** Orden relativo de cada destino, usado para decidir la dirección del slide de [CobreApp]. */
internal fun orden(p: Pantalla): Int = when (p) {
    Pantalla.Login -> 0
    Pantalla.Escaner -> 2
    is Pantalla.Detalle -> 3
    else -> 1
}

/**
 * Secciones agrupadas en la pestaña "Más" cuando un rol tiene más de cinco destinos,
 * como la pestaña "Más" de iOS. [roles] indica quién la ve (igual que `NAV_POR_ROL` de la web).
 */
enum class SeccionMas(val titulo: String, val descripcion: String) {
    PRODUCCION("Producción", "Etapas de cada comanda en planta"),
    INCIDENCIAS("Incidencias", "Problemas reportados por producción"),
    CLIENTES("Clientes", "Fichas, historial y datos de contacto"),
    COTIZACIONES("Cotizaciones", "Solicitudes del formulario de contacto"),
    AVISOS("Comunicación", "Avisos para el equipo"),
    USUARIOS("Usuarios", "Cuentas, roles y acceso"),
    REPORTES("Reportes", "Volumen, cuentas y servicios por periodo"),
    ETAPAS("Etapas del flujo", "Nombres y tiempos estimados de producción"),
    AJUSTES("Ajustes", "Tu cuenta, apariencia y sesión"),
}

/** Secciones de "Más" para recepción: lo que no cabe en sus cinco pestañas. */
internal val MasRecepcion = listOf(SeccionMas.INCIDENCIAS, SeccionMas.COTIZACIONES, SeccionMas.AJUSTES)

/** Secciones de "Más" para administración. */
internal val MasAdmin = listOf(
    SeccionMas.PRODUCCION,
    SeccionMas.INCIDENCIAS,
    SeccionMas.CLIENTES,
    SeccionMas.COTIZACIONES,
    SeccionMas.AVISOS,
    SeccionMas.USUARIOS,
    SeccionMas.REPORTES,
    SeccionMas.ETAPAS,
    SeccionMas.AJUSTES,
)

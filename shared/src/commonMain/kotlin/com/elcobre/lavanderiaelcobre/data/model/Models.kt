package com.elcobre.lavanderiaelcobre.data.model

/**
 * Etapas del proceso productivo; el orden real lo define [TipoComanda.flujo], no la
 * declaración del enum. Recepción/Lavado/Secado/Planchado/Entrega son las mismas 5
 * etapas que usa la web (`lib/mock/comandas.ts`); Retiro, Recepción en planta y
 * Entrega a domicilio son propias del flujo a domicilio, que no existe en la web.
 */
enum class EtapaProceso(val displayName: String, val corto: String) {
    RETIRO("Retiro en domicilio", "Retiro"),
    RECEPCION("Recepción", "Recepción"),
    RECEPCION_PLANTA("Recepción en planta", "Recepción"),
    LAVADO("Lavado", "Lavado"),
    SECADO("Secado", "Secado"),
    PLANCHADO("Planchado", "Planchado"),
    ENTREGA("Entrega", "Entrega"),
    ENTREGA_DOMICILIO("Entrega a domicilio", "Entrega"),
}

/** Tipo de comanda: determina el flujo de etapas que sigue el pedido. */
enum class TipoComanda(val displayName: String, val corto: String) {
    EN_TIENDA("Retiro en tienda", "Tienda"),
    DOMICILIO("Envío a domicilio", "Domicilio");

    val flujo: List<EtapaProceso>
        get() = when (this) {
            EN_TIENDA -> listOf(
                EtapaProceso.RECEPCION,
                EtapaProceso.LAVADO,
                EtapaProceso.SECADO,
                EtapaProceso.PLANCHADO,
                EtapaProceso.ENTREGA,
            )
            DOMICILIO -> listOf(
                EtapaProceso.RETIRO,
                EtapaProceso.RECEPCION_PLANTA,
                EtapaProceso.LAVADO,
                EtapaProceso.SECADO,
                EtapaProceso.PLANCHADO,
                EtapaProceso.ENTREGA_DOMICILIO,
            )
        }
}

enum class Prioridad(val displayName: String) {
    ALTA("Alta"),
    MEDIA("Media"),
    BAJA("Baja"),
}

enum class TipoAlerta(val displayName: String, val descripcionCorta: String) {
    RETRASO("Retraso", "El pedido va con demora respecto al plazo"),
    INCIDENCIA("Incidencia", "Problema con una prenda o el proceso"),
    INSUMO_CRITICO("Insumo crítico", "Falta de detergente, agua o consumible"),
}

enum class TipoEvento {
    CAMBIO_ETAPA,
    COMENTARIO,
    ALERTA,
    ALERTA_RESUELTA,
}

data class EventoHistorial(
    val tipo: TipoEvento,
    val titulo: String,
    val detalle: String,
    val hora: String,
    val autor: String,
)

/**
 * Modelo de dominio del pedido, inmutable y sin dependencias de Compose: cada
 * transición devuelve una copia; [com.elcobre.lavanderiaelcobre.ui.CobreViewModel]
 * observa los cambios y guarda el estado. Historial con el evento más reciente primero.
 */
data class Pedido(
    val id: String,
    val comanda: String,
    val cliente: String,
    val tipo: TipoComanda,
    val tipoServicio: String,
    val prioridad: Prioridad,
    val piezas: Int,
    val plazoEntrega: String,
    val etapaActual: EtapaProceso,
    val alertaActiva: TipoAlerta? = null,
    val historial: List<EventoHistorial> = emptyList(),
    // Referencias para la mutación en Firebase:
    val etapaIdDb: String? = null,
    val ordenEtapaDb: Int? = null,
    val estadoComandaDb: String? = null,
    val datosComanda: List<String> = emptyList(),
    val prendas: List<String> = emptyList(),
    val etapasDetalle: List<String> = emptyList(),
) {
    val estaBloqueado: Boolean get() = alertaActiva != null
    val flujo: List<EtapaProceso> get() = tipo.flujo

    /** 0-based; usado también para armar textos "Etapa X de N" junto a [totalEtapas]. */
    val indiceEtapa: Int get() = flujo.indexOf(etapaActual).coerceAtLeast(0)
    val totalEtapas: Int get() = flujo.size
    val siguienteEtapa: EtapaProceso? get() = flujo.getOrNull(indiceEtapa + 1)
    val esFinal: Boolean get() = if (estadoComandaDb != null) {
        estadoComandaDb in setOf("ENTREGADA", "ANULADA")
    } else siguienteEtapa == null
    val puedeCompletar: Boolean get() = if (estadoComandaDb != null) {
        !esFinal && etapaIdDb != null && ordenEtapaDb != null
    } else siguienteEtapa != null

    /** Avanza a la etapa siguiente. Sin efecto si está bloqueado o ya es final. */
    fun avanzarEtapa(hora: String, autor: String, comentario: String?): Pedido {
        if (estaBloqueado) return this // no se avanza con alertas activas
        val siguiente = siguienteEtapa ?: return this
        return copy(
            etapaActual = siguiente,
            historial = listOf(
                EventoHistorial(
                    tipo = TipoEvento.CAMBIO_ETAPA,
                    titulo = "${etapaActual.displayName} → ${siguiente.displayName}",
                    detalle = comentario?.takeIf { it.isNotBlank() }
                        ?: "Avance de etapa registrado.",
                    hora = hora,
                    autor = autor,
                ),
            ) + historial,
        )
    }

    fun agregarComentario(texto: String, hora: String, autor: String): Pedido = copy(
        historial = listOf(
            EventoHistorial(TipoEvento.COMENTARIO, "Comentario", texto, hora, autor),
        ) + historial,
    )

    fun registrarAlerta(tipo: TipoAlerta, texto: String, hora: String, autor: String): Pedido = copy(
        alertaActiva = tipo,
        historial = listOf(
            EventoHistorial(
                tipo = TipoEvento.ALERTA,
                titulo = "ALERTA: ${tipo.displayName}",
                detalle = texto.takeIf { it.isNotBlank() } ?: tipo.descripcionCorta,
                hora = hora,
                autor = autor,
            ),
        ) + historial,
    )

    fun resolverAlerta(hora: String, autor: String): Pedido {
        val tipo = alertaActiva ?: return this
        return copy(
            alertaActiva = null,
            historial = listOf(
                EventoHistorial(
                    tipo = TipoEvento.ALERTA_RESUELTA,
                    titulo = "Resuelto: ${tipo.displayName}",
                    detalle = "Incidencia solucionada. El proceso puede continuar.",
                    hora = hora,
                    autor = autor,
                ),
            ) + historial,
        )
    }
}

/** Operario autenticado. */
data class Operario(
    val nombre: String,
    val rol: String,
)

/** Administrador autenticado; gestiona la flota de reparto (RF-VE01 a RF-VE04). */
data class Administrador(
    val nombre: String,
    val rol: String = "Administrador",
)

/**
 * Sesión activa del prototipo: cada rol tiene su propia vista raíz (ver
 * [com.elcobre.lavanderiaelcobre.ui.CobreApp]). El SRS solo define la app Android
 * para el Operario; el acceso del Administrador es una ampliación de alcance consciente.
 */
sealed interface Sesion {
    data class DeOperario(val operario: Operario) : Sesion
    data class DeAdministrador(val administrador: Administrador) : Sesion
}

/** Comunicado de administración visible para su equipo destinatario. */
data class Aviso(
    val id: String,
    val titulo: String,
    val mensaje: String,
    val hora: String,
    val dirigidoA: String,
    val destacado: Boolean = false,
    val autor: String = "",
)

/** Insumo del catálogo de lavandería, reportable por el operario cuando escasea (RF-AN05 / RF-IN). */
enum class Insumo(val displayName: String, val unidad: String) {
    DETERGENTE("Detergente industrial", "L"),
    SUAVIZANTE("Suavizante", "L"),
    CLORO("Cloro / desinfectante", "L"),
    QUITAMANCHAS("Quitamanchas", "L"),
    BOLSAS("Bolsas de empaque", "un"),
    PERCHAS("Perchas", "un"),
}

enum class SeveridadStock(val displayName: String) {
    BAJO("Stock bajo"),
    AGOTADO("Agotado"),
}

/** Alerta de stock creada por el operario (RF-AN05), independiente de un pedido. */
data class AlertaStock(
    val id: String,
    val insumo: Insumo,
    val severidad: SeveridadStock,
    val nota: String,
    val hora: String,
    val autor: String,
)

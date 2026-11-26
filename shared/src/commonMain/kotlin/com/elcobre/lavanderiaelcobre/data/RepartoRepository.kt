package com.elcobre.lavanderiaelcobre.data

import com.elcobre.lavanderiaelcobre.data.model.EntregasReparto
import com.elcobre.lavanderiaelcobre.data.model.InspeccionInput
import com.elcobre.lavanderiaelcobre.data.model.PanelReparto
import com.elcobre.lavanderiaelcobre.data.model.SalidaReparto

/**
 * Sección del repartidor, igual que `/intranet/reparto` y `/intranet/entregas` de la web:
 * - Mi vehículo: `GetPanelRepartidor`, `RegistrarSalidaFlota` y `RegistrarRetornoFlota`
 *   (roles repartidor y conductor; el servidor exige que la salida sea propia).
 * - Entregas: `GetComandasParaEntrega`, `EntregarComanda` y `ReportarProblemaEntrega`.
 * Las salidas que administración programa desde la app se inician con el flujo anterior
 * (`RegistrarInspeccionAntes` + `IniciarSalidaVehiculo`).
 */
interface RepartoRepository {
    suspend fun panel(): PanelReparto = noDisponible()

    /** Salida propia con inspección y fotos. Devuelve el id de la salida. */
    suspend fun registrarSalida(input: SalidaFlotaInput): String = noDisponible()

    /** Regreso de la salida en curso con inspección y fotos: la salida queda FINALIZADA. */
    suspend fun registrarRetorno(input: RetornoFlotaInput): Unit = noDisponible()

    /** Salida programada por administración: inspección previa (si falta), fotos y paso a EN_SERVICIO. */
    suspend fun iniciarProgramada(salida: SalidaReparto, input: InspeccionInput): Unit = noDisponible()

    suspend fun entregas(): EntregasReparto = noDisponible()
    suspend fun entregar(comandaId: String, detalle: String?): Unit = noDisponible()
    suspend fun reportarProblema(comandaId: String, motivo: String, detalle: String?): Unit = noDisponible()

    /** Descarta estados de reintento locales de salidas/inspecciones al cancelar o reiniciar el flujo. */
    fun descartarReintentos() = Unit
}

/** Fábrica por plataforma de [RepartoRepository] (Firebase Data Connect en Android; stub en iOS). */
expect fun crearRepartoRepository(): RepartoRepository

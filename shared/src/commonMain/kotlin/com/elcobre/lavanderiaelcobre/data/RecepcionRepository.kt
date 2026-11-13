package com.elcobre.lavanderiaelcobre.data

import com.elcobre.lavanderiaelcobre.data.model.CatalogosComanda
import com.elcobre.lavanderiaelcobre.data.model.ClienteFicha
import com.elcobre.lavanderiaelcobre.data.model.ComandaGestion
import com.elcobre.lavanderiaelcobre.data.model.EstadoIncidencia
import com.elcobre.lavanderiaelcobre.data.model.FiltroComandas
import com.elcobre.lavanderiaelcobre.data.model.FormularioComanda
import com.elcobre.lavanderiaelcobre.data.model.IncidenciaGestion
import com.elcobre.lavanderiaelcobre.data.model.NuevoClienteInput
import com.elcobre.lavanderiaelcobre.data.model.PaginaComandas
import com.elcobre.lavanderiaelcobre.data.model.PaginaProduccion

internal const val NO_DISPONIBLE_PLATAFORMA = "Esta función aún no está disponible en iOS."

internal fun noDisponible(): Nothing = throw OperacionComandaException(NO_DISPONIBLE_PLATAFORMA)

/** Identidad de una comanda nueva: se conserva entre reintentos para no duplicarla. */
data class IdentidadComanda(val id: String, val numero: String)

/**
 * Comandas, clientes e incidencias de recepción y administración (`/intranet/comandas`,
 * `/intranet/clientes`, `/intranet/incidencias`). Cada método por defecto informa que no
 * está disponible, así iOS y los dobles de prueba solo implementan lo que usan.
 */
@Suppress("TooManyFunctions") // Una operación por mutación de la web; agruparlas ocultaría el mapeo 1:1.
interface RecepcionRepository {
    suspend fun comandas(filtro: FiltroComandas, offset: Int, limit: Int): PaginaComandas = noDisponible()
    suspend fun detalle(id: String): ComandaGestion? = noDisponible()
    suspend fun catalogos(): CatalogosComanda = noDisponible()

    /** Crea la comanda con su flujo de producción en una sola operación (`CrearComandaConFlujo`). Devuelve su id. */
    suspend fun crearComanda(identidad: IdentidadComanda, formulario: FormularioComanda): String = noDisponible()

    /** Reemplaza cabecera y prendas de una comanda pendiente (`EditarComandaConDetalles`). */
    suspend fun editarComanda(id: String, version: String, formulario: FormularioComanda): Unit = noDisponible()
    suspend fun anular(id: String, motivo: String): Unit = noDisponible()
    suspend fun entregar(id: String): Unit = noDisponible()
    suspend fun asociarFlujo(id: String): Unit = noDisponible()

    suspend fun fichasClientes(): List<ClienteFicha> = noDisponible()
    suspend fun crearCliente(input: NuevoClienteInput): String = noDisponible()
    suspend fun editarCliente(id: String, input: NuevoClienteInput): Unit = noDisponible()

    suspend fun incidencias(): List<IncidenciaGestion> = noDisponible()
    suspend fun actualizarIncidencia(id: String, estado: EstadoIncidencia): Unit = noDisponible()
}

/** Seguimiento de producción de toda la planta (`/intranet/seguimiento`). */
interface ProduccionRepository {
    suspend fun seguimiento(buscar: String, offset: Int, limit: Int): PaginaProduccion = noDisponible()
    suspend fun completarEtapa(comanda: ComandaGestion): Unit = noDisponible()
    suspend fun reasignar(comandaId: String, etapaId: String, operarioId: String, motivo: String): Unit = noDisponible()
    suspend fun reportarIncidencia(comandaId: String, motivo: String, descripcion: String): Unit = noDisponible()
}

expect fun crearRecepcionRepository(): RecepcionRepository

expect fun crearProduccionRepository(): ProduccionRepository

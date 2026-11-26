package com.elcobre.lavanderiaelcobre.data

import com.elcobre.lavanderiaelcobre.data.model.CatalogosComanda
import com.elcobre.lavanderiaelcobre.data.model.ClienteFicha
import com.elcobre.lavanderiaelcobre.data.model.ComandaGestion
import com.elcobre.lavanderiaelcobre.data.model.EstadoIncidencia
import com.elcobre.lavanderiaelcobre.data.model.EstimacionConsumo
import com.elcobre.lavanderiaelcobre.data.model.FiltroComandas
import com.elcobre.lavanderiaelcobre.data.model.FormularioComanda
import com.elcobre.lavanderiaelcobre.data.model.IncidenciaGestion
import com.elcobre.lavanderiaelcobre.data.model.NuevoClienteInput
import com.elcobre.lavanderiaelcobre.data.model.PaginaComandas
import com.elcobre.lavanderiaelcobre.data.model.PaginaProduccion

/** Mensaje uniforme cuando una operación aún no cuenta con implementación nativa en la plataforma. */
internal const val NO_DISPONIBLE_PLATAFORMA = "Esta función aún no está disponible en iOS."

/** Centraliza el fallo por defecto de interfaces de repositorio en plataformas sin soporte aún. */
internal fun noDisponible(): Nothing = throw OperacionComandaException(NO_DISPONIBLE_PLATAFORMA)

/** Identidad de una comanda nueva: se conserva entre reintentos para no duplicarla. */
data class IdentidadComanda(val id: String, val numero: String)

/** Resultado de registrar un cliente con cuenta en la plataforma (`lib/clientes/cuenta.ts`). */
data class ResultadoClienteConCuenta(
    /** Id de la ficha del cliente. */
    val id: String,
    /** `false` si el correo ya tenía cuenta: solo se creó la ficha. */
    val cuentaCreada: Boolean,
    val invitacionEnviada: Boolean,
) {
    /** Mensaje para el personal, igual a `avisoClienteConCuenta` de la web. */
    fun aviso(email: String): String = when {
        !cuentaCreada ->
            "Cliente registrado. $email ya tenía una cuenta: la ficha se asociará cuando inicie sesión con su correo verificado."
        invitacionEnviada ->
            "Cliente registrado con cuenta. Enviamos a $email un enlace para activar su cuenta y crear su contraseña."
        else ->
            "Cliente registrado con cuenta, pero no se pudo enviar la invitación a $email. " +
                "Pídele usar \"¿Olvidaste tu contraseña?\" para activarla."
    }
}

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

    /**
     * Ficha y cuenta del cliente (`CrearClienteConCuenta`): la cuenta se crea con una
     * contraseña aleatoria y el cliente recibe un enlace para definir la suya.
     */
    suspend fun crearClienteConCuenta(input: NuevoClienteInput): ResultadoClienteConCuenta = noDisponible()
    suspend fun editarCliente(id: String, input: NuevoClienteInput): Unit = noDisponible()

    /** Estimación de consumo de insumos que calcula la web (`POST /api/ia/estimar-comanda`). */
    suspend fun estimarConsumo(tipoServicioId: String, pesoKg: Double): EstimacionConsumo = noDisponible()

    suspend fun incidencias(): List<IncidenciaGestion> = noDisponible()
    suspend fun actualizarIncidencia(id: String, estado: EstadoIncidencia): Unit = noDisponible()
}

/** Seguimiento de producción de toda la planta (`/intranet/seguimiento`). */
interface ProduccionRepository {
    suspend fun seguimiento(buscar: String, offset: Int, limit: Int): PaginaProduccion = noDisponible()
    suspend fun completarEtapa(comanda: ComandaGestion): Unit = noDisponible()

    /** Reasigna una etapa activa a otro operario dejando [motivo] registrado en el historial. */
    suspend fun reasignar(comandaId: String, etapaId: String, operarioId: String, motivo: String): Unit = noDisponible()
    suspend fun reportarIncidencia(comandaId: String, motivo: String, descripcion: String): Unit = noDisponible()
}

/** Fábrica por plataforma de [RecepcionRepository] (Firebase Data Connect en Android; stub en iOS). */
expect fun crearRecepcionRepository(): RecepcionRepository

/** Fábrica por plataforma de [ProduccionRepository] (Firebase Data Connect en Android; stub en iOS). */
expect fun crearProduccionRepository(): ProduccionRepository

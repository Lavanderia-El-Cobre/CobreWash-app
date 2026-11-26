package com.elcobre.lavanderiaelcobre.data

import com.elcobre.lavanderiaelcobre.data.model.ConteoGestion
import com.elcobre.lavanderiaelcobre.data.model.EstadoSolicitud
import com.elcobre.lavanderiaelcobre.data.model.EtapaConfig
import com.elcobre.lavanderiaelcobre.data.model.NuevoAvisoInput
import com.elcobre.lavanderiaelcobre.data.model.NuevoInsumoInput
import com.elcobre.lavanderiaelcobre.data.model.NuevoUsuarioInput
import com.elcobre.lavanderiaelcobre.data.model.PaginaMovimientos
import com.elcobre.lavanderiaelcobre.data.model.PaginaSolicitudes
import com.elcobre.lavanderiaelcobre.data.model.ReporteGestion
import com.elcobre.lavanderiaelcobre.data.model.ResumenPanel
import com.elcobre.lavanderiaelcobre.data.model.RolRef
import com.elcobre.lavanderiaelcobre.data.model.TipoMovimientoInventario
import com.elcobre.lavanderiaelcobre.data.model.UsuarioGestion

/**
 * Bandeja de cotizaciones (admin y recepción) y comunicación interna (avisos).
 * Los métodos por defecto lanzan [noDisponible] para que plataformas parciales
 * (como iOS) y pruebas solo implementen lo necesario.
 */
interface ComunicacionRepository {
    suspend fun solicitudes(estado: EstadoSolicitud?, offset: Int, limit: Int): PaginaSolicitudes = noDisponible()
    suspend fun actualizarSolicitud(id: String, estado: EstadoSolicitud): Unit = noDisponible()

    /** Avisos activos publicados por administración (`GetAvisosAdministracion`). */
    suspend fun avisosAdministracion(offset: Int, limit: Int): PaginaAvisos = noDisponible()

    /** Avisos para el equipo del rol indicado: `operario` o `recepcionista` (`GetAvisosParaEquipo`). */
    suspend fun avisosEquipo(rol: String, offset: Int, limit: Int): PaginaAvisos = noDisponible()

    /** Publica un nuevo aviso dirigido a todo el equipo o a un rol específico. */
    suspend fun publicarAviso(input: NuevoAvisoInput): Unit = noDisponible()
}

/** Datos para editar una cuenta (`ActualizarUsuario`). */
data class EdicionUsuarioInput(
    val id: String,
    val rolId: String,
    val nombre: String,
    val apellido: String?,
    val telefono: String?,
    val activo: Boolean,
)

/**
 * Funciones exclusivas de administración: panel, usuarios, inventario, reportes y etapas.
 * Cada método corresponde 1:1 a una consulta o mutación de Firebase Data Connect usada en la web.
 */
@Suppress("TooManyFunctions") // Una operación por mutación de la web; agruparlas ocultaría el mapeo 1:1.
interface AdministracionRepository {
    suspend fun resumenPanel(): ResumenPanel = noDisponible()

    suspend fun roles(): List<RolRef> = noDisponible()
    suspend fun usuarios(): List<UsuarioGestion> = noDisponible()

    /**
     * Crea la cuenta en Firebase Auth sin cerrar la sesión del administrador, registra su perfil
     * y envía la invitación por correo. Devuelve `true` si el envío de la invitación tuvo éxito.
     */
    suspend fun crearUsuario(input: NuevoUsuarioInput): Boolean = noDisponible()
    suspend fun actualizarUsuario(input: EdicionUsuarioInput): Unit = noDisponible()

    suspend fun stock(): List<InsumoInventario> = noDisponible()
    suspend fun crearInsumo(input: NuevoInsumoInput): Unit = noDisponible()
    suspend fun actualizarInsumo(insumo: InsumoInventario): Unit = noDisponible()
    suspend fun registrarMovimiento(insumoId: String, tipo: TipoMovimientoInventario, cantidad: Double, motivo: String?): Unit =
        noDisponible()
    suspend fun historial(insumoId: String?, offset: Int, limit: Int): PaginaMovimientos = noDisponible()

    /** Conteos físicos en el estado indicado (`PENDIENTE`, `APROBADO`, `RECHAZADO`) o todos. */
    suspend fun conteos(estado: String?): List<ConteoGestion> = noDisponible()

    /** Aprueba un conteo pendiente y ajusta el stock en el servidor dejando [motivo] auditado. */
    suspend fun aprobarConteo(id: String, motivo: String): Unit = noDisponible()

    /** Rechaza un conteo pendiente sin modificar el stock, registrando el [motivo]. */
    suspend fun rechazarConteo(id: String, motivo: String): Unit = noDisponible()

    /** Reporte entre dos instantes en milisegundos (hasta exclusivo). */
    suspend fun reporte(desdeMs: Long, hastaMs: Long): ReporteGestion = noDisponible()

    suspend fun etapas(): List<EtapaConfig> = noDisponible()

    /** Actualiza los parámetros de una etapa del flujo (nombre, orden y tiempo estimado). */
    suspend fun configurarEtapa(etapa: EtapaConfig): Unit = noDisponible()
}

/** Cambio de contraseña de la cuenta actual (sección Configuración de la web). */
fun interface CuentaRepository {
    suspend fun cambiarContrasena(actual: String, nueva: String)
}

/** Fábrica por plataforma de [ComunicacionRepository] (Firebase en Android; stub en iOS). */
expect fun crearComunicacionRepository(): ComunicacionRepository

/** Fábrica por plataforma de [AdministracionRepository] (Firebase en Android; stub en iOS). */
expect fun crearAdministracionRepository(): AdministracionRepository

/** Fábrica por plataforma de [CuentaRepository] (Firebase Auth en Android; stub en iOS). */
expect fun crearCuentaRepository(): CuentaRepository

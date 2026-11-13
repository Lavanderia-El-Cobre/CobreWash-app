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

/** Bandeja de cotizaciones (admin y recepción) y comunicación interna (avisos). */
interface ComunicacionRepository {
    suspend fun solicitudes(estado: EstadoSolicitud?, offset: Int, limit: Int): PaginaSolicitudes = noDisponible()
    suspend fun actualizarSolicitud(id: String, estado: EstadoSolicitud): Unit = noDisponible()

    /** Avisos activos publicados por administración (`GetAvisosAdministracion`). */
    suspend fun avisosAdministracion(offset: Int, limit: Int): PaginaAvisos = noDisponible()

    /** Avisos para el equipo del rol indicado: `operario` o `recepcionista` (`GetAvisosParaEquipo`). */
    suspend fun avisosEquipo(rol: String, offset: Int, limit: Int): PaginaAvisos = noDisponible()
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

/** Funciones exclusivas de administración: panel, usuarios, inventario, reportes y etapas. */
@Suppress("TooManyFunctions") // Una operación por mutación de la web; agruparlas ocultaría el mapeo 1:1.
interface AdministracionRepository {
    suspend fun resumenPanel(): ResumenPanel = noDisponible()

    suspend fun roles(): List<RolRef> = noDisponible()
    suspend fun usuarios(): List<UsuarioGestion> = noDisponible()

    /** Crea la cuenta en Firebase Auth sin cerrar la sesión del administrador y luego su perfil. */
    suspend fun crearUsuario(input: NuevoUsuarioInput): Unit = noDisponible()
    suspend fun actualizarUsuario(input: EdicionUsuarioInput): Unit = noDisponible()

    suspend fun stock(): List<InsumoInventario> = noDisponible()
    suspend fun crearInsumo(input: NuevoInsumoInput): Unit = noDisponible()
    suspend fun actualizarInsumo(insumo: InsumoInventario): Unit = noDisponible()
    suspend fun registrarMovimiento(insumoId: String, tipo: TipoMovimientoInventario, cantidad: Double, motivo: String?): Unit =
        noDisponible()
    suspend fun historial(insumoId: String?, offset: Int, limit: Int): PaginaMovimientos = noDisponible()

    /** Conteos físicos en el estado indicado (`PENDIENTE`, `APROBADO`, `RECHAZADO`) o todos. */
    suspend fun conteos(estado: String?): List<ConteoGestion> = noDisponible()
    suspend fun aprobarConteo(id: String, motivo: String): Unit = noDisponible()
    suspend fun rechazarConteo(id: String, motivo: String): Unit = noDisponible()

    /** Reporte entre dos instantes en milisegundos (hasta exclusivo). */
    suspend fun reporte(desdeMs: Long, hastaMs: Long): ReporteGestion = noDisponible()

    suspend fun etapas(): List<EtapaConfig> = noDisponible()
    suspend fun configurarEtapa(etapa: EtapaConfig): Unit = noDisponible()
}

/** Cambio de contraseña de la cuenta actual (sección Configuración de la web). */
fun interface CuentaRepository {
    suspend fun cambiarContrasena(actual: String, nueva: String)
}

expect fun crearComunicacionRepository(): ComunicacionRepository

expect fun crearAdministracionRepository(): AdministracionRepository

expect fun crearCuentaRepository(): CuentaRepository

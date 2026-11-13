package com.elcobre.lavanderiaelcobre.data.model

/** Estados de la bandeja de cotizaciones (`SolicitudContactoEstado`). */
enum class EstadoSolicitud(val etiqueta: String) {
    RECIBIDA("Recibida"),
    EN_REVISION("En revisión"),
    ATENDIDA("Atendida"),
    ;

    companion object {
        fun desdeCodigo(codigo: String?): EstadoSolicitud = entries.firstOrNull { it.name == codigo } ?: RECIBIDA
    }
}

/** Solicitud de cotización enviada desde el formulario público de contacto. */
data class SolicitudContacto(
    val id: String,
    val nombre: String,
    val email: String,
    val telefono: String,
    val servicio: String,
    val mensaje: String,
    val estado: EstadoSolicitud,
    val creadoEn: String,
)

data class PaginaSolicitudes(val solicitudes: List<SolicitudContacto>, val total: Int)

/** Rol del catálogo `Rol`; el id hace falta para crear usuarios y avisos dirigidos. */
data class RolRef(val id: String, val nombre: String, val descripcion: String? = null)

/** Destinatarios que admite un aviso, como en `lib/avisos/modelo.ts`. */
enum class DestinatarioAviso(val etiqueta: String, val rol: String?) {
    TODOS("Todos", null),
    OPERARIO("Operarios", "operario"),
    RECEPCIONISTA("Recepción", "recepcionista"),
}

data class NuevoAvisoInput(val titulo: String, val contenido: String, val destinatario: DestinatarioAviso)

const val TITULO_AVISO_MAXIMO = 120
const val CONTENIDO_AVISO_MAXIMO = 1000

fun validarAviso(input: NuevoAvisoInput): String? = when {
    input.titulo.isBlank() || input.titulo.trim().length > TITULO_AVISO_MAXIMO ->
        "Escribe un título de hasta $TITULO_AVISO_MAXIMO caracteres."
    input.contenido.isBlank() || input.contenido.trim().length > CONTENIDO_AVISO_MAXIMO ->
        "Escribe un contenido de hasta $CONTENIDO_AVISO_MAXIMO caracteres."
    else -> null
}

/** Cuenta interna o de cliente, como la lista de `/intranet/usuarios`. */
data class UsuarioGestion(
    val id: String,
    val nombre: String,
    val apellido: String?,
    val email: String,
    val rut: String?,
    val telefono: String?,
    val activo: Boolean,
    val rol: RolRef,
    val creadoEn: String = "",
) {
    val nombreCompleto: String get() = listOfNotNull(nombre, apellido).joinToString(" ").trim()
}

/** Alta administrada de una cuenta (`createManagedUser` de la web). */
data class NuevoUsuarioInput(
    val rolId: String,
    val rolNombre: String,
    val nombre: String,
    val apellido: String,
    val rut: String,
    val telefono: String,
    val email: String,
    val contrasena: String,
    val tipoCliente: TipoClienteFicha? = null,
    val direccion: String = "",
)

/** Igual que `validateManagedUserRegistration` de la web. */
fun validarNuevoUsuario(input: NuevoUsuarioInput): String? {
    val incompleto = listOf(input.rolId, input.nombre, input.apellido, input.rut, input.email, input.contrasena).any { it.isBlank() }
    return when {
        incompleto -> "Todos los campos son obligatorios."
        !Validaciones.rutValido(input.rut) -> "El RUT ingresado no es válido."
        !Validaciones.correoValido(input.email) -> "Ingresa un correo válido."
        input.telefono.isNotBlank() && !Validaciones.telefonoValido(input.telefono) ->
            "Ingresa un celular o teléfono fijo chileno válido."
        Validaciones.problemaContrasena(input.contrasena) != null -> Validaciones.problemaContrasena(input.contrasena)
        input.rolNombre == "cliente" && input.tipoCliente == null -> "Selecciona el tipo de cliente."
        else -> null
    }
}

fun validarEdicionUsuario(rolId: String, nombre: String): String? =
    if (rolId.isBlank() || nombre.isBlank()) "El rol y el nombre son obligatorios." else null

enum class TipoMovimientoInventario(val etiqueta: String) {
    ENTRADA("Entrada"),
    SALIDA("Salida"),
}

data class MovimientoInventario(
    val id: String,
    val tipo: TipoMovimientoInventario,
    val cantidad: Double,
    val motivo: String?,
    val fecha: String,
    val insumo: String,
    val unidad: String,
    val usuario: String?,
)

data class PaginaMovimientos(val movimientos: List<MovimientoInventario>, val total: Int)

/** Conteo físico visto por administración, con quién lo registró y el stock vigente. */
data class ConteoGestion(
    val id: String,
    val insumoId: String,
    val insumo: String,
    val unidad: String,
    val cantidadContada: Double,
    val stockSistema: Double,
    val stockActual: Double,
    val revisionConteo: Int,
    val revisionActual: Int,
    val estado: String,
    val registradoPor: String,
    val creadoEn: String,
    val resueltoPor: String? = null,
    val motivoResolucion: String? = null,
) {
    val diferencia: Double get() = cantidadContada - stockSistema

    /** Si el stock cambió desde el conteo, aprobarlo ya no es seguro (el servidor lo rechaza). */
    val desactualizado: Boolean get() = revisionConteo != revisionActual
}

data class NuevoInsumoInput(val nombre: String, val unidad: String, val stockInicial: Double, val stockMinimo: Double)

fun validarInsumo(nombre: String, unidad: String, stockMinimo: Double, stockInicial: Double = 0.0): String? = when {
    nombre.isBlank() || nombre.length > 80 -> "Escribe un nombre de hasta 80 caracteres."
    unidad.isBlank() || unidad.length > 20 -> "Escribe una unidad de medida de hasta 20 caracteres."
    stockMinimo < 0 || stockInicial < 0 -> "El stock no puede ser negativo."
    else -> null
}

fun validarMovimiento(cantidad: Double?, stockActual: Double, tipo: TipoMovimientoInventario): String? = when {
    cantidad == null || cantidad <= 0 -> "Ingresa una cantidad mayor que cero."
    tipo == TipoMovimientoInventario.SALIDA && cantidad > stockActual -> "La salida supera el stock disponible."
    else -> null
}

/** Totales del panel de administración (`GetPanelProduccion`). */
data class ResumenPanel(
    val pendientes: Int,
    val enProceso: Int,
    val listas: Int,
    val conIncidencias: Int,
    val conAtraso: Int,
    val recientes: List<ComandaGestion>,
)

/** Fila de un reporte agregado (por cliente/empresa o por servicio). */
data class FilaReporte(val etiqueta: String, val comandas: Int, val prendas: Int, val monto: Double)

data class ReporteGestion(
    val desde: String,
    val hasta: String,
    val porCuenta: List<FilaReporte>,
    val porServicio: List<FilaReporte>,
    val cerradas: Int,
) {
    val totalComandas: Int get() = porCuenta.sumOf { it.comandas }
    val totalPrendas: Int get() = porCuenta.sumOf { it.prendas }
    val totalMonto: Double get() = porCuenta.sumOf { it.monto }
}

/** Periodos rápidos del reporte, en días hacia atrás desde hoy. */
enum class PeriodoReporte(val etiqueta: String, val dias: Int) {
    SEMANA("7 días", 7),
    MES("30 días", 30),
    TRIMESTRE("90 días", 90),
    ANIO("12 meses", 365),
}

/** Etapa configurable del flujo (`EtapaProduccion`), editable por administración. */
data class EtapaConfig(val id: String, val nombre: String, val orden: Int, val descripcion: String?, val tiempoEstimadoMin: Int?)

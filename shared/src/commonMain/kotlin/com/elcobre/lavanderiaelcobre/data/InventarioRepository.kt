package com.elcobre.lavanderiaelcobre.data

data class InsumoInventario(
    val id: String,
    val codigoQr: String,
    val nombre: String,
    val unidad: String,
    val stockActual: Double,
    val stockMinimo: Double,
    val activo: Boolean,
    /** Versión del saldo en Firebase: un conteo solo se acepta si coincide con la vigente. */
    val revisionStock: Int = 0,
) {
    val estado: String get() = when {
        !activo -> "Inactivo"
        stockMinimo > 0 && stockActual <= stockMinimo * 0.5 -> "Crítico"
        stockMinimo > 0 && stockActual <= stockMinimo -> "Bajo"
        else -> "OK"
    }
}

/** Estado de revisión de un conteo físico, igual que `EstadoConteoInventario` de la web. */
enum class EstadoRevisionConteo(val etiqueta: String) {
    PENDIENTE("Pendiente de aprobación"),
    APROBADO("Aprobado"),
    RECHAZADO("Rechazado"),
}

/**
 * Conteo físico enviado por el operario (RF-IN10). Se guarda en Firebase como
 * pendiente; administración lo aprueba (y entonces ajusta el stock) o lo rechaza
 * desde la intranet, como en `ConteosInventario.tsx` de la web.
 */
data class ConteoFisico(
    val insumoId: String,
    val cantidadContada: Double,
    val stockSistema: Double,
    val id: String = "",
    val estado: EstadoRevisionConteo = EstadoRevisionConteo.PENDIENTE,
    val creadoEn: String = "",
    val motivoResolucion: String? = null,
) {
    val diferencia: Double get() = cantidadContada - stockSistema
    val coincide: Boolean get() = kotlin.math.abs(diferencia) < 0.001
    val estadoConteo: String
        get() = when {
            coincide -> "Cuadrado"
            diferencia < 0 -> "Faltante"
            else -> "Sobrante"
        }
}

interface InventarioRepository {
    suspend fun catalogo(): List<InsumoInventario>
    suspend fun porQr(codigo: String): InsumoInventario?

    /** Conteos enviados por el operario autenticado, del más reciente al más antiguo. */
    suspend fun misConteos(): List<ConteoFisico> = emptyList()

    /** Registra un conteo pendiente de aprobación (`RegistrarConteoInventario`). */
    suspend fun registrarConteo(insumo: InsumoInventario, cantidad: Double): Unit =
        throw OperacionComandaException("El registro de conteos no está disponible.")
}

class FirebaseInventarioRepository : InventarioRepository {
    override suspend fun catalogo(): List<InsumoInventario> = catalogoInsumosFirebase()
    override suspend fun porQr(codigo: String): InsumoInventario? = insumoPorQrFirebase(codigo)
    override suspend fun misConteos(): List<ConteoFisico> = misConteosInventarioFirebase()
    override suspend fun registrarConteo(insumo: InsumoInventario, cantidad: Double) =
        registrarConteoInventarioFirebase(insumo.id, cantidad, insumo.revisionStock)
}

internal expect suspend fun catalogoInsumosFirebase(): List<InsumoInventario>
internal expect suspend fun insumoPorQrFirebase(codigo: String): InsumoInventario?
internal expect suspend fun misConteosInventarioFirebase(): List<ConteoFisico>
internal expect suspend fun registrarConteoInventarioFirebase(insumoId: String, cantidad: Double, revisionStock: Int)

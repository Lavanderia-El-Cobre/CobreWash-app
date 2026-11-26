package com.elcobre.lavanderiaelcobre.data

import kotlinx.serialization.Serializable

import com.elcobre.lavanderiaelcobre.data.offline.AccionesSinConexion
import com.elcobre.lavanderiaelcobre.data.offline.TipoAccion
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid
import com.elcobre.lavanderiaelcobre.data.offline.CacheLectura
import kotlinx.serialization.builtins.ListSerializer

/**
 * Insumo del catálogo de inventario. Es `@Serializable` para persistirse en [CacheLectura]
 * y permitir consultas y escaneo de QR incluso sin conexión.
 */
@Serializable
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
    /** Clasificación visual del stock: `Crítico` (<= 50 % del mínimo), `Bajo` (<= mínimo) u `OK`. */
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

    /** Tolerancia de `0.001` para evitar falsos descuadres por aritmética de punto flotante. */
    val coincide: Boolean get() = kotlin.math.abs(diferencia) < 0.001

    /** Resumen legible de la desviación respecto al stock del sistema. */
    val estadoConteo: String
        get() = when {
            coincide -> "Cuadrado"
            diferencia < 0 -> "Faltante"
            else -> "Sobrante"
        }
}

/** Contrato de acceso al catálogo de insumos y registro de conteos físicos de planta. */
interface InventarioRepository {
    suspend fun catalogo(): List<InsumoInventario>
    suspend fun porQr(codigo: String): InsumoInventario?

    /** Conteos enviados por el operario autenticado, del más reciente al más antiguo. */
    suspend fun misConteos(): List<ConteoFisico> = emptyList()

    /** Registra un conteo pendiente de aprobación (`RegistrarConteoInventario`). */
    suspend fun registrarConteo(insumo: InsumoInventario, cantidad: Double): Unit =
        throw OperacionComandaException("El registro de conteos no está disponible.")
}

/**
 * Implementación sobre Firebase con respaldo offline: [catalogo] y [porQr] usan [CacheLectura],
 * y [registrarConteo] se encola en [AccionesSinConexion] con UUID idempotente y [InsumoInventario.revisionStock].
 */
class FirebaseInventarioRepository : InventarioRepository {
    override suspend fun catalogo(): List<InsumoInventario> =
        CacheLectura.leer("insumos", ListSerializer(InsumoInventario.serializer())) { catalogoInsumosFirebase() }

    /** Sin conexión, el QR del insumo se resuelve con el último catálogo guardado. */
    override suspend fun porQr(codigo: String): InsumoInventario? = try {
        insumoPorQrFirebase(codigo)
    } catch (error: OperacionComandaException) {
        CacheLectura.guardado("insumos", ListSerializer(InsumoInventario.serializer()))
            ?.firstOrNull { it.codigoQr.equals(codigo.trim(), ignoreCase = true) } ?: throw error
    }
    override suspend fun misConteos(): List<ConteoFisico> = misConteosInventarioFirebase()
    @OptIn(ExperimentalUuidApi::class)
    override suspend fun registrarConteo(insumo: InsumoInventario, cantidad: Double) = AccionesSinConexion.ejecutar(
        TipoAccion.CONTEO,
        entidad = insumo.id,
        datos = mapOf(
            "conteoId" to Uuid.random().toString(), "insumoId" to insumo.id, "cantidad" to "$cantidad",
            "revisionStock" to "${insumo.revisionStock}",
        ),
        descripcion = "Conteo · ${insumo.nombre}",
    )
}

/** Puentes `expect`/`actual` por plataforma contra Firebase Data Connect para inventario. */
internal expect suspend fun catalogoInsumosFirebase(): List<InsumoInventario>
internal expect suspend fun insumoPorQrFirebase(codigo: String): InsumoInventario?
internal expect suspend fun misConteosInventarioFirebase(): List<ConteoFisico>
internal expect suspend fun registrarConteoInventarioFirebase(insumoId: String, cantidad: Double, revisionStock: Int)

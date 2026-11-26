package com.elcobre.lavanderiaelcobre.data

import com.elcobre.lavanderiaelcobre.data.model.ComandaEntrega
import com.elcobre.lavanderiaelcobre.data.model.DisponibilidadVehiculo
import com.elcobre.lavanderiaelcobre.data.model.EntregaRegistrada
import com.elcobre.lavanderiaelcobre.data.model.EntregasReparto
import com.elcobre.lavanderiaelcobre.data.model.EstadoCondicionVehiculo
import com.elcobre.lavanderiaelcobre.data.model.EstadoIncidencia
import com.elcobre.lavanderiaelcobre.data.model.EstadoSalida
import com.elcobre.lavanderiaelcobre.data.model.InspeccionInput
import com.elcobre.lavanderiaelcobre.data.model.InspeccionReparto
import com.elcobre.lavanderiaelcobre.data.model.PanelReparto
import com.elcobre.lavanderiaelcobre.data.model.ProblemaEntrega
import com.elcobre.lavanderiaelcobre.data.model.SalidaReparto
import com.elcobre.lavanderiaelcobre.data.model.TipoClienteFicha
import com.elcobre.lavanderiaelcobre.data.model.VehiculoReparto
import com.elcobre.lavanderiaelcobre.data.offline.AccionesSinConexion
import com.elcobre.lavanderiaelcobre.data.offline.TipoAccion
import com.elcobre.lavanderiaelcobre.dataconnect.EstadoVehiculo
import com.elcobre.lavanderiaelcobre.dataconnect.ExampleConnector
import com.elcobre.lavanderiaelcobre.dataconnect.GetComandasParaEntregaQuery
import com.elcobre.lavanderiaelcobre.dataconnect.GetPanelRepartidorQuery
import com.elcobre.lavanderiaelcobre.dataconnect.MomentoInspeccion
import com.elcobre.lavanderiaelcobre.dataconnect.execute
import com.elcobre.lavanderiaelcobre.dataconnect.instance
import com.elcobre.lavanderiaelcobre.dataconnect.ref
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.dataconnect.QueryRef
import java.util.UUID
import java.util.concurrent.ConcurrentHashMap
import kotlin.math.roundToInt
import com.elcobre.lavanderiaelcobre.data.offline.CacheLectura

/** Implementación Android de [RepartoRepository] sobre Data Connect, caché de lectura y cola offline. */
actual fun crearRepartoRepository(): RepartoRepository = FirebaseRepartoRepository()

/** Política directa al servidor antes de actualizar la copia local de [CacheLectura]. */
private val SERVIDOR = QueryRef.FetchPolicy.SERVER_ONLY

private fun estadoDc(estado: EstadoCondicionVehiculo): EstadoVehiculo = EstadoVehiculo.valueOf(estado.codigo)

private fun GetPanelRepartidorQuery.Data.MisSalidasItem.InspeccionesItem.aInspeccion() = InspeccionReparto(
    id = id.toString(),
    estado = EstadoCondicionVehiculo.desdeCodigo(estadoVehiculo.stringValue),
    kilometraje = kilometraje.roundToInt(),
    observaciones = observaciones,
    registradoEn = fechaHora(registradoEn),
    fotos = fotos.size,
)

private fun GetPanelRepartidorQuery.Data.MisSalidasItem.aSalida() = SalidaReparto(
    id = id.toString(),
    estado = EstadoSalida.desdeCodigo(estado.stringValue),
    vehiculoId = vehiculo.id.toString(),
    patente = vehiculo.patente,
    vehiculo = listOf(vehiculo.marca, vehiculo.modelo).filter { it.isNotBlank() }.distinct().joinToString(" "),
    creadoEn = fechaHora(creadoEn),
    fechaSalida = fechaSalida?.let(::fechaHora),
    fechaRetorno = fechaRetorno?.let(::fechaHora),
    observaciones = observaciones,
    antes = inspecciones.firstOrNull { it.momento.value == MomentoInspeccion.ANTES }?.aInspeccion(),
    despues = inspecciones.firstOrNull { it.momento.value == MomentoInspeccion.DESPUES }?.aInspeccion(),
)

/** Ordena como la web: primero el vehículo propio, luego los disponibles y al final los que otra persona tiene en ruta. */
private fun GetPanelRepartidorQuery.Data.aPanel(uid: String?): PanelReparto = PanelReparto(
    vehiculos = vehiculos.map { v ->
        val enCurso = v.enCurso.firstOrNull()
        VehiculoReparto(
            id = v.id.toString(),
            patente = v.patente,
            vehiculo = listOf(v.marca, v.modelo).filter { it.isNotBlank() }.distinct().joinToString(" "),
            descripcion = v.descripcion,
            disponibilidad = when {
                enCurso == null -> DisponibilidadVehiculo.DISPONIBLE
                enCurso.repartidor.id == uid -> DisponibilidadVehiculo.CONTIGO
                else -> DisponibilidadVehiculo.EN_RUTA
            },
            // El odómetro no retrocede: el mayor kilometraje de sus últimas salidas.
            kilometrajeActual = v.recientes.flatMap { it.inspecciones }.maxOfOrNull { it.kilometraje }?.roundToInt() ?: 0,
            conductorActual = enCurso?.repartidor?.let { nombreCompleto(it.nombre, it.apellido) },
        )
    }.sortedBy { it.disponibilidad.ordinal },
    salidas = misSalidas.map { it.aSalida() },
)

private fun GetComandasParaEntregaQuery.Data.ComandasItem.aEntrega(): ComandaEntrega {
    val porTipo = prendas.groupBy { it.tipoPrenda.nombre }.mapValues { (_, l) -> l.sumOf { it.cantidad } }
    val total = prendas.sumOf { it.cantidad }
    val resumen = if (total == 0) {
        "Sin prendas registradas"
    } else {
        "$total prenda${if (total == 1) "" else "s"} · " + porTipo.entries.joinToString(", ") { "${it.key} ×${it.value}" }
    }
    return ComandaEntrega(
        id = id.toString(),
        numero = numeroComanda,
        cliente = cliente.nombre,
        tipoCliente = TipoClienteFicha.desdeCodigo(cliente.tipoCliente.stringValue),
        telefono = cliente.telefono,
        direccion = cliente.direccion,
        empresa = empresa,
        proyecto = proyecto,
        observaciones = observaciones,
        fechaEntregaEstimada = fechaEntregaEstimada?.let(::fechaCorta),
        prendas = resumen,
        problemas = problemas.map {
            ProblemaEntrega(
                id = it.id.toString(),
                motivo = it.motivo,
                descripcion = it.descripcion,
                estado = EstadoIncidencia.desdeCodigo(it.estado.stringValue),
                fecha = fechaHora(it.fecha),
                reportadaPor = nombreCompleto(it.reportadaPor.nombre, it.reportadaPor.apellido),
            )
        },
    )
}

/** Programa una salida de vehículo para un repartidor directamente en Data Connect. */
internal actual suspend fun programarSalidaFirebase(vehiculoId: String, repartidorId: String, observaciones: String?) {
    solicitud {
        ExampleConnector.instance.crearSalidaVehiculo.execute(UUID.fromString(vehiculoId), repartidorId) {
            this.observaciones = observaciones?.trim()?.ifEmpty { null }?.take(300)
        }
    }
}

/**
 * Sección del repartidor. Salida y regreso reutilizan el registro de flota de
 * administración (`registrarSalidaFirebase`/`registrarRetornoFirebase`): ids generados
 * en el dispositivo y consulta previa, así un reintento no duplica el movimiento.
 */
private class FirebaseRepartoRepository : RepartoRepository {
    private val dc get() = ExampleConnector.instance
    private val inspeccionesPendientes = ConcurrentHashMap<String, String>()

    override fun descartarReintentos() {
        inspeccionesPendientes.clear()
        descartarReintentosFlotaFirebase()
    }

    override suspend fun panel(): PanelReparto = CacheLectura.leer("panel-reparto", PanelReparto.serializer()) {
        solicitud {
            dc.getPanelRepartidor.ref { limit = 30 }.execute(SERVIDOR).data.aPanel(FirebaseAuth.getInstance().currentUser?.uid)
        }
    }

    override suspend fun registrarSalida(input: SalidaFlotaInput): String {
        val uid = FirebaseAuth.getInstance().currentUser?.uid ?: throw OperacionComandaException("Inicia sesión nuevamente.")
        // El servidor exige que la salida sea del propio repartidor.
        return registrarSalidaFirebase(input.copy(repartidorId = uid))
    }

    override suspend fun registrarRetorno(input: RetornoFlotaInput) = registrarRetornoFirebase(input)

    /**
     * Inicia una salida ya programada: registra la inspección previa, sube las fotografías
     * y cambia el estado de la salida. Conserva el ID de inspección en [inspeccionesPendientes]
     * para que un reintento tras fallo al subir fotos no cree inspecciones duplicadas.
     */
    override suspend fun iniciarProgramada(salida: SalidaReparto, input: InspeccionInput): Unit = solicitud {
        validarArchivosEvidencia(input.fotos)
        val clave = "${input.salidaId}|ANTES"
        val inspeccionId = salida.antes?.id ?: inspeccionesPendientes[clave] ?: dc.registrarInspeccionAntes.execute(
            UUID.fromString(input.salidaId), estadoDc(input.estado), input.kilometraje.toDouble(),
        ) { observaciones = input.observaciones?.trim()?.ifEmpty { null }?.take(500) }
            .data.inspeccionVehiculo_insert.id.toString()
        inspeccionesPendientes[clave] = inspeccionId
        try {
            for ((indice, foto) in input.fotos.withIndex()) subirFotoInspeccion(inspeccionId, foto, indice + 1)
            eliminarCapturasConfirmadas(input.fotos.map { it.uri })
        } catch (error: OperacionComandaException) {
            throw OperacionComandaException(
                "La inspección ya se guardó. Hay fotografías pendientes: reintenta para subirlas. " + error.message, error,
            )
        }
        dc.iniciarSalidaVehiculo.execute(UUID.fromString(input.salidaId))
        inspeccionesPendientes.remove(clave)
    }

    /** Número visible de cada comanda listada, para describir las acciones en la cola. */
    private val numeros = ConcurrentHashMap<String, String>()

    override suspend fun entregas(): EntregasReparto = CacheLectura.leer("entregas", EntregasReparto.serializer()) { entregasRemotas() }

    private suspend fun entregasRemotas(): EntregasReparto = solicitud {
        val data = dc.getComandasParaEntrega.ref { limit = 50 }.execute(SERVIDOR).data
        data.comandas.forEach { numeros[it.id.toString()] = it.numeroComanda }
        EntregasReparto(
            comandas = data.comandas.map { it.aEntrega() },
            misEntregas = data.misEntregas.map {
                EntregaRegistrada(it.id.toString(), fechaHora(it.fecha), it.motivo, it.comanda.numeroComanda, it.comanda.cliente.nombre)
            },
        )
    }

    override suspend fun entregar(comandaId: String, detalle: String?) = AccionesSinConexion.ejecutar(
        TipoAccion.ENTREGAR,
        entidad = comandaId,
        datos = mapOf("comandaId" to comandaId, "detalle" to detalle),
        descripcion = "Entrega · ${numeros[comandaId] ?: "comanda"}",
    )

    override suspend fun reportarProblema(comandaId: String, motivo: String, detalle: String?) = AccionesSinConexion.ejecutar(
        TipoAccion.REPORTAR_PROBLEMA,
        entidad = comandaId,
        datos = mapOf("comandaId" to comandaId, "motivo" to motivo, "detalle" to detalle),
        descripcion = "Problema de entrega ($motivo) · ${numeros[comandaId] ?: "comanda"}",
    )
}

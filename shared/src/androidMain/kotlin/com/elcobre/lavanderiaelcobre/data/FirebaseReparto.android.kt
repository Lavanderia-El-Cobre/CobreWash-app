package com.elcobre.lavanderiaelcobre.data

import com.elcobre.lavanderiaelcobre.data.model.EstadoCondicionVehiculo
import com.elcobre.lavanderiaelcobre.data.model.EstadoSalida
import com.elcobre.lavanderiaelcobre.data.model.InspeccionInput
import com.elcobre.lavanderiaelcobre.data.model.InspeccionReparto
import com.elcobre.lavanderiaelcobre.data.model.SalidaReparto
import com.elcobre.lavanderiaelcobre.data.model.Vehiculo
import com.elcobre.lavanderiaelcobre.dataconnect.EstadoVehiculo
import com.elcobre.lavanderiaelcobre.dataconnect.ExampleConnector
import com.elcobre.lavanderiaelcobre.dataconnect.GetMisSalidasVehiculoQuery
import com.elcobre.lavanderiaelcobre.dataconnect.MomentoInspeccion
import com.elcobre.lavanderiaelcobre.dataconnect.execute
import com.elcobre.lavanderiaelcobre.dataconnect.instance
import com.elcobre.lavanderiaelcobre.dataconnect.ref
import com.google.firebase.dataconnect.QueryRef
import java.util.UUID
import java.util.concurrent.ConcurrentHashMap
import kotlin.math.roundToInt

actual fun crearRepartoRepository(): RepartoRepository = FirebaseRepartoRepository()

private fun estadoDc(estado: EstadoCondicionVehiculo): EstadoVehiculo = EstadoVehiculo.valueOf(estado.codigo)

private fun GetMisSalidasVehiculoQuery.Data.SalidaVehiculosItem.InspeccionVehiculosOnSalidaItem.aInspeccion() = InspeccionReparto(
    id = id.toString(),
    estado = EstadoCondicionVehiculo.desdeCodigo(estadoVehiculo.stringValue),
    kilometraje = kilometraje.roundToInt(),
    observaciones = observaciones,
    registradoEn = fechaHora(registradoEn),
    fotos = fotoInspeccionVehiculos_on_inspeccion.size,
)

internal actual suspend fun programarSalidaFirebase(vehiculoId: String, repartidorId: String, observaciones: String?) {
    solicitud {
        ExampleConnector.instance.crearSalidaVehiculo.execute(UUID.fromString(vehiculoId), repartidorId) {
            this.observaciones = observaciones?.trim()?.ifEmpty { null }?.take(300)
        }
    }
}

/**
 * Salidas del repartidor. Las inspecciones se registran primero y las fotos después;
 * si la subida de una foto falla, el reintento reutiliza la inspección ya guardada
 * (se identifica por salida y momento) en vez de duplicarla.
 */
private class FirebaseRepartoRepository : RepartoRepository {
    private val dc get() = ExampleConnector.instance
    private val inspeccionesPendientes = ConcurrentHashMap<String, String>()

    override fun descartarReintentos() = inspeccionesPendientes.clear()

    override suspend fun misSalidas(): List<SalidaReparto> = solicitud {
        dc.getMisSalidasVehiculo.ref().execute(QueryRef.FetchPolicy.SERVER_ONLY).data.salidaVehiculos.map { s ->
            SalidaReparto(
                id = s.id.toString(),
                estado = EstadoSalida.desdeCodigo(s.estado.stringValue),
                vehiculoId = s.vehiculo.id.toString(),
                patente = s.vehiculo.patente,
                vehiculo = listOf(s.vehiculo.marca, s.vehiculo.modelo).filter { it.isNotBlank() }.distinct().joinToString(" "),
                creadoEn = fechaHora(s.creadoEn),
                fechaSalida = s.fechaSalida?.let(::fechaHora),
                fechaRetorno = s.fechaRetorno?.let(::fechaHora),
                observaciones = s.observaciones,
                antes = s.inspeccionVehiculos_on_salida.firstOrNull { it.momento.value == MomentoInspeccion.ANTES }?.aInspeccion(),
                despues = s.inspeccionVehiculos_on_salida.firstOrNull { it.momento.value == MomentoInspeccion.DESPUES }?.aInspeccion(),
            )
        }
    }

    override suspend fun vehiculos(): List<Vehiculo> = solicitud {
        dc.getVehiculos.ref().execute(QueryRef.FetchPolicy.SERVER_ONLY).data.vehiculos.map {
            Vehiculo(
                id = it.id.toString(), patente = it.patente, modelo = it.modelo, kilometrajeActual = 0,
                marca = it.marca, anio = it.anio, descripcion = it.descripcion, activo = it.activo,
            )
        }
    }

    override suspend fun iniciarSalida(salida: SalidaReparto, input: InspeccionInput): Unit = solicitud {
        validarArchivosEvidencia(input.fotos)
        val clave = "${input.salidaId}|ANTES"
        val inspeccionId = salida.antes?.id ?: inspeccionesPendientes[clave] ?: dc.registrarInspeccionAntes.execute(
            UUID.fromString(input.salidaId), estadoDc(input.estado), input.kilometraje.toDouble(),
        ) { observaciones = input.observaciones?.trim()?.ifEmpty { null }?.take(500) }
            .data.inspeccionVehiculo_insert.id.toString()
        inspeccionesPendientes[clave] = inspeccionId
        subirFotos(inspeccionId, input)
        dc.iniciarSalidaVehiculo.execute(UUID.fromString(input.salidaId))
        inspeccionesPendientes.remove(clave)
    }

    override suspend fun registrarRetorno(input: InspeccionInput): Unit = solicitud {
        validarArchivosEvidencia(input.fotos)
        val clave = "${input.salidaId}|DESPUES"
        val inspeccionId = inspeccionesPendientes[clave] ?: dc.registrarInspeccionDespues.execute(
            UUID.fromString(input.salidaId), estadoDc(input.estado), input.kilometraje.toDouble(),
        ) { observaciones = input.observaciones?.trim()?.ifEmpty { null }?.take(500) }
            .data.inspeccionVehiculo_insert.id.toString()
        inspeccionesPendientes[clave] = inspeccionId
        subirFotos(inspeccionId, input)
        inspeccionesPendientes.remove(clave)
    }

    private suspend fun subirFotos(inspeccionId: String, input: InspeccionInput) {
        try {
            for ((indice, foto) in input.fotos.withIndex()) subirFotoInspeccion(inspeccionId, foto, indice + 1)
            eliminarCapturasConfirmadas(input.fotos.map { it.uri })
        } catch (error: OperacionComandaException) {
            throw OperacionComandaException(
                "La inspección ya se guardó. Hay fotografías pendientes: reintenta para subirlas. " + error.message, error,
            )
        }
    }
}

package com.elcobre.lavanderiaelcobre.data

import com.elcobre.lavanderiaelcobre.data.model.ChequeoVehiculo
import com.elcobre.lavanderiaelcobre.data.model.ConductorFlota
import com.elcobre.lavanderiaelcobre.data.model.EstadoCondicionVehiculo
import com.elcobre.lavanderiaelcobre.data.model.FotoChequeo
import com.elcobre.lavanderiaelcobre.data.model.Vehiculo
import com.elcobre.lavanderiaelcobre.data.model.esRolConductorORepartidor
import com.elcobre.lavanderiaelcobre.data.model.extraerMarcaYModelo
import com.elcobre.lavanderiaelcobre.data.model.normalizarPatenteChilena
import com.elcobre.lavanderiaelcobre.data.offline.AccionesSinConexion
import com.elcobre.lavanderiaelcobre.data.offline.FotoPendiente
import com.elcobre.lavanderiaelcobre.data.offline.TipoAccion
import com.elcobre.lavanderiaelcobre.dataconnect.EstadoVehiculo
import com.elcobre.lavanderiaelcobre.dataconnect.ExampleConnector
import com.elcobre.lavanderiaelcobre.dataconnect.GetFlotaVehiculosQuery
import com.elcobre.lavanderiaelcobre.dataconnect.MomentoInspeccion
import com.elcobre.lavanderiaelcobre.dataconnect.SalidaVehiculoEstado
import com.elcobre.lavanderiaelcobre.dataconnect.execute
import com.elcobre.lavanderiaelcobre.dataconnect.instance
import com.elcobre.lavanderiaelcobre.dataconnect.ref
import com.google.firebase.dataconnect.QueryRef
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.TimeZone
import java.util.UUID
import kotlin.math.roundToInt

private const val MAX_FOTOS_INSPECCION = 20

/**
 * Sin estado en memoria que descartar en Android: los reintentos de salidas y regresos
 * viven en la cola sin conexión persistente en disco.
 */
internal actual fun descartarReintentosFlotaFirebase() = Unit

private fun mapearCondicionADataConnect(estado: EstadoCondicionVehiculo): EstadoVehiculo = when (estado) {
    EstadoCondicionVehiculo.APTO -> EstadoVehiculo.APTO
    EstadoCondicionVehiculo.CON_OBSERVACIONES -> EstadoVehiculo.CON_OBSERVACIONES
    EstadoCondicionVehiculo.NO_APTO -> EstadoVehiculo.NO_APTO
}

private fun mapearFotosInspeccion(
    inspeccion: GetFlotaVehiculosQuery.Data.SalidasItem.InspeccionesItem?,
): List<FotoChequeo> = inspeccion?.fotos.orEmpty().map {
    FotoChequeo(
        id = it.id.toString(),
        uri = it.rutaStorage,
        descripcion = it.descripcion,
        orden = it.orden,
    )
}

private fun mapearSalidaAChequeo(
    salida: GetFlotaVehiculosQuery.Data.SalidasItem,
    formatoFecha: SimpleDateFormat,
    formatoHora: SimpleDateFormat,
): ChequeoVehiculo {
    val antes = salida.inspecciones.firstOrNull { it.momento.value == MomentoInspeccion.ANTES }
    val despues = salida.inspecciones.firstOrNull { it.momento.value == MomentoInspeccion.DESPUES }
    val finalizada = salida.estado.value == SalidaVehiculoEstado.FINALIZADA
    val kmSalida = antes?.kilometraje?.roundToInt() ?: 0
    val kmEntrada = if (finalizada) (despues?.kilometraje?.roundToInt() ?: kmSalida) else null
    val instanteBase = salida.fechaSalida ?: antes?.registradoEn ?: salida.creadoEn
    val instanteRetorno = salida.fechaRetorno ?: despues?.registradoEn
    val responsable = listOfNotNull(salida.repartidor.nombre, salida.repartidor.apellido)
        .joinToString(" ")
        .trim()
        .ifEmpty { salida.repartidor.email }

    return ChequeoVehiculo(
        id = salida.id.toString(),
        vehiculoId = salida.vehiculo.id.toString(),
        fecha = formatoFecha.format(instanteBase.toDate()),
        responsable = responsable,
        repartidorId = salida.repartidor.id,
        estadoSalida = EstadoCondicionVehiculo.desdeCodigo(antes?.estadoVehiculo?.value?.name),
        estadoEntrada = despues?.let { EstadoCondicionVehiculo.desdeCodigo(it.estadoVehiculo.value?.name) },
        kmSalida = kmSalida,
        fotosSalida = mapearFotosInspeccion(antes),
        observacionesSalida = antes?.observaciones ?: salida.observaciones,
        horaSalida = formatoHora.format(instanteBase.toDate()),
        kmEntrada = kmEntrada,
        fotosEntrada = mapearFotosInspeccion(despues),
        observacionesEntrada = despues?.observaciones ?: salida.observaciones.takeIf { finalizada },
        horaEntrada = instanteRetorno?.let { formatoHora.format(it.toDate()) },
    )
}

/**
 * Consulta vehículos, conductores y salidas de flota paginando de a 200 registros (`SERVER_ONLY`).
 * Excluye salidas canceladas y calcula el odómetro actual de cada vehículo a partir del
 * mayor kilometraje registrado en sus inspecciones vigentes.
 */
internal actual suspend fun consultarFlotaFirebase(): EstadoFlotaRemota = solicitud {
    var data: GetFlotaVehiculosQuery.Data? = null
    val salidas = mutableListOf<GetFlotaVehiculosQuery.Data.SalidasItem>()
    var offset = 0
    do {
        val pagina = ExampleConnector.instance.getFlotaVehiculos.ref { limit = 200; this.offset = offset }
            .execute(QueryRef.FetchPolicy.SERVER_ONLY).data
        data = pagina
        salidas.addAll(pagina.salidas)
        offset += pagina.salidas.size
    } while (pagina.salidas.size == 200)
    val completa = requireNotNull(data).copy(salidas = salidas.distinctBy { it.id })
    val zona = TimeZone.getTimeZone("America/Santiago")
    val locale = Locale.forLanguageTag("es-CL")
    val formatoFecha = SimpleDateFormat("dd/MM/yyyy", locale).apply { timeZone = zona }
    val formatoHora = SimpleDateFormat("HH:mm", locale).apply { timeZone = zona }

    val salidasVigentes = completa.salidas.filter { it.estado.value != SalidaVehiculoEstado.CANCELADA }
    val maxKmPorVehiculo = salidasVigentes.groupBy { it.vehiculo.id.toString() }
        .mapValues { (_, viajes) ->
            viajes.flatMap { it.inspecciones }.maxOfOrNull { it.kilometraje.roundToInt() } ?: 0
        }

    val vehiculos = completa.vehiculos.map { item ->
        val id = item.id.toString()
        Vehiculo(
            id = id,
            patente = item.patente,
            marca = item.marca,
            modelo = item.modelo,
            anio = item.anio,
            descripcion = item.descripcion,
            activo = item.activo,
            kilometrajeActual = maxKmPorVehiculo[id] ?: 0,
        )
    }

    val conductores = completa.conductores
        .filter { esRolConductorORepartidor(it.rol.nombre) }
        .map { item ->
            val nombreCompleto = listOfNotNull(item.nombre, item.apellido)
                .joinToString(" ")
                .trim()
                .ifEmpty { item.email }
            ConductorFlota(
                id = item.id,
                nombreCompleto = nombreCompleto,
                rut = item.rut,
                rol = item.rol.nombre,
            )
        }

    EstadoFlotaRemota(
        vehiculos = vehiculos,
        conductores = conductores,
        chequeos = salidasVigentes.map { mapearSalidaAChequeo(it, formatoFecha, formatoHora) },
    )
}

/** Normaliza patente chilena, marca y modelo antes de registrar un vehículo en Data Connect. */
internal actual suspend fun registrarVehiculoFirebase(input: NuevoVehiculoInput): String = solicitud {
    val patente = normalizarPatenteChilena(input.patente)
    val (marca, modelo) = extraerMarcaYModelo(input.marca, input.modelo)
    val resultado = ExampleConnector.instance.registrarVehiculoFlota.execute(
        patente = patente,
        marca = marca,
        modelo = modelo,
    ) {
        anio = input.anio
        descripcion = input.descripcion?.trim()?.takeIf { it.isNotEmpty() }
    }
    resultado.data.vehiculo_insert.id.toString()
}

/** Actualiza los datos normalizados y el estado activo de un vehículo existente en Data Connect. */
internal actual suspend fun editarVehiculoFirebase(input: EditarVehiculoInput): Unit = solicitud {
    val patente = normalizarPatenteChilena(input.patente)
    val (marca, modelo) = extraerMarcaYModelo(input.marca, input.modelo)
    ExampleConnector.instance.editarVehiculoFlota.execute(
        id = UUID.fromString(input.id),
        patente = patente,
        marca = marca,
        modelo = modelo,
        activo = input.activo,
    ) {
        anio = input.anio
        descripcion = input.descripcion?.trim()?.takeIf { it.isNotEmpty() }
    }
}


private fun fotosPendientes(fotos: List<FotoChequeo>) = fotos.map { FotoPendiente(it.id, it.uri, it.descripcion) }

/**
 * Salida con inspección y fotos. Pasa por la cola sin conexión: la salida y la inspección
 * llevan ids generados aquí y la operación su propio id, así un reintento no la duplica.
 * Primero se guarda el movimiento y después se suben las fotos (guardadas en el teléfono).
 */
internal actual suspend fun registrarSalidaFirebase(input: SalidaFlotaInput): String {
    validarArchivosEvidencia(input.fotos)
    if (input.fotos.size > MAX_FOTOS_INSPECCION) throw OperacionComandaException("Máximo 20 fotografías por inspección.")
    val salidaId = UUID.randomUUID().toString()
    val obs = input.observaciones?.trim()?.takeIf { it.isNotEmpty() }
    AccionesSinConexion.ejecutar(
        TipoAccion.SALIDA_FLOTA,
        entidad = salidaId,
        datos = mapOf(
            "vehiculoId" to input.vehiculoId, "repartidorId" to input.repartidorId, "kilometraje" to "${input.kilometraje}",
            "estadoVehiculo" to mapearCondicionADataConnect(input.estadoVehiculo).name, "salidaId" to salidaId,
            "inspeccionId" to UUID.randomUUID().toString(), "observaciones" to obs?.take(300), "observacionesInspeccion" to obs?.take(500),
        ),
        descripcion = "Salida de vehículo · ${input.kilometraje} km",
        fotos = fotosPendientes(input.fotos),
    )
    return salidaId
}

/** Regreso con inspección y fotos, por la cola sin conexión; va después de su salida. */
internal actual suspend fun registrarRetornoFirebase(input: RetornoFlotaInput) {
    validarArchivosEvidencia(input.fotos)
    if (input.fotos.size > MAX_FOTOS_INSPECCION) throw OperacionComandaException("Máximo 20 fotografías por inspección.")
    val obs = input.observaciones?.trim()?.takeIf { it.isNotEmpty() }
    AccionesSinConexion.ejecutar(
        TipoAccion.RETORNO_FLOTA,
        entidad = input.salidaId,
        datos = mapOf(
            "salidaId" to input.salidaId, "kilometraje" to "${input.kilometraje}",
            "estadoVehiculo" to mapearCondicionADataConnect(input.estadoVehiculo).name, "inspeccionId" to UUID.randomUUID().toString(),
            "observacionesRetorno" to obs?.take(300), "observacionesInspeccion" to obs?.take(500),
        ),
        descripcion = "Regreso de vehículo · ${input.kilometraje} km",
        fotos = fotosPendientes(input.fotos),
    )
}

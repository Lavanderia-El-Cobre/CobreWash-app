package com.elcobre.lavanderiaelcobre.data.offline

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.os.SystemClock
import android.provider.Settings
import androidx.work.BackoffPolicy
import androidx.work.Constraints
import androidx.work.CoroutineWorker
import androidx.work.ExistingWorkPolicy
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import com.elcobre.lavanderiaelcobre.data.FalloSubidaFoto
import com.elcobre.lavanderiaelcobre.data.ORIGEN_WEB
import com.elcobre.lavanderiaelcobre.data.eliminarCapturasConfirmadas
import com.elcobre.lavanderiaelcobre.data.model.FotoChequeo
import com.elcobre.lavanderiaelcobre.data.subirFotoInspeccion
import com.elcobre.lavanderiaelcobre.dataconnect.ComandaEstado
import com.elcobre.lavanderiaelcobre.dataconnect.EstadoVehiculo
import com.elcobre.lavanderiaelcobre.dataconnect.ExampleConnector
import com.elcobre.lavanderiaelcobre.dataconnect.execute
import com.elcobre.lavanderiaelcobre.dataconnect.instance
import com.google.firebase.FirebaseNetworkException
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.dataconnect.DataConnectException
import io.grpc.Status
import io.grpc.StatusException
import io.grpc.StatusRuntimeException
import java.io.File
import java.io.IOException
import java.net.HttpURLConnection
import java.net.URL
import java.util.Date
import java.util.TimeZone
import java.util.UUID
import java.util.concurrent.TimeUnit
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

private const val ARCHIVO_COLA = "cola_acciones.json"
private const val TRABAJO = "enviar-acciones-pendientes"

/** Archivo de la cola en el almacenamiento privado de la app; se escribe completo y se reemplaza. */
private class AlmacenArchivo(private val archivo: File) : AlmacenCola {
    override fun leer(): String? = runCatching { archivo.takeIf { it.isFile }?.readText() }.getOrNull()
    override fun escribir(contenido: String) {
        runCatching {
            archivo.parentFile?.mkdirs()
            val temporal = File(archivo.parentFile, archivo.name + ".tmp")
            temporal.writeText(contenido)
            if (!temporal.renameTo(archivo)) {
                archivo.delete()
                temporal.renameTo(archivo)
            }
        }
    }
}

/**
 * Fuente de tiempo del dispositivo que combina reloj de pared, reloj monotónico y contador
 * de arranque (`BOOT_COUNT`) para reconstruir la hora real y detectar relojes alterados.
 */
private class RelojAndroid(private val context: Context) : RelojDispositivo {
    override fun ahoraMs(): Long = System.currentTimeMillis()
    override fun monotonicoMs(): Long = SystemClock.elapsedRealtime()

    // Cambia en cada reinicio del teléfono: el reloj monotónico solo es comparable dentro del mismo.
    override fun arranque(): String =
        runCatching { Settings.Global.getInt(context.contentResolver, Settings.Global.BOOT_COUNT).toString() }
            .getOrElse { ((System.currentTimeMillis() - SystemClock.elapsedRealtime()) / 60_000).toString() }

    override fun zonaHoraria(): String = TimeZone.getDefault().id
}

private fun Throwable.causas(): Sequence<Throwable> = generateSequence(this) { it.cause }

private fun codigoGrpc(error: Throwable): Status.Code? = error.causas().firstNotNullOfOrNull {
    when (it) {
        is StatusException -> it.status.code
        is StatusRuntimeException -> it.status.code
        else -> null
    }
}

private val CODIGOS_SIN_RED = setOf(Status.Code.UNAVAILABLE, Status.Code.DEADLINE_EXCEEDED, Status.Code.CANCELLED, Status.Code.ABORTED)

/** Traduce un error del servidor al resultado de la cola. */
internal fun clasificarError(error: Throwable): ResultadoEnvio {
    val causas = error.causas().toList()
    val mensaje = causas.mapNotNull { it.message }.joinToString(" ")
    val codigo = codigoGrpc(error)
    return when {
        "OPERACION_YA_APLICADA" in mensaje -> ResultadoEnvio.Aplicada
        "HORA_FUERA_DE_RANGO" in mensaje -> ResultadoEnvio.Rechazada(
            "La acción ocurrió hace más de 72 horas o la hora del teléfono es incorrecta. Revísala con administración.",
        )
        else -> clasificarFalloSistema(error, causas, mensaje, codigo)
    }
}

private fun clasificarFalloSistema(
    error: Throwable,
    causas: List<Throwable>,
    mensaje: String,
    codigo: Status.Code?,
): ResultadoEnvio = when {
    causas.any { it is IOException || it is FirebaseNetworkException } || codigo in CODIGOS_SIN_RED ->
        ResultadoEnvio.SinRed
    codigo == Status.Code.UNAUTHENTICATED || "La cuenta está inactiva" in mensaje || "Perfil no encontrado" in mensaje ->
        ResultadoEnvio.Sesion("Tu sesión ya no es válida: vuelve a iniciar sesión para enviar las acciones pendientes.")
    error is DataConnectException -> ResultadoEnvio.Rechazada(mensajeLegible(mensaje))
    causas.any { it is IllegalArgumentException || it is NoSuchElementException || it is NumberFormatException } ->
        ResultadoEnvio.Rechazada("Los datos guardados de esta acción son inválidos. Descártala y vuelve a registrarla.")
    else -> ResultadoEnvio.SinRed
}

/** El motivo del `@check` que rechazó la acción, sin el detalle técnico. */
private fun mensajeLegible(mensaje: String): String =
    Regex("\"message\"\\s*:\\s*\"([^\"]+)\"").find(mensaje)?.groupValues?.get(1)
        ?: mensaje.lineSequence().firstOrNull { it.isNotBlank() }?.take(200)
        ?: "El servidor rechazó la acción."

private fun uuid(texto: String?): UUID? = texto?.let(UUID::fromString)

/** Envía cada tipo de acción con su mutación "…Movil". */
private class EjecutorAndroid : EjecutorAcciones {
    private val dc get() = ExampleConnector.instance

    /** Obtiene la hora del servidor mediante cabecera `Date` de un `HEAD` sin redirecciones. */
    override suspend fun horaServidorMs(): Long? = withContext(Dispatchers.IO) {
        val conexion = URL(ORIGEN_WEB).openConnection() as HttpURLConnection
        try {
            conexion.requestMethod = "HEAD"
            conexion.connectTimeout = 8_000
            conexion.readTimeout = 8_000
            conexion.instanceFollowRedirects = false
            conexion.responseCode
            conexion.date.takeIf { it > 0 }
        } catch (_: IOException) {
            null
        } finally {
            conexion.disconnect()
        }
    }

    // El SDK de Data Connect lanza distintos tipos (gRPC, red, reglas): todos se clasifican.
    @Suppress("TooGenericExceptionCaught")
    override suspend fun aplicar(accion: AccionPendiente, hora: HoraOriginal): ResultadoEnvio = try {
        enviar(accion, UUID.fromString(accion.id), Timestamp(Date(hora.ocurridoEnMs)), hora.relojDudoso)
        ResultadoEnvio.Aplicada
    } catch (error: CancellationException) {
        throw error
    } catch (error: Exception) {
        clasificarError(error)
    }

    @Suppress("LongMethod")
    private suspend fun enviar(a: AccionPendiente, id: UUID, ocurrido: Timestamp, dudoso: Boolean) {
        val d = a.datos
        when (a.tipo) {
            TipoAccion.COMPLETAR_ETAPA -> dc.completarEtapaComandaMovil.execute(
                UUID.fromString(d.getValue("comandaId")), UUID.fromString(d.getValue("etapaId")), d.getValue("orden").toInt(),
                ComandaEstado.valueOf(d.getValue("estadoComanda")), id, ocurrido,
            ) { relojDudoso = dudoso }
            TipoAccion.REGISTRAR_INCIDENCIA -> dc.registrarIncidenciaComandaMovil.execute(
                UUID.fromString(d.getValue("comandaId")), d.getValue("motivo"), id, ocurrido,
            ) {
                descripcion = d["descripcion"]
                relojDudoso = dudoso
            }
            TipoAccion.ENTREGAR -> dc.entregarComandaMovil.execute(UUID.fromString(d.getValue("comandaId")), id, ocurrido) {
                detalle = d["detalle"]
                relojDudoso = dudoso
            }
            TipoAccion.REPORTAR_PROBLEMA -> dc.reportarProblemaEntregaMovil.execute(
                UUID.fromString(d.getValue("comandaId")), d.getValue("motivo"), id, ocurrido,
            ) {
                detalle = d["detalle"]
                relojDudoso = dudoso
            }
            TipoAccion.SALIDA_FLOTA -> dc.registrarSalidaFlotaMovil.execute(
                UUID.fromString(d.getValue("vehiculoId")), d.getValue("repartidorId"), d.getValue("kilometraje").toDouble(),
                EstadoVehiculo.valueOf(d.getValue("estadoVehiculo")), id, ocurrido,
            ) {
                salidaId = uuid(d["salidaId"])
                inspeccionId = uuid(d["inspeccionId"])
                observaciones = d["observaciones"]
                observacionesInspeccion = d["observacionesInspeccion"]
                relojDudoso = dudoso
            }
            TipoAccion.RETORNO_FLOTA -> dc.registrarRetornoFlotaMovil.execute(
                UUID.fromString(d.getValue("salidaId")), d.getValue("kilometraje").toDouble(),
                EstadoVehiculo.valueOf(d.getValue("estadoVehiculo")), id, ocurrido,
            ) {
                inspeccionId = uuid(d["inspeccionId"])
                observacionesRetorno = d["observacionesRetorno"]
                observacionesInspeccion = d["observacionesInspeccion"]
                relojDudoso = dudoso
            }
            TipoAccion.CONTEO -> dc.registrarConteoInventarioMovil.execute(
                UUID.fromString(d.getValue("conteoId")), UUID.fromString(d.getValue("insumoId")), d.getValue("cantidad").toDouble(),
                d.getValue("revisionStock").toInt(), id, ocurrido,
            ) { relojDudoso = dudoso }
        }
    }

    /** Envía el comentario opcional adjunto al completar una etapa cuando la mutación principal ya pasó. */
    @Suppress("TooGenericExceptionCaught")
    override suspend fun enviarComentario(accion: AccionPendiente): ResultadoEnvio {
        val texto = accion.datos["comentario"]?.trim()?.takeIf { it.isNotEmpty() }
            ?: return ResultadoEnvio.Aplicada
        val comandaId = accion.datos["comandaId"] ?: return ResultadoEnvio.Aplicada
        return try {
            dc.agregarComentarioComanda.execute(UUID.fromString(comandaId), texto)
            ResultadoEnvio.Aplicada
        } catch (error: CancellationException) {
            throw error
        } catch (error: Exception) {
            clasificarError(error)
        }
    }

    /**
     * Sube las fotos asociadas a una inspección ya sincronizada y clasifica el código HTTP
     * en reintento transitorio (`SinRed`), renovación de sesión (`401`) o rechazo definitivo.
     */
    @Suppress("TooGenericExceptionCaught")
    override suspend fun subirFotos(accion: AccionPendiente): ResultadoEnvio {
        val inspeccionId = accion.datos["inspeccionId"] ?: return ResultadoEnvio.Aplicada
        return try {
            accion.fotos.forEachIndexed { i, f -> subirFotoInspeccion(inspeccionId, FotoChequeo(f.id, f.uri, f.descripcion, i + 1), i + 1) }
            ResultadoEnvio.Aplicada
        } catch (error: FalloSubidaFoto) {
            when (error.codigo) {
                null, in 500..599, 408, 429 -> ResultadoEnvio.SinRed
                401 -> ResultadoEnvio.Sesion("Tu sesión venció: vuelve a iniciar sesión para enviar las fotografías.")
                else -> ResultadoEnvio.Rechazada(error.message ?: "La fotografía fue rechazada.")
            }
        } catch (error: CancellationException) {
            throw error
        } catch (error: Exception) {
            clasificarError(error)
        }
    }

    override fun fotosConfirmadas(accion: AccionPendiente) = eliminarCapturasConfirmadas(accion.fotos.map { it.uri })
}

@Volatile
private var contextoApp: Context? = null
private val lockInicializacion = Any()
private val alcance = CoroutineScope(SupervisorJob() + Dispatchers.IO)

private fun tieneInternetValidado(conectividad: ConnectivityManager?): Boolean {
    val capacidades = conectividad?.getNetworkCapabilities(conectividad.activeNetwork) ?: return false
    return capacidades.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) &&
        capacidades.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED)
}

/**
 * Configura la cola al abrir la app (y en el trabajo en segundo plano): archivo de la cola,
 * reloj, envío por mutaciones "…Movil", aviso de conexión y envío al recuperar la red.
 */
fun inicializarAccionesSinConexion(context: Context) {
    if (contextoApp != null) return
    synchronized(lockInicializacion) {
        if (contextoApp != null) return
        val app = context.applicationContext
        contextoApp = app
        val cola = ColaAcciones(AlmacenArchivo(File(app.filesDir, ARCHIVO_COLA)))
        val carpetaCache = File(app.filesDir, "cache_lectura")
        CacheLectura.configurar(
            fabrica = { nombre -> AlmacenArchivo(File(carpetaCache.apply { mkdirs() }, "$nombre.json")) },
            borrarTodo = { carpetaCache.deleteRecursively() },
        )
        val uid = { FirebaseAuth.getInstance().currentUser?.uid }
        val sync = Sincronizador(cola, EjecutorAndroid(), RelojAndroid(app), uid, hayRed = { AccionesSinConexion.conectado.value })
        AccionesSinConexion.configurar(cola, sync, uid) { programarEnvio(app) }
        val conectividad = app.getSystemService(ConnectivityManager::class.java)
        AccionesSinConexion.informarConexion(tieneInternetValidado(conectividad))
        conectividad?.registerDefaultNetworkCallback(
            object : ConnectivityManager.NetworkCallback() {
                override fun onAvailable(network: Network) = actualizarEstadoRed(tieneInternetValidado(conectividad))

                override fun onCapabilitiesChanged(network: Network, networkCapabilities: NetworkCapabilities) {
                    val validada = networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) &&
                        networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED)
                    actualizarEstadoRed(validada)
                }

                // Reevalúa la red activa para evitar falsos "Sin conexión" al conmutar Wi-Fi ↔ datos móviles.
                override fun onLost(network: Network) = AccionesSinConexion.informarConexion(tieneInternetValidado(conectividad))
            },
        )
        if (cola.acciones.value.isNotEmpty()) programarEnvio(app)
    }
}

private fun actualizarEstadoRed(conectadoAhora: Boolean) {
    val previo = AccionesSinConexion.conectado.value
    AccionesSinConexion.informarConexion(conectadoAhora)
    if (conectadoAhora && !previo) {
        alcance.launch { AccionesSinConexion.sincronizar(forzar = true) }
    }
}

/** Programa el envío en segundo plano: corre cuando hay red, aunque la app esté cerrada. */
private fun programarEnvio(context: Context) {
    val trabajo = OneTimeWorkRequestBuilder<EnvioAccionesWorker>()
        .setConstraints(Constraints.Builder().setRequiredNetworkType(NetworkType.CONNECTED).build())
        .setBackoffCriteria(BackoffPolicy.EXPONENTIAL, 30, TimeUnit.SECONDS)
        .build()
    WorkManager.getInstance(context).enqueueUniqueWork(TRABAJO, ExistingWorkPolicy.KEEP, trabajo)
}

/** Trabajo en segundo plano que envía la cola. Reintenta mientras queden acciones pendientes. */
class EnvioAccionesWorker(context: Context, params: WorkerParameters) : CoroutineWorker(context, params) {
    override suspend fun doWork(): Result {
        com.elcobre.lavanderiaelcobre.data.inicializarArchivosEvidencia(applicationContext)
        inicializarAccionesSinConexion(applicationContext)
        val resumen = AccionesSinConexion.sincronizador?.sincronizar(forzar = true) ?: return Result.success()
        AccionesSinConexion.emitirSincronizacion(resumen)
        // Sesión pausada: espera a que la persona vuelva a entrar (la app reprograma el envío).
        return if (resumen.pendientes > 0 && resumen.pausada == null) Result.retry() else Result.success()
    }
}

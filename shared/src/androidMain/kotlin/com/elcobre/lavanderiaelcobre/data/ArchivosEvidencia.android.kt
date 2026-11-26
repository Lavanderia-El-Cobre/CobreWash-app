package com.elcobre.lavanderiaelcobre.data

import android.content.Context
import com.elcobre.lavanderiaelcobre.data.offline.AccionesSinConexion
import com.elcobre.lavanderiaelcobre.data.offline.inicializarAccionesSinConexion
import java.io.File
import java.io.IOException

private var carpetaEvidencias: File? = null
private const val RETENCION_TEMPORAL_MS = 48L * 60 * 60 * 1000

fun inicializarArchivosEvidencia(context: Context) {
    // La cola debe estar cargada antes de limpiar: sus fotos aún no subidas no se borran.
    inicializarAccionesSinConexion(context)
    carpetaEvidencias = File(context.applicationContext.filesDir, "evidencias_flota")
    limpiarArchivosEvidencia(carpetaEvidencias, System.currentTimeMillis() - RETENCION_TEMPORAL_MS)
}

/** Fotos de acciones en cola (modo sin conexión): se conservan hasta que se suban o se descarte la acción. */
private fun fotosEnCola(): Set<String> = AccionesSinConexion.cola?.rutasFotos().orEmpty()
    .mapNotNull { runCatching { File(it.removePrefix("file://")).canonicalPath }.getOrNull() }.toSet()

/** Solo archivos temporales propios: nunca borra directorios ni sigue rutas fuera de la carpeta. */
internal fun limpiarArchivosEvidencia(carpeta: File?, antesDe: Long = Long.MAX_VALUE, protegidas: Set<String> = fotosEnCola()) {
    carpeta?.listFiles()?.forEach { archivo ->
        val enCola = runCatching { archivo.canonicalPath in protegidas }.getOrDefault(false)
        if (esCapturaPropia(carpeta, archivo) && archivo.lastModified() < antesDe && !enCola) archivo.delete()
    }
}

internal fun esCapturaPropia(carpeta: File, archivo: File): Boolean = try {
    archivo.isFile && archivo.name.startsWith("captura-") && archivo.extension == "jpg" &&
        archivo.canonicalFile.parentFile == carpeta.canonicalFile
} catch (_: IOException) { false }

internal fun eliminarCapturasConfirmadas(rutas: List<String>) {
    val carpeta = carpetaEvidencias ?: return
    rutas.forEach { ruta ->
        val archivo = File(ruta.removePrefix("file://"))
        if (esCapturaPropia(carpeta, archivo)) archivo.delete()
    }
}

internal fun eliminarCapturasAlSalir() = limpiarArchivosEvidencia(carpetaEvidencias)

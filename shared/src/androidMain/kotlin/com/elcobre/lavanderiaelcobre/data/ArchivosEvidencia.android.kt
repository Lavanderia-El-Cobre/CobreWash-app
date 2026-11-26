package com.elcobre.lavanderiaelcobre.data

import android.content.Context
import com.elcobre.lavanderiaelcobre.data.offline.AccionesSinConexion
import com.elcobre.lavanderiaelcobre.data.offline.inicializarAccionesSinConexion
import java.io.File
import java.io.IOException

private var carpetaEvidencias: File? = null
private const val RETENCION_TEMPORAL_MS = 48L * 60 * 60 * 1000

/**
 * Inicializa la carpeta privada de evidencias fotográficas y purga capturas temporales
 * vencidas (> 48 h) que no pertenezcan a acciones pendientes de sincronización.
 */
fun inicializarArchivosEvidencia(context: Context) {
    // La cola debe estar cargada antes de limpiar: sus fotos aún no subidas no se borran.
    inicializarAccionesSinConexion(context)
    carpetaEvidencias = File(context.applicationContext.filesDir, "evidencias_flota")
    limpiarArchivosEvidencia(carpetaEvidencias, System.currentTimeMillis() - RETENCION_TEMPORAL_MS)
}

/**
 * Rutas canónicas de fotos referenciadas por la cola sin conexión.
 * Se protegen de la limpieza automática hasta que se confirmen o se descarte la acción.
 */
private fun fotosEnCola(): Set<String> = AccionesSinConexion.cola?.rutasFotos().orEmpty()
    .mapNotNull { runCatching { File(it.removePrefix("file://")).canonicalPath }.getOrNull() }.toSet()

/**
 * Elimina capturas temporales anteriores a [antesDe] dentro de [carpeta], excluyendo
 * las rutas en [protegidas]. Nunca borra directorios ni sigue rutas fuera de la carpeta.
 */
internal fun limpiarArchivosEvidencia(carpeta: File?, antesDe: Long = Long.MAX_VALUE, protegidas: Set<String> = fotosEnCola()) {
    carpeta?.listFiles()?.forEach { archivo ->
        val enCola = runCatching { archivo.canonicalPath in protegidas }.getOrDefault(false)
        if (esCapturaPropia(carpeta, archivo) && archivo.lastModified() < antesDe && !enCola) archivo.delete()
    }
}

/**
 * Verifica mediante ruta canónica que [archivo] sea un JPEG generado por la app (`captura-*.jpg`)
 * y resida directamente en [carpeta], evitando borrados fuera del directorio privado.
 */
internal fun esCapturaPropia(carpeta: File, archivo: File): Boolean = try {
    archivo.isFile && archivo.name.startsWith("captura-") && archivo.extension == "jpg" &&
        archivo.canonicalFile.parentFile == carpeta.canonicalFile
} catch (_: IOException) { false }

/** Borra del dispositivo las capturas locales ya confirmadas en el servidor. */
internal fun eliminarCapturasConfirmadas(rutas: List<String>) {
    val carpeta = carpetaEvidencias ?: return
    rutas.forEach { ruta ->
        val archivo = File(ruta.removePrefix("file://"))
        if (esCapturaPropia(carpeta, archivo)) archivo.delete()
    }
}

/**
 * Purga las capturas locales al cerrar sesión para no dejar fotografías operativas
 * en dispositivos compartidos, respetando las protegidas por la cola pendiente.
 */
internal fun eliminarCapturasAlSalir() = limpiarArchivosEvidencia(carpetaEvidencias)

package com.elcobre.lavanderiaelcobre.ui.components

import android.Manifest
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.media.ExifInterface
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import com.elcobre.lavanderiaelcobre.data.OperacionComandaException
import com.elcobre.lavanderiaelcobre.data.descargarFotoEvidencia
import java.io.File
import java.io.IOException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * Implementación Android del capturador fotográfico de evidencias.
 * Gestiona el permiso `CAMERA`, abre el visor guiado [CamaraEvidenciaDialog] indicando la parte
 * del vehículo a capturar y mantiene [ActivityResultContracts.TakePicture] como respaldo del sistema.
 */
@Composable
actual fun rememberCapturadorFoto(
    onFotoCapturada: (uri: String, guia: GuiaFotoVehiculo) -> Unit,
): (GuiaFotoVehiculo) -> Unit {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val callback by rememberUpdatedState(onFotoCapturada)
    var pendienteArchivo by rememberSaveable { mutableStateOf<String?>(null) }
    var guiaPendiente by remember { mutableStateOf<GuiaFotoVehiculo?>(null) }
    var guiaEnCamara by remember { mutableStateOf<GuiaFotoVehiculo?>(null) }
    val camaraSistema = rememberLauncherForActivityResult(ActivityResultContracts.TakePicture()) { ok ->
        val archivo = pendienteArchivo?.let(::File)
        val guia = guiaPendiente ?: GUIAS_FOTOS_VEHICULO.first()
        pendienteArchivo = null
        guiaPendiente = null
        if (ok && archivo != null) scope.launch {
            val valido = withContext(Dispatchers.IO) { prepararCaptura(archivo) }
            if (valido) callback(archivo.absolutePath, guia)
            else {
                archivo.delete()
                avisar(context, "No se pudo preparar la fotografía. Captúrala nuevamente.")
            }
        } else archivo?.delete()
    }
    val abrirCamaraSistema = { guia: GuiaFotoVehiculo ->
        val archivo = crearArchivo(context)
        if (archivo == null) avisar(context, "No se pudo crear el archivo de fotografía.")
        else {
            pendienteArchivo = archivo.absolutePath
            guiaPendiente = guia
            lanzarCamara(context, archivo, camaraSistema)
        }
    }
    val permiso = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { concedido ->
        val guia = guiaPendiente
        guiaPendiente = null
        if (concedido && guia != null) guiaEnCamara = guia
        else avisar(context, "Permite el acceso a la cámara para capturar evidencia.")
    }
    guiaEnCamara?.let { guiaActiva ->
        CamaraEvidenciaDialog(
            guia = guiaActiva,
            onCapturada = { archivo ->
                guiaEnCamara = null
                callback(archivo.absolutePath, guiaActiva)
            },
            onUsarCamaraSistema = {
                guiaEnCamara = null
                abrirCamaraSistema(guiaActiva)
            },
            onCerrar = { guiaEnCamara = null },
        )
    }
    return remember(context, permiso) {
        { guia ->
            val concedido = ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED
            if (concedido) guiaEnCamara = guia
            else {
                guiaPendiente = guia
                permiso.launch(Manifest.permission.CAMERA)
            }
        }
    }
}

private fun avisar(context: Context, mensaje: String) = Toast.makeText(context, mensaje, Toast.LENGTH_LONG).show()

/** Crea un JPEG temporal (`captura-*.jpg`) en la carpeta privada limpiada por `ArchivosEvidencia`. */
internal fun crearArchivo(context: Context): File? = try {
    val carpeta = File(context.filesDir, "evidencias_flota")
    if (!carpeta.exists() && !carpeta.mkdirs()) null else File.createTempFile("captura-", ".jpg", carpeta)
} catch (_: IOException) { null }

/** Expone [archivo] a la app de cámara mediante `FileProvider` y limpia si falla el lanzamiento. */
private fun lanzarCamara(context: Context, archivo: File, lanzador: ActivityResultLauncher<android.net.Uri>) {
    try {
        val uri = FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", archivo)
        lanzador.launch(uri)
    } catch (_: ActivityNotFoundException) {
        archivo.delete()
        avisar(context, "No hay cámara disponible. No se guardó una fotografía.")
    } catch (_: SecurityException) {
        archivo.delete()
        avisar(context, "No se pudo abrir la cámara. Revisa sus permisos.")
    }
}

/**
 * Reduce resolución (<= 2200 px de lado mayor) mediante submuestreo, corrige orientación EXIF
 * y recomprime a JPEG 85% validando que el resultado no supere el límite de 3 MB.
 */
internal fun prepararCaptura(archivo: File): Boolean = try {
    val limites = BitmapFactory.Options().apply { inJustDecodeBounds = true }
    BitmapFactory.decodeFile(archivo.absolutePath, limites)
    val opciones = BitmapFactory.Options()
    while (maxOf(limites.outWidth, limites.outHeight) / opciones.inSampleSize.coerceAtLeast(1) > 2200) {
        opciones.inSampleSize = opciones.inSampleSize.coerceAtLeast(1) * 2
    }
    val bitmap = BitmapFactory.decodeFile(archivo.absolutePath, opciones)
    if (bitmap == null) false else {
        val orientacion = ExifInterface(archivo.absolutePath).getAttributeInt(ExifInterface.TAG_ORIENTATION, 1)
        val matriz = matrizOrientacion(orientacion)
        val orientada = Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matriz, true)
        val guardada = archivo.outputStream().use { orientada.compress(Bitmap.CompressFormat.JPEG, 85, it) }
        if (orientada !== bitmap) orientada.recycle()
        bitmap.recycle()
        guardada && archivo.length() in 4..(3 * 1024 * 1024)
    }
} catch (_: IOException) { false }

private fun matrizOrientacion(orientacion: Int): Matrix = Matrix().apply {
    when (orientacion) {
        ExifInterface.ORIENTATION_FLIP_HORIZONTAL -> setScale(-1f, 1f)
        ExifInterface.ORIENTATION_ROTATE_180 -> setRotate(180f)
        ExifInterface.ORIENTATION_FLIP_VERTICAL -> setScale(1f, -1f)
        ExifInterface.ORIENTATION_TRANSPOSE -> { setRotate(90f); postScale(-1f, 1f) }
        ExifInterface.ORIENTATION_ROTATE_90 -> setRotate(90f)
        ExifInterface.ORIENTATION_TRANSVERSE -> { setRotate(270f); postScale(-1f, 1f) }
        ExifInterface.ORIENTATION_ROTATE_270 -> setRotate(270f)
    }
}

private const val MAX_LADO_MINIATURA = 320

private fun calcularSampleSize(ancho: Int, alto: Int, maxLado: Int): Int {
    var factor = 1
    while (maxOf(ancho, alto) / factor > maxLado) factor *= 2
    return factor
}

private fun decodificarMiniaturaBytes(bytes: ByteArray): Bitmap? {
    val limites = BitmapFactory.Options().apply { inJustDecodeBounds = true }
    BitmapFactory.decodeByteArray(bytes, 0, bytes.size, limites)
    val opciones = BitmapFactory.Options().apply {
        inSampleSize = calcularSampleSize(limites.outWidth, limites.outHeight, MAX_LADO_MINIATURA)
    }
    return BitmapFactory.decodeByteArray(bytes, 0, bytes.size, opciones)
}

private fun decodificarMiniaturaArchivo(ruta: String): Bitmap? {
    val limites = BitmapFactory.Options().apply { inJustDecodeBounds = true }
    BitmapFactory.decodeFile(ruta, limites)
    val opciones = BitmapFactory.Options().apply {
        inSampleSize = calcularSampleSize(limites.outWidth, limites.outHeight, MAX_LADO_MINIATURA)
    }
    return BitmapFactory.decodeFile(ruta, opciones)
}

/**
 * Muestra la miniatura de una evidencia fotográfica, ya sea remota (`vehiculos/inspecciones/...`)
 * descargada con autenticación o local desde disco, con ícono de respaldo si falla la carga.
 */
@Composable
actual fun MiniaturaFotoChequeo(uri: String, descripcion: String?, tint: Color, modifier: Modifier) {
    val bitmap by produceState<ImageBitmap?>(initialValue = null, uri) {
        value = try {
            withContext(Dispatchers.IO) {
                if (uri.startsWith("vehiculos/inspecciones/")) {
                    descargarFotoEvidencia(uri)?.let { decodificarMiniaturaBytes(it)?.asImageBitmap() }
                } else decodificarMiniaturaArchivo(uri.removePrefix("file://"))?.asImageBitmap()
            }
        } catch (_: OperacionComandaException) { null }
    }
    val imagen = bitmap
    if (imagen != null) Image(bitmap = imagen, contentDescription = descripcion, contentScale = ContentScale.Crop, modifier = modifier)
    else Icon(
        imageVector = CobreIcons.PhotoCamera,
        contentDescription = "Evidencia no disponible: ${descripcion.orEmpty()}",
        tint = tint,
        modifier = modifier,
    )
}

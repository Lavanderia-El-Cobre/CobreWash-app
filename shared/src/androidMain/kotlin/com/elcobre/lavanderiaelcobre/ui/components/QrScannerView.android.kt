package com.elcobre.lavanderiaelcobre.ui.components

import android.Manifest
import android.content.pm.PackageManager
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.google.mlkit.vision.barcode.BarcodeScanner
import com.google.mlkit.vision.barcode.BarcodeScannerOptions
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.common.InputImage
import java.util.concurrent.Executors
import java.util.concurrent.ExecutionException

/**
 * Implementación Android del lector QR basado en CameraX y ML Kit.
 * Solicita automáticamente el permiso `CAMERA` al entrar y ofrece un botón para reintentar
 * si el usuario lo deniega.
 */
@Composable
actual fun QrScannerView(modifier: Modifier, isActive: Boolean, onQrCodeScanned: (String) -> Unit) {
    val context = LocalContext.current
    var permiso by remember {
        mutableStateOf(ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED)
    }
    val solicitud = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { permiso = it }
    LaunchedEffect(Unit) { if (!permiso) solicitud.launch(Manifest.permission.CAMERA) }
    if (permiso) {
        CamaraQr(modifier, isActive, onQrCodeScanned)
    } else {
        Box(modifier, contentAlignment = Alignment.Center) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("Permite el acceso a la cámara para escanear comandas.")
                Spacer(Modifier.height(16.dp))
                Button(onClick = { solicitud.launch(Manifest.permission.CAMERA) }) { Text("Otorgar permiso") }
            }
        }
    }
}

/**
 * Vincula la vista previa y el análisis de fotogramas de CameraX al ciclo de vida activo.
 * Usa `STRATEGY_KEEP_ONLY_LATEST` y un ejecutor dedicado de un hilo para evitar acumulación
 * de cuadros, y libera cámara, analizador, ML Kit y ejecutor en `onDispose`.
 */
@Composable
private fun CamaraQr(modifier: Modifier, activo: Boolean, onCodigo: (String) -> Unit) {
    val context = LocalContext.current
    val owner = LocalLifecycleOwner.current
    val activoActual by rememberUpdatedState(activo)
    val callback by rememberUpdatedState(onCodigo)
    val previewView = remember(context) { PreviewView(context) }
    val executor = remember { Executors.newSingleThreadExecutor() }
    val scanner = remember {
        BarcodeScanning.getClient(BarcodeScannerOptions.Builder().setBarcodeFormats(Barcode.FORMAT_QR_CODE).build())
    }
    var error by remember { mutableStateOf<String?>(null) }
    DisposableEffect(owner, previewView) {
        var disposed = false
        var provider: ProcessCameraProvider? = null
        val preview = Preview.Builder().build().also { it.setSurfaceProvider(previewView.surfaceProvider) }
        val analysis = ImageAnalysis.Builder().setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST).build()
        analysis.setAnalyzer(executor) { frame ->
            analizar(frame, scanner, { !disposed && activoActual }, { callback(it) }) {
                error = "No se pudo leer el QR. Vuelve a intentar."
            }
        }
        val future = ProcessCameraProvider.getInstance(context)
        future.addListener({
            if (!disposed) {
                try {
                    provider = future.get()
                    provider?.bindToLifecycle(owner, CameraSelector.DEFAULT_BACK_CAMERA, preview, analysis)
                } catch (cause: ExecutionException) {
                    Log.e("QrScanner", "No se pudo inicializar la cámara", cause)
                    error = "La cámara no está disponible."
                } catch (cause: IllegalArgumentException) {
                    Log.e("QrScanner", "No se pudo conectar la cámara", cause)
                    error = "No se encontró una cámara compatible."
                } catch (cause: IllegalStateException) {
                    Log.e("QrScanner", "La cámara no pudo iniciarse", cause)
                    error = "No se pudo iniciar la cámara."
                } catch (cause: InterruptedException) {
                    Thread.currentThread().interrupt()
                    Log.e("QrScanner", "Inicio de cámara interrumpido", cause)
                    error = "Inicio de cámara interrumpido."
                }
            }
        }, ContextCompat.getMainExecutor(context))
        onDispose {
            disposed = true
            analysis.clearAnalyzer()
            provider?.unbind(preview, analysis)
            scanner.close()
            executor.shutdown()
        }
    }
    Box(modifier) {
        AndroidView(factory = { previewView }, modifier = Modifier.fillMaxSize())
        error?.let { Text(it, modifier = Modifier.align(Alignment.Center)) }
    }
}

/**
 * Procesa un fotograma de CameraX con ML Kit respetando su rotación.
 * Cierra siempre [frame] al finalizar para evitar bloquear la cola de análisis de cámara.
 */
@androidx.annotation.OptIn(androidx.camera.core.ExperimentalGetImage::class)
private fun analizar(
    frame: ImageProxy, scanner: BarcodeScanner, activo: () -> Boolean,
    onCodigo: (String) -> Unit, onError: () -> Unit,
) {
    val media = frame.image
    if (!activo() || media == null) {
        frame.close()
        return
    }
    scanner.process(InputImage.fromMediaImage(media, frame.imageInfo.rotationDegrees))
        .addOnSuccessListener { resultados ->
            if (activo()) resultados.firstNotNullOfOrNull { it.rawValue }?.let(onCodigo)
        }
        .addOnFailureListener { cause ->
            Log.w("QrScanner", "Error al analizar un fotograma", cause)
            if (activo()) onError()
        }
        .addOnCompleteListener { frame.close() }
}

package com.elcobre.lavanderiaelcobre.ui.components

import android.content.Context
import android.util.Log
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.elcobre.lavanderiaelcobre.ui.theme.CobreTheme
import java.io.File
import java.util.concurrent.ExecutionException
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.concurrent.atomic.AtomicBoolean
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * Visor de cámara integrado con CameraX que muestra sobre la vista en vivo la parte del vehículo
 * a fotografiar (ej. "Frente del vehículo") junto con su indicación de encuadre.
 * Si CameraX no logra inicializarse en el dispositivo, ofrece respaldo con la cámara del sistema.
 */
@Composable
internal fun CamaraEvidenciaDialog(
    guia: GuiaFotoVehiculo,
    onCapturada: (File) -> Unit,
    onUsarCamaraSistema: () -> Unit,
    onCerrar: () -> Unit,
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var capturando by remember { mutableStateOf(false) }
    var errorCamara by remember { mutableStateOf<String?>(null) }
    var imageCapture by remember { mutableStateOf<ImageCapture?>(null) }
    val executor = remember { Executors.newSingleThreadExecutor() }

    DisposableEffect(executor) {
        onDispose { executor.shutdown() }
    }

    val tomarFoto = {
        val captura = imageCapture
        if (!capturando && captura != null) {
            capturando = true
            errorCamara = null
            ejecutarDisparo(context, captura, executor) { archivo, error ->
                scope.launch {
                    if (archivo != null && withContext(Dispatchers.IO) { prepararCaptura(archivo) }) {
                        capturando = false
                        onCapturada(archivo)
                    } else {
                        capturando = false
                        archivo?.delete()
                        errorCamara = error ?: "No se pudo guardar la fotografía. Intenta nuevamente."
                    }
                }
            }
        }
    }

    Dialog(
        onDismissRequest = { if (!capturando) onCerrar() },
        properties = DialogProperties(
            usePlatformDefaultWidth = false,
            dismissOnBackPress = !capturando,
            dismissOnClickOutside = false,
        ),
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black),
        ) {
            VisorCameraX(
                onListo = { imageCapture = it },
                onError = { errorCamara = it },
            )
            MarcoEncuadreVehiculo(modifier = Modifier.align(Alignment.Center))
            CabeceraGuiaCamara(
                guia = guia,
                capturando = capturando,
                onCerrar = onCerrar,
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .fillMaxWidth(),
            )
            BarraDisparoCamara(
                guia = guia,
                capturando = capturando,
                habilitado = imageCapture != null && !capturando,
                error = errorCamara,
                onDisparar = tomarFoto,
                onUsarCamaraSistema = onUsarCamaraSistema,
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth(),
            )
        }
    }
}

@Composable
private fun VisorCameraX(
    onListo: (ImageCapture) -> Unit,
    onError: (String) -> Unit,
) {
    val context = LocalContext.current
    val owner = LocalLifecycleOwner.current
    val previewView = remember(context) { PreviewView(context) }

    DisposableEffect(owner, previewView) {
        val disposed = AtomicBoolean(false)
        var provider: ProcessCameraProvider? = null
        val preview = Preview.Builder().build().also { it.setSurfaceProvider(previewView.surfaceProvider) }
        val capture = ImageCapture.Builder()
            .setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY)
            .build()
        val future = ProcessCameraProvider.getInstance(context)
        future.addListener({
            if (!disposed.get()) {
                vincularCamara(future, owner, preview, capture) { camProvider, error ->
                    if (camProvider != null) {
                        provider = camProvider
                        onListo(capture)
                    } else if (error != null) {
                        onError(error)
                    }
                }
            }
        }, ContextCompat.getMainExecutor(context))
        onDispose {
            disposed.set(true)
            provider?.unbind(preview, capture)
        }
    }

    AndroidView(factory = { previewView }, modifier = Modifier.fillMaxSize())
}

private fun vincularCamara(
    future: com.google.common.util.concurrent.ListenableFuture<ProcessCameraProvider>,
    owner: androidx.lifecycle.LifecycleOwner,
    preview: Preview,
    capture: ImageCapture,
    alResultado: (ProcessCameraProvider?, String?) -> Unit,
) {
    try {
        val provider = future.get()
        provider.unbindAll()
        provider.bindToLifecycle(owner, CameraSelector.DEFAULT_BACK_CAMERA, preview, capture)
        alResultado(provider, null)
    } catch (cause: ExecutionException) {
        Log.e("CamaraEvidencia", "Error al inicializar cámara", cause)
        alResultado(null, "No se pudo iniciar la cámara integrada.")
    } catch (cause: IllegalArgumentException) {
        Log.e("CamaraEvidencia", "Cámara trasera no disponible", cause)
        alResultado(null, "No se encontró una cámara trasera compatible.")
    } catch (cause: IllegalStateException) {
        Log.e("CamaraEvidencia", "No se pudo enlazar la cámara", cause)
        alResultado(null, "La cámara está ocupada o no disponible.")
    } catch (cause: InterruptedException) {
        Thread.currentThread().interrupt()
        Log.e("CamaraEvidencia", "Inicio de cámara interrumpido", cause)
        alResultado(null, "Inicio de cámara interrumpido.")
    }
}

private fun ejecutarDisparo(
    context: Context,
    capture: ImageCapture,
    executor: ExecutorService,
    alTerminar: (archivo: File?, error: String?) -> Unit,
) {
    val archivo = crearArchivo(context)
    if (archivo == null) {
        alTerminar(null, "No se pudo crear el archivo de fotografía.")
        return
    }
    val opciones = ImageCapture.OutputFileOptions.Builder(archivo).build()
    capture.takePicture(
        opciones,
        executor,
        object : ImageCapture.OnImageSavedCallback {
            override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
                alTerminar(archivo, null)
            }

            override fun onError(exception: ImageCaptureException) {
                Log.w("CamaraEvidencia", "Error al capturar foto", exception)
                archivo.delete()
                alTerminar(null, "No se pudo capturar la fotografía. Intenta nuevamente.")
            }
        },
    )
}

@Composable
private fun CabeceraGuiaCamara(
    guia: GuiaFotoVehiculo,
    capturando: Boolean,
    onCerrar: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val accent = CobreTheme.colors.accent
    val etiquetaPaso = guia.paso?.let { "Paso $it de ${GUIAS_FOTOS_VEHICULO.size}" } ?: "Evidencia adicional"
    Column(
        modifier = modifier
            .background(Color.Black.copy(alpha = 0.62f))
            .statusBarsPadding()
            .padding(horizontal = 16.dp, vertical = 14.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Surface(
                color = accent.copy(alpha = 0.24f),
                shape = CircleShape,
                border = BorderStroke(1.dp, accent.copy(alpha = 0.7f)),
            ) {
                Text(
                    text = etiquetaPaso,
                    style = MaterialTheme.typography.labelMedium,
                    color = Color.White,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 5.dp),
                )
            }
            IconButton(onClick = { if (!capturando) onCerrar() }, enabled = !capturando) {
                Icon(
                    imageVector = CobreIcons.Close,
                    contentDescription = "Cerrar cámara",
                    tint = Color.White,
                )
            }
        }
        Spacer(Modifier.height(6.dp))
        Text(
            text = guia.tituloCamara,
            style = MaterialTheme.typography.headlineSmall,
            color = Color.White,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
        )
        Spacer(Modifier.height(4.dp))
        Text(
            text = guia.indicacion,
            style = MaterialTheme.typography.bodySmall,
            color = Color.White.copy(alpha = 0.85f),
            textAlign = TextAlign.Center,
        )
    }
}

@Composable
private fun MarcoEncuadreVehiculo(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp)
            .height(260.dp)
            .border(
                width = 1.5.dp,
                color = Color.White.copy(alpha = 0.45f),
                shape = RoundedCornerShape(20.dp),
            ),
    )
}

@Composable
private fun BarraDisparoCamara(
    guia: GuiaFotoVehiculo,
    capturando: Boolean,
    habilitado: Boolean,
    error: String?,
    onDisparar: () -> Unit,
    onUsarCamaraSistema: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .background(Color.Black.copy(alpha = 0.68f))
            .navigationBarsPadding()
            .padding(horizontal = 20.dp, vertical = 18.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        if (error != null) {
            Text(
                text = error,
                style = MaterialTheme.typography.bodySmall,
                color = Color(0xFFFFB4AB),
                textAlign = TextAlign.Center,
            )
            CobreButton(
                text = "Usar cámara del teléfono",
                onClick = onUsarCamaraSistema,
                variant = ButtonVariant.Tonal,
                icon = CobreIcons.PhotoCamera,
            )
        }
        Box(
            modifier = Modifier
                .size(76.dp)
                .clip(CircleShape)
                .border(3.dp, Color.White, CircleShape)
                .padding(6.dp)
                .clip(CircleShape)
                .background(if (habilitado) Color.White else Color.White.copy(alpha = 0.45f))
                .semantics { contentDescription = "Capturar ${guia.tituloCamara}" }
                .clickable(enabled = habilitado, role = Role.Button, onClick = onDisparar),
            contentAlignment = Alignment.Center,
        ) {
            if (capturando) {
                CircularProgressIndicator(
                    modifier = Modifier.size(28.dp),
                    strokeWidth = 3.dp,
                    color = Color.Black,
                )
            }
        }
        Text(
            text = if (capturando) "Guardando ${guia.etiqueta}…" else "Toca para capturar · ${guia.etiqueta}",
            style = MaterialTheme.typography.labelMedium,
            color = Color.White.copy(alpha = 0.9f),
        )
    }
}

package com.elcobre.lavanderiaelcobre

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.content.ContextCompat
import com.elcobre.lavanderiaelcobre.data.auth.inicializarSesionPersistente
import com.elcobre.lavanderiaelcobre.ui.inicializarNotificadorPlanta

/**
 * Actividad raíz de Android que aloja la interfaz compartida de Compose Multiplatform ([App]).
 *
 * Antes de montar la composición, inicializa los puentes dependientes de `Context` (notificaciones
 * locales de planta, persistencia de sesión y almacenamiento temporal de evidencias) y gestiona
 * la solicitud del permiso de notificaciones en Android 13+.
 */
class MainActivity : ComponentActivity() {
    // Registro del contrato de permisos en tiempo de ejecución; el resultado no bloquea el uso.
    private val permisoNotificaciones = registerForActivityResult(
        ActivityResultContracts.RequestPermission(),
    ) { }

    override fun onCreate(savedInstanceState: Bundle?) {
        // Extiende el lienzo bajo las barras del sistema para el diseño de borde a borde.
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)

        // Inicialización temprana de servicios Android que requieren Context antes de montar App().
        inicializarNotificadorPlanta(this)
        inicializarSesionPersistente(this)
        com.elcobre.lavanderiaelcobre.data.inicializarArchivosEvidencia(this)

        // POST_NOTIFICATIONS es un permiso en tiempo de ejecución obligatorio recién desde API 33.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
            ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED
        ) {
            permisoNotificaciones.launch(Manifest.permission.POST_NOTIFICATIONS)
        }

        setContent {
            App()
        }
    }
}

/** Previsualización en Android Studio de la raíz compartida [App]. */
@Preview
@Composable
fun AppAndroidPreview() {
    App()
}

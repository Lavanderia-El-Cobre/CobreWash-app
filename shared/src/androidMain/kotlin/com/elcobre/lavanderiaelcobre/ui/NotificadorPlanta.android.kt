package com.elcobre.lavanderiaelcobre.ui

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import java.util.concurrent.atomic.AtomicInteger

private const val CANAL_PLANTA_ID = "cobre_planta_operaciones"
private const val CANAL_PLANTA_NOMBRE = "Avisos y Comandas de Planta"

private var appContext: Context? = null
private val idSecuencia = AtomicInteger(1000)

/**
 * Registra el canal de notificaciones operativas de planta en Android 8+ (API 26+)
 * y conserva el contexto de aplicación para emitir alertas desde lógica compartida.
 */
fun inicializarNotificadorPlanta(context: Context) {
    val application = context.applicationContext
    appContext = application
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        val manager = application.getSystemService(Context.NOTIFICATION_SERVICE) as? NotificationManager ?: return
        val canal = NotificationChannel(
            CANAL_PLANTA_ID,
            CANAL_PLANTA_NOMBRE,
            NotificationManager.IMPORTANCE_DEFAULT,
        ).apply {
            description = "Notificaciones de nuevos avisos de administración y comandas asignadas"
        }
        manager.createNotificationChannel(canal)
    }
}

/**
 * Muestra una notificación local para eventos de planta (avisos o comandas asignadas).
 * Verifica el permiso en tiempo de ejecución `POST_NOTIFICATIONS` en Android 13+ (API 33+)
 * y usa IDs incrementales para no sobrescribir alertas simultáneas.
 */
internal actual fun notificarEventoPlanta(titulo: String, mensaje: String) {
    val context = appContext ?: return
    runCatching {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            val concedido = ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS,
            ) == PackageManager.PERMISSION_GRANTED
            if (!concedido) return
        }
        val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as? NotificationManager ?: return
        val notificacion = NotificationCompat.Builder(context, CANAL_PLANTA_ID)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle(titulo)
            .setContentText(mensaje)
            .setStyle(NotificationCompat.BigTextStyle().bigText(mensaje))
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)
            .build()
        manager.notify(idSecuencia.incrementAndGet(), notificacion)
    }
}

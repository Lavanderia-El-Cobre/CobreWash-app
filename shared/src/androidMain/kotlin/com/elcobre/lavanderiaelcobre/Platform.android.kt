package com.elcobre.lavanderiaelcobre

import android.os.Build

/** Implementación Android de [Platform] que expone el nivel de API del dispositivo. */
class AndroidPlatform : Platform {
    override val name: String = "Android ${Build.VERSION.SDK_INT}"
}

/** Resuelve [getPlatform] devolviendo la implementación específica de Android. */
actual fun getPlatform(): Platform = AndroidPlatform()

package com.elcobre.lavanderiaelcobre

import platform.UIKit.UIDevice

/** Implementación iOS de [Platform] basada en nombre y versión reportados por `UIDevice`. */
class IOSPlatform: Platform {
    override val name: String = UIDevice.currentDevice.systemName() + " " + UIDevice.currentDevice.systemVersion
}

/** Resuelve [getPlatform] devolviendo la implementación específica de iOS. */
actual fun getPlatform(): Platform = IOSPlatform()

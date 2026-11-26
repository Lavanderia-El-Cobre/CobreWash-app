package com.elcobre.lavanderiaelcobre

/**
 * Utilidad multiplataforma heredada de la plantilla KMP para verificar el enlace
 * `expect`/`actual` de [Platform] en pruebas y diagnósticos básicos.
 */
@Suppress("unused")
class Greeting {
    private val platform = getPlatform()

    /** Construye un saludo usando el identificador de la plataforma actual. */
    fun greet(): String {
        return sayHello(platform.name)
    }
}

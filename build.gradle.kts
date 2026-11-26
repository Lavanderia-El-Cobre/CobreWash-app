plugins {
    // Declarar los plugins aquí con `apply false` evita cargarlos múltiples veces
    // en los classloaders de cada subproyecto.
    alias(libs.plugins.androidApplication) apply false
    alias(libs.plugins.androidMultiplatformLibrary) apply false
    alias(libs.plugins.composeMultiplatform) apply false
    alias(libs.plugins.composeCompiler) apply false
    alias(libs.plugins.kotlinMultiplatform) apply false
    alias(libs.plugins.googleServices) apply false
    alias(libs.plugins.detekt)
}

// Análisis estático (ISO/IEC 25010 mantenibilidad). Ejecutar con `gradlew detekt`.
// Quality gate real: el build falla si se reintroducen violaciones.
detekt {
    buildUponDefaultConfig = true
    config.setFrom("config/detekt/detekt.yml")
    ignoreFailures = false
    source.setFrom(
        "shared/src/commonMain/kotlin",
        "shared/src/androidMain/kotlin",
        "shared/src/iosMain/kotlin",
        "shared/src/commonTest/kotlin",
        "androidApp/src/main/kotlin",
    )
}

// Genera reportes TXT (consola/CI), XML (integraciones) y HTML (revisión local).
tasks.withType<io.gitlab.arturbosch.detekt.Detekt>().configureEach {
    reports {
        txt.required.set(true)
        xml.required.set(true)
        html.required.set(true)
    }
}

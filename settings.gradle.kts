rootProject.name = "LavanderiaElCobre"
// Habilita accesores con seguridad de tipos entre módulos (por ejemplo, `projects.shared`).
enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

pluginManagement {
    repositories {
        // Restringe las consultas a Google Maven solo a sus grupos para agilizar el sync.
        google {
            mavenContent {
                includeGroupAndSubgroups("androidx")
                includeGroupAndSubgroups("com.android")
                includeGroupAndSubgroups("com.google")
            }
        }
        mavenCentral()
        gradlePluginPortal()
    }
}

dependencyResolutionManagement {
    repositories {
        // Mismo filtrado de grupos para evitar búsquedas innecesarias en dependencias externas.
        google {
            mavenContent {
                includeGroupAndSubgroups("androidx")
                includeGroupAndSubgroups("com.android")
                includeGroupAndSubgroups("com.google")
            }
        }
        mavenCentral()
    }
}

// `:androidApp` aloja el punto de entrada Android; `:shared` concentra el código KMP y Compose.
include(":androidApp")
include(":shared")

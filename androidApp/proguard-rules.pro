# ==============================================================================
# Reglas ProGuard / R8 para CobreWash Android
# ==============================================================================

# --- Kotlinx Serialization ---
-keepattributes *Annotation*, InnerClasses
-dontnote kotlinx.serialization.SerializationKt

# Conservar serializers y objetos Companion generados
-keepclassmembers class * {
    *** Companion;
}
-keepclasseswithmembers class * {
    kotlinx.serialization.KSerializer serializer(...);
}
-keep class kotlinx.serialization.** { *; }

# Modelos serializables de CobreWash
-keep class com.elcobre.lavanderiaelcobre.data.model.** { *; }
-keep class com.elcobre.lavanderiaelcobre.data.auth.** { *; }

# --- Firebase & DataConnect ---
# Conservar el SDK generado y los conectores para evitar truncamiento de campos en queries
-keep class com.google.firebase.dataconnect.** { *; }
-keep class com.elcobre.lavanderiaelcobre.dataconnect.** { *; }
-keepclassmembers class com.elcobre.lavanderiaelcobre.dataconnect.** { *; }

# --- Jetpack Compose Multiplatform ---
-keep class androidx.compose.** { *; }
-keepclassmembers class * {
    @androidx.compose.runtime.Composable *;
}

# --- Coroutines ---
-keepnames class kotlinx.coroutines.internal.MainDispatcherFactory {}
-keepnames class kotlinx.coroutines.CoroutineExceptionHandler {}

# --- ML Kit & CameraX ---
-keep class com.google.mlkit.** { *; }
-dontwarn com.google.mlkit.**

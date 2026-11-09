# CobreWash App

Aplicación móvil (Android/iOS) de **Lavandería El Cobre** para el seguimiento operativo de pedidos de ropa a lo largo del proceso de lavandería: recepción, lavado, secado, planchado y entrega (en tienda o a domicilio), con alertas de incidencias/retrasos y reporte de insumos. Comparte lógica de negocio con la app web mediante un módulo Kotlin Multiplatform.

> **Estado:** prototipo funcional con datos mock en memoria (sin backend real todavía). Ver [`docs/COMPLIANCE.md`](docs/COMPLIANCE.md) para el mapeo de calidad/seguridad y la hoja de ruta hacia Firebase.

## Stack

- **Kotlin Multiplatform (KMP)** — lógica y UI compartidas entre Android e iOS
- **Compose Multiplatform** — UI declarativa
- **MVVM** — `ViewModel`s con estado inmutable en `commonMain`
- **detekt** — análisis estático (`config/detekt/detekt.yml`)

## Estructura del proyecto

```
androidApp/   # Punto de entrada Android
iosApp/       # Punto de entrada iOS (SwiftUI + Compose Multiplatform)
shared/       # Código compartido (commonMain, androidMain, iosMain)
  ├─ data/         # Modelos y datos mock
  └─ ui/            # Pantallas, componentes y tema
    ├─ screens/     # login, dashboard, detalle, vehiculos, insumos, avisos, escaner, configuracion
    └─ components/  # Componentes reutilizables (top bar, iconos, etc.)
docs/         # Documentación de cumplimiento y estándares
config/       # Configuración de herramientas (detekt)
```

## Requisitos

- JDK 17+
- Android Studio (Koala o superior) con plugin de Kotlin Multiplatform
- Para iOS: Xcode + macOS

## Cómo correr la app

- **Firebase Android:** descargar `google-services.json` desde la app Android con paquete `com.elcobre.lavanderiaelcobre` del proyecto `lavanderia-el-cobre-16fd5` y guardarlo en `androidApp/`. El archivo está excluido de Git. La configuración de `config/google-services.ci.json` solo permite comprobar la compilación en CI con `-PallowDemoFirebase=true`; no permite iniciar sesión.
- **Android:** `./gradlew :androidApp:assembleDebug` o usar el run widget del IDE
- **iOS:** abrir [`/iosApp`](./iosApp) en Xcode y ejecutar desde ahí

## Tests

- **Android:** `./gradlew :shared:testAndroidHostTest`
- **iOS:** `./gradlew :shared:iosSimulatorArm64Test`

## Flujo de trabajo / contribución

Este proyecto sigue un flujo de ramas `main` (producción) → `test` (integración) → `feature/xxx` / `fix/xxx` (trabajo diario), con commits en formato [Conventional Commits](https://www.conventionalcommits.org/). Nunca se hace push directo a `main`.

## Seguridad

Ver [`SECURITY.md`](SECURITY.md) para la política de reporte de vulnerabilidades.

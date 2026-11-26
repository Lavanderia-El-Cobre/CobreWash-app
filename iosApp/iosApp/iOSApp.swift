import SwiftUI

/// Punto de entrada principal de la aplicación iOS.
///
/// Crea la escena raíz y delega toda la interfaz a `ContentView`, que sirve de puente
/// hacia el módulo compartido Compose Multiplatform (`Shared`).
@main
struct iOSApp: App {
    var body: some Scene {
        WindowGroup {
            ContentView()
        }
    }
}

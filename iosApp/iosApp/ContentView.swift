import UIKit
import SwiftUI
import Shared

/// Puente `UIViewControllerRepresentable` que aloja la interfaz Compose Multiplatform en iOS.
///
/// Invoca `MainViewControllerKt.MainViewController()` del framework compartido `Shared` para
/// montar el árbol raíz `App()`. Toda la navegación y el estado de UI residen en Kotlin.
struct ComposeView: UIViewControllerRepresentable {
    func makeUIViewController(context: Self.Context) -> UIViewController {
        MainViewControllerKt.MainViewController()
    }

    // Sin sincronización desde SwiftUI: el estado reactivo vive íntegramente en Compose.
    func updateUIViewController(_ uiViewController: UIViewController, context: Self.Context) {}
}

/// Vista raíz SwiftUI que extiende el lienzo de Compose a toda la pantalla.
///
/// Se ignoran las áreas seguras de SwiftUI para que Compose administre los insets de sistema
/// y renderice el fondo de borde a borde de forma consistente con Android.
struct ContentView: View {
    var body: some View {
        ComposeView()
            .ignoresSafeArea()
    }
}

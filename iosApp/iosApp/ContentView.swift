import UIKit
import SwiftUI
import shared

struct ComposeView: UIViewControllerRepresentable {

    func makeUIViewController(context: Context) -> UIViewController {
        Main_iosKt.MainViewController(
            apiKey: SecurityKeyLibrary.apiKey,
            apiHost: SecurityKeyLibrary.apiHost,
            apiVersion: SecurityKeyLibrary.apiVersion
        )
    }

    func updateUIViewController(_ uiViewController: UIViewController, context: Context) {}
}

struct ContentView: View {
    var body: some View {
        ComposeView()
            .ignoresSafeArea(.all, edges: .bottom) // Compose has its own keyboard handler
    }
}

import androidx.compose.runtime.remember
import androidx.compose.ui.window.ComposeUIViewController
import com.arkivanov.decompose.DefaultComponentContext
import com.arkivanov.decompose.router.stack.StackNavigation
import com.arkivanov.essenty.lifecycle.LifecycleRegistry
import di.CompositionRoot
import ui.Configuration
import ui.ProvideAppComponents

fun MainViewController(
    apiKey: String,
    apiHost: String,
    apiVersion: String,
) = ComposeUIViewController {
    val lifecycle = remember { LifecycleRegistry() }
    val rootComponentContext = remember { DefaultComponentContext(lifecycle) }
    val compositionRoot = remember {
        CompositionRoot(
            componentContext = rootComponentContext,
            apiKey = apiKey,
            apiHost = apiHost,
            apiVersion = apiVersion,
        )
    }
    val stackNavigation = remember { StackNavigation<Configuration>() }

    ProvideAppComponents(rootComponentContext, compositionRoot, stackNavigation) {
        App()
    }
}

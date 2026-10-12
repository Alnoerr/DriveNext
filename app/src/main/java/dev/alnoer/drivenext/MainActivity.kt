@file:OptIn(com.arkivanov.decompose.ExperimentalDecomposeApi::class)

package dev.alnoer.drivenext

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.arkivanov.decompose.defaultComponentContext
import com.arkivanov.decompose.jetpackcomponentcontext.asJetpackComponentContext
import dev.alnoer.drivenext.presentation.DriveNextApp
import dev.alnoer.drivenext.presentation.navigation.RootComponent
import dev.alnoer.drivenext.presentation.theme.DriveNextTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()

        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val container = (application as DriveNextApplication).container
        val root = RootComponent(
            componentContext = defaultComponentContext().asJetpackComponentContext(),
            repository = container.authRepository,
            preferences = container.appPreferences,
            hasInternet = { container.networkMonitor.hasInternet },
            googleAuth = container.googleAuth
        )
        setContent {
            DriveNextTheme {
                DriveNextApp(root)
            }
        }
    }
}

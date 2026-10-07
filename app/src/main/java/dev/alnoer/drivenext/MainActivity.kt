package dev.alnoer.drivenext

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import dev.alnoer.drivenext.presentation.DriveNextApp
import dev.alnoer.drivenext.presentation.theme.DriveNextTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()

        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val container = (application as DriveNextApplication).container
        setContent {
            DriveNextTheme {
                DriveNextApp(
                    isOnboardingCompleted = { container.appPreferences.isOnboardingCompleted },
                    completeOnboarding = { container.appPreferences.isOnboardingCompleted = true },
                    hasInternet = { container.networkMonitor.hasInternet }
                )
            }
        }
    }
}

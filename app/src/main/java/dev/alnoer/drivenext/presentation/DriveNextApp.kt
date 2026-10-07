package dev.alnoer.drivenext.presentation

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import dev.alnoer.drivenext.presentation.auth.AuthScreen
import dev.alnoer.drivenext.presentation.no_connection.NoConnectionScreen
import dev.alnoer.drivenext.presentation.onboarding.OnboardingScreen
import dev.alnoer.drivenext.presentation.splash.SplashScreen
import kotlinx.coroutines.delay
import kotlin.time.Duration.Companion.seconds

private enum class Screen { Splash, Onboarding, NoConnection, Auth }

@Composable
fun DriveNextApp(
    isOnboardingCompleted: () -> Boolean,
    completeOnboarding: () -> Unit,
    hasInternet: () -> Boolean
) {
    var screen by rememberSaveable { mutableStateOf(Screen.Splash) }
    val currentHasInternet by rememberUpdatedState(hasInternet)
    val currentIsOnboardingCompleted by rememberUpdatedState(isOnboardingCompleted)

    fun destination(): Screen = when {
        !currentHasInternet() -> Screen.NoConnection
        !currentIsOnboardingCompleted() -> Screen.Onboarding
        else -> Screen.Auth
    }

    LaunchedEffect(screen) {
        if (screen == Screen.Splash) {
            delay(2.seconds)
            screen = destination()
        }
    }

    Surface(
        Modifier.fillMaxSize().safeDrawingPadding(),
        color = MaterialTheme.colorScheme.background
    ) {
        when (screen) {
            Screen.Splash -> SplashScreen()
            Screen.Onboarding -> OnboardingScreen(
                onFinish = {
                    completeOnboarding()
                    screen = destination()
                }
            )
            Screen.NoConnection -> NoConnectionScreen(
                onRetry = { screen = destination() }
            )
            Screen.Auth -> AuthScreen()
        }
    }
}

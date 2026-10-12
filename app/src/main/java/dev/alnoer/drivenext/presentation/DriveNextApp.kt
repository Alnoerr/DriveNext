@file:OptIn(com.arkivanov.decompose.ExperimentalDecomposeApi::class)

package dev.alnoer.drivenext.presentation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.arkivanov.decompose.extensions.compose.stack.Children
import dev.alnoer.drivenext.presentation.auth.AuthScreen
import dev.alnoer.drivenext.presentation.auth.LoginContent
import dev.alnoer.drivenext.presentation.auth.RegistrationContent
import dev.alnoer.drivenext.presentation.auth.RegistrationSuccessScreen
import dev.alnoer.drivenext.presentation.navigation.RootComponent
import dev.alnoer.drivenext.presentation.no_connection.NoConnectionScreen
import dev.alnoer.drivenext.presentation.onboarding.OnboardingScreen
import dev.alnoer.drivenext.presentation.splash.SplashScreen

@Composable
fun DriveNextApp(root: RootComponent) {
    Surface(
        Modifier.fillMaxSize().safeDrawingPadding(),
        color = MaterialTheme.colorScheme.background
    ) {
        Children(root.stack) { entry ->
            val child = entry.instance
            CompositionLocalProvider(LocalLifecycleOwner provides child.lifecycleOwner) {
                when (child) {
                    is RootComponent.Child.Splash -> SplashScreen()
                    is RootComponent.Child.Onboarding -> OnboardingScreen(child.component::onFinish)
                    is RootComponent.Child.NoConnection -> NoConnectionScreen(child.component::onRetry)
                    is RootComponent.Child.Auth -> AuthScreen(child.component::onLogin, child.component::onRegister)
                    is RootComponent.Child.Login -> LoginContent(child.component)
                    is RootComponent.Child.Registration -> RegistrationContent(child.component)
                    is RootComponent.Child.Success -> RegistrationSuccessScreen(child.component::onNext)
                    is RootComponent.Child.Main -> Box(Modifier.fillMaxSize())
                }
            }
        }
    }
}

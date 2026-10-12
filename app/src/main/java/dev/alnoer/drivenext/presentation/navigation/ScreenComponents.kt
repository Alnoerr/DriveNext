@file:OptIn(com.arkivanov.decompose.ExperimentalDecomposeApi::class)

package dev.alnoer.drivenext.presentation.navigation

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.arkivanov.decompose.jetpackcomponentcontext.JetpackComponentContext
import dev.alnoer.drivenext.data.local.AppPreferences
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.seconds

class SplashComponent(
    componentContext: JetpackComponentContext,
    onFinished: () -> Unit
) : JetpackComponentContext by componentContext {
    init {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.RESUMED) {
                delay(2.seconds)
                onFinished()
            }
        }
    }
}

class OnboardingComponent(
    componentContext: JetpackComponentContext,
    private val preferences: AppPreferences,
    private val onFinished: () -> Unit
) : JetpackComponentContext by componentContext {
    fun onFinish() {
        preferences.isOnboardingCompleted = true
        onFinished()
    }
}

class NoConnectionComponent(
    componentContext: JetpackComponentContext,
    private val onRetryConnection: () -> Unit
) : JetpackComponentContext by componentContext {
    fun onRetry() {
        onRetryConnection()
    }
}

class AuthComponent(
    componentContext: JetpackComponentContext,
    private val onLoginSelected: () -> Unit,
    private val onRegistrationSelected: () -> Unit
) : JetpackComponentContext by componentContext {
    fun onLogin() {
        onLoginSelected()
    }

    fun onRegister() {
        onRegistrationSelected()
    }
}

class RegistrationSuccessComponent(
    componentContext: JetpackComponentContext,
    private val onContinue: () -> Unit
) : JetpackComponentContext by componentContext {
    fun onNext() {
        onContinue()
    }
}

class MainComponent(componentContext: JetpackComponentContext) : JetpackComponentContext by componentContext

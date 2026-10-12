@file:OptIn(com.arkivanov.decompose.ExperimentalDecomposeApi::class)

package dev.alnoer.drivenext.presentation.auth

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.arkivanov.decompose.extensions.compose.stack.Children

@Composable
fun LoginContent(component: LoginComponent) {
    val state by component.state.collectAsStateWithLifecycle()
    val context = LocalContext.current

    LoginScreen(
        state = state,
        onUpdate = component::onUpdate,
        onLogin = component::onLogin,
        onGoogle = { component.onGoogle(context) },
        onRegister = component::onRegister
    )
}

@Composable
fun RegistrationContent(component: RegistrationComponent) {
    Children(component.stack) { entry ->
        val child = entry.instance
        CompositionLocalProvider(LocalLifecycleOwner provides child.lifecycleOwner) {
            when (child) {
                is RegistrationComponent.Child.Credentials -> {
                    val state by child.component.state.collectAsStateWithLifecycle()
                    RegistrationCredentialsScreen(
                        state, child.component::onUpdate, child.component::onNext, child.component::onBack
                    )
                }
                is RegistrationComponent.Child.Profile -> {
                    val state by child.component.state.collectAsStateWithLifecycle()
                    RegistrationProfileScreen(
                        state, child.component::onUpdate, child.component::onNext, child.component::onBack
                    )
                }
                is RegistrationComponent.Child.Documents -> {
                    val state by child.component.state.collectAsStateWithLifecycle()
                    RegistrationDocumentsScreen(
                        state, child.component::onUpdate, child.component::onPhoto, child.component::onError,
                        child.component::onNext, child.component::onBack
                    )
                }
            }
        }
    }
}

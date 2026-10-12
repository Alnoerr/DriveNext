@file:OptIn(com.arkivanov.decompose.ExperimentalDecomposeApi::class)

package dev.alnoer.drivenext.presentation.auth

import android.content.Context
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.arkivanov.decompose.jetpackcomponentcontext.JetpackComponentContext
import com.arkivanov.decompose.jetpackcomponentcontext.viewModel
import dev.alnoer.drivenext.data.auth.AuthenticationRepository
import dev.alnoer.drivenext.data.auth.GoogleAuth
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.launch

class LoginComponent(
    componentContext: JetpackComponentContext,
    repository: AuthenticationRepository,
    private val googleAuth: GoogleAuth,
    private val onAuthenticated: () -> Unit,
    private val onRegistration: () -> Unit
) : JetpackComponentContext by componentContext {
    private val viewModel = viewModel { AuthViewModel(repository, createSavedStateHandle()) }
    val state = viewModel.state

    init {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.RESUMED) {
                viewModel.result.filterNotNull().collect {
                    viewModel.consumeResult()
                    onAuthenticated()
                }
            }
        }
    }

    fun onUpdate(transform: (AuthState) -> AuthState) {
        viewModel.update(transform)
    }

    fun onLogin() {
        viewModel.login()
    }

    fun onRegister() {
        if (!state.value.isLoading) onRegistration()
    }

    fun onGoogle(context: Context) {
        if (state.value.isLoading) return
        if (!googleAuth.isConfigured) {
            viewModel.showError(googleAuth.unavailableMessage)
            return
        }
        viewModel.update { it.copy(isLoading = true) }
        lifecycleScope.launch {
            try {
                when (val result = googleAuth.signIn(context)) {
                    is GoogleAuth.Result.Success -> viewModel.googleLogin(result.email, result.token)
                    is GoogleAuth.Result.Error -> viewModel.showError(result.message)
                    GoogleAuth.Result.Cancelled -> Unit
                }
            } finally {
                viewModel.update { it.copy(isLoading = false) }
            }
        }
    }
}

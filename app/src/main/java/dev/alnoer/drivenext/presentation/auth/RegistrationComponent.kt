@file:OptIn(com.arkivanov.decompose.ExperimentalDecomposeApi::class)

package dev.alnoer.drivenext.presentation.auth

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.arkivanov.decompose.jetpackcomponentcontext.JetpackComponentContext
import com.arkivanov.decompose.jetpackcomponentcontext.viewModel
import com.arkivanov.decompose.router.stack.StackNavigation
import com.arkivanov.decompose.router.stack.childStack
import com.arkivanov.decompose.router.stack.pop
import com.arkivanov.decompose.router.stack.pushNew
import dev.alnoer.drivenext.data.auth.AuthenticationRepository
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable

class RegistrationComponent(
    componentContext: JetpackComponentContext,
    repository: AuthenticationRepository,
    private val onExit: () -> Unit,
    private val onRegistered: () -> Unit
) : JetpackComponentContext by componentContext {
    private val viewModel = viewModel { AuthViewModel(repository, createSavedStateHandle()) }
    private val navigation = StackNavigation<Step>()

    val stack = childStack(
        source = navigation,
        serializer = Step.serializer(),
        initialConfiguration = Step.Credentials,
        handleBackButton = true,
        childFactory = { step, context ->
            when (step) {
                Step.Credentials -> Child.Credentials(RegistrationCredentialsComponent(
                    context, viewModel, { navigation.pushNew(Step.Profile) }, onExit
                ))
                Step.Profile -> Child.Profile(RegistrationProfileComponent(
                    context, viewModel, { navigation.pushNew(Step.Documents) }, { navigation.pop() }
                ))
                Step.Documents -> Child.Documents(RegistrationDocumentsComponent(
                    context, viewModel, { navigation.pop() }
                ))
            }
        }
    )

    init {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.RESUMED) {
                viewModel.result.filterNotNull().collect {
                    viewModel.consumeResult()
                    onRegistered()
                }
            }
        }
    }

    sealed class Child(val lifecycleOwner: LifecycleOwner) {
        class Credentials(val component: RegistrationCredentialsComponent) : Child(component)
        class Profile(val component: RegistrationProfileComponent) : Child(component)
        class Documents(val component: RegistrationDocumentsComponent) : Child(component)
    }

    @Serializable
    enum class Step { Credentials, Profile, Documents }
}

class RegistrationCredentialsComponent(
    componentContext: JetpackComponentContext,
    private val viewModel: AuthViewModel,
    private val onContinue: () -> Unit,
    private val onExit: () -> Unit
) : JetpackComponentContext by componentContext {
    val state = viewModel.state

    fun onUpdate(transform: (AuthState) -> AuthState) {
        viewModel.update(transform)
    }

    fun onNext() {
        if (viewModel.validateCredentials()) onContinue()
    }

    fun onBack() {
        onExit()
    }
}

class RegistrationProfileComponent(
    componentContext: JetpackComponentContext,
    private val viewModel: AuthViewModel,
    private val onContinue: () -> Unit,
    private val onPrevious: () -> Unit
) : JetpackComponentContext by componentContext {
    val state = viewModel.state

    fun onUpdate(transform: (AuthState) -> AuthState) {
        viewModel.update(transform)
    }

    fun onNext() {
        if (viewModel.validateProfile()) onContinue()
    }

    fun onBack() {
        onPrevious()
    }
}

class RegistrationDocumentsComponent(
    componentContext: JetpackComponentContext,
    private val viewModel: AuthViewModel,
    private val onPrevious: () -> Unit
) : JetpackComponentContext by componentContext {
    val state = viewModel.state

    fun onUpdate(transform: (AuthState) -> AuthState) {
        viewModel.update(transform)
    }

    fun onPhoto(target: PhotoTarget, uri: String) {
        viewModel.setPhoto(target, uri)
    }

    fun onError(message: String) {
        viewModel.showError(message)
    }

    fun onNext() {
        viewModel.register()
    }

    fun onBack() {
        if (!state.value.isLoading) onPrevious()
    }
}

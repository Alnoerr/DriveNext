@file:OptIn(com.arkivanov.decompose.ExperimentalDecomposeApi::class)

package dev.alnoer.drivenext.presentation.navigation

import androidx.lifecycle.LifecycleOwner
import com.arkivanov.decompose.jetpackcomponentcontext.JetpackComponentContext
import com.arkivanov.decompose.router.stack.ChildStack
import com.arkivanov.decompose.router.stack.StackNavigation
import com.arkivanov.decompose.router.stack.childStack
import com.arkivanov.decompose.router.stack.pop
import com.arkivanov.decompose.router.stack.pushNew
import com.arkivanov.decompose.router.stack.replaceAll
import com.arkivanov.decompose.value.Value
import dev.alnoer.drivenext.data.auth.AuthenticationRepository
import dev.alnoer.drivenext.data.auth.GoogleAuth
import dev.alnoer.drivenext.data.local.AppPreferences
import dev.alnoer.drivenext.presentation.auth.LoginComponent
import dev.alnoer.drivenext.presentation.auth.RegistrationComponent
import kotlinx.serialization.Serializable

class RootComponent(
    componentContext: JetpackComponentContext,
    private val repository: AuthenticationRepository,
    private val preferences: AppPreferences,
    private val hasInternet: () -> Boolean,
    private val googleAuth: GoogleAuth
) : JetpackComponentContext by componentContext {
    private val navigation = StackNavigation<Config>()

    val stack: Value<ChildStack<*, Child>> = childStack(
        source = navigation,
        serializer = Config.serializer(),
        initialConfiguration = Config.Splash,
        handleBackButton = true,
        childFactory = ::createChild
    )

    private fun createChild(config: Config, context: JetpackComponentContext): Child = when (config) {
        Config.Splash -> Child.Splash(SplashComponent(context, ::openDestination))
        Config.Onboarding -> Child.Onboarding(OnboardingComponent(context, preferences, ::openDestination))
        Config.NoConnection -> Child.NoConnection(NoConnectionComponent(context, ::openDestination))
        Config.Auth -> Child.Auth(AuthComponent(
            context, { navigation.pushNew(Config.Login) }, { navigation.pushNew(Config.Registration) }
        ))
        Config.Login -> Child.Login(LoginComponent(
            context, repository, googleAuth, ::openMain, { navigation.pushNew(Config.Registration) }
        ))
        Config.Registration -> Child.Registration(RegistrationComponent(
            context, repository, { navigation.pop() }, { navigation.replaceAll(Config.Success) }
        ))
        Config.Success -> Child.Success(RegistrationSuccessComponent(context, ::openMain))
        Config.Main -> Child.Main(MainComponent(context))
    }

    private fun openDestination() {
        navigation.replaceAll(when {
            !hasInternet() -> Config.NoConnection
            repository.hasSession && !repository.isRegistrationCompleted -> Config.Success
            repository.hasSession -> Config.Main
            !preferences.isOnboardingCompleted -> Config.Onboarding
            else -> Config.Auth
        })
    }

    private fun openMain() {
        repository.completeRegistration()
        navigation.replaceAll(Config.Main)
    }

    sealed class Child(val lifecycleOwner: LifecycleOwner) {
        class Splash(val component: SplashComponent) : Child(component)
        class Onboarding(val component: OnboardingComponent) : Child(component)
        class NoConnection(val component: NoConnectionComponent) : Child(component)
        class Auth(val component: AuthComponent) : Child(component)
        class Login(val component: LoginComponent) : Child(component)
        class Registration(val component: RegistrationComponent) : Child(component)
        class Success(val component: RegistrationSuccessComponent) : Child(component)
        class Main(val component: MainComponent) : Child(component)
    }

    @Serializable
    private enum class Config { Splash, Onboarding, NoConnection, Auth, Login, Registration, Success, Main }
}

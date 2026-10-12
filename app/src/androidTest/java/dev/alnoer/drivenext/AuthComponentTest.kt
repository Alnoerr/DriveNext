@file:OptIn(com.arkivanov.decompose.ExperimentalDecomposeApi::class)

package dev.alnoer.drivenext

import android.content.Context
import android.content.ContextWrapper
import android.content.SharedPreferences
import androidx.test.platform.app.InstrumentationRegistry
import com.arkivanov.decompose.DefaultComponentContext
import com.arkivanov.decompose.jetpackcomponentcontext.asJetpackComponentContext
import com.arkivanov.essenty.backhandler.BackDispatcher
import com.arkivanov.essenty.instancekeeper.InstanceKeeperDispatcher
import com.arkivanov.essenty.lifecycle.LifecycleRegistry
import com.arkivanov.essenty.lifecycle.destroy
import com.arkivanov.essenty.lifecycle.pause
import com.arkivanov.essenty.lifecycle.resume
import com.arkivanov.essenty.statekeeper.StateKeeperDispatcher
import dev.alnoer.drivenext.data.auth.AuthenticationRepository
import dev.alnoer.drivenext.data.auth.GoogleAuth
import dev.alnoer.drivenext.data.local.AppPreferences
import dev.alnoer.drivenext.presentation.auth.RegistrationComponent
import dev.alnoer.drivenext.presentation.auth.LoginComponent
import dev.alnoer.drivenext.presentation.navigation.RootComponent
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withTimeout
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertSame
import org.junit.Assert.assertTrue
import org.junit.Test
import java.util.UUID

class AuthComponentTest {
    private val instrumentation = InstrumentationRegistry.getInstrumentation()
    private val preferencesName = "component_test_${UUID.randomUUID()}"
    private val context = object : ContextWrapper(instrumentation.targetContext) {
        override fun getSharedPreferences(name: String, mode: Int): SharedPreferences =
            super.getSharedPreferences(preferencesName, mode)
    }
    private val hosts = mutableListOf<Host>()

    @After
    fun cleanUp() {
        onMain { hosts.forEach { it.destroy() } }
        context.deleteSharedPreferences(preferencesName)
    }

    @Test
    fun delayedLoginNavigatesThroughRecreatedRoot() = runBlocking {
        val repository = FakeRepository()
        val preferences = AppPreferences(context).apply { isOnboardingCompleted = true }
        val googleAuth = GoogleAuth(context)
        val firstHost = onMain { Host().also { hosts.add(it) } }
        val firstRoot = onMain {
            RootComponent(firstHost.context, repository, preferences, { true }, googleAuth).also { firstHost.resume() }
        }
        waitUntil { firstRoot.stack.value.active.instance is RootComponent.Child.Auth }
        val firstLogin = onMain {
            (firstRoot.stack.value.active.instance as RootComponent.Child.Auth).component.onLogin()
            (firstRoot.stack.value.active.instance as RootComponent.Child.Login).component.also {
                it.onUpdate { state -> state.copy(email = "name@domainname.com", password = "password") }
                it.onLogin()
            }
        }
        withTimeout(5000) { repository.loginStarted.await() }
        instrumentation.waitForIdleSync()
        val secondHost = onMain {
            val savedState = firstHost.stateKeeper.save()
            firstHost.detach()
            Host(StateKeeperDispatcher(savedState), firstHost.instanceKeeper).also { hosts.add(it) }
        }
        val secondRoot = onMain {
            RootComponent(secondHost.context, repository, preferences, { true }, googleAuth).also { secondHost.resume() }
        }
        onMain {
            val secondLogin = (secondRoot.stack.value.active.instance as RootComponent.Child.Login).component
            assertSame(firstLogin.state, secondLogin.state)
            assertTrue(secondLogin.state.value.isLoading)
            repository.finishLogin.complete(Unit)
        }
        waitUntil { secondRoot.stack.value.active.instance is RootComponent.Child.Main }
        onMain {
            assertTrue(firstRoot.stack.value.active.instance is RootComponent.Child.Login)
            assertTrue(secondRoot.stack.value.backStack.isEmpty())
        }
    }

    @Test
    fun backgroundLoginResultSurvivesProcessRecreation() = runBlocking {
        val repository = FakeRepository()
        val googleAuth = GoogleAuth(context)
        var originalNavigations = 0
        var restoredNavigations = 0
        val firstHost = onMain { Host().also { hosts.add(it) } }
        val first = onMain {
            LoginComponent(firstHost.context, repository, googleAuth, { originalNavigations++ }, {}).also { firstHost.resume() }
        }
        onMain {
            first.onUpdate { it.copy(email = "name@domainname.com", password = "password") }
            first.onLogin()
        }
        withTimeout(5000) { repository.loginStarted.await() }
        onMain {
            firstHost.pause()
            repository.finishLogin.complete(Unit)
        }
        waitUntil { repository.hasSession && !first.state.value.isLoading }
        val secondHost = onMain {
            val savedState = firstHost.stateKeeper.save()
            firstHost.destroy()
            Host(StateKeeperDispatcher(savedState)).also { hosts.add(it) }
        }
        onMain {
            LoginComponent(secondHost.context, repository, googleAuth, { restoredNavigations++ }, {})
            secondHost.resume()
        }
        waitUntil { restoredNavigations == 1 }
        onMain {
            assertEquals(0, originalNavigations)
            secondHost.pause()
            secondHost.resume()
            assertEquals(1, restoredNavigations)
        }
    }

    @Test
    fun removingLoginCancelsItsRequest() = runBlocking {
        val repository = FakeRepository()
        val preferences = AppPreferences(context).apply { isOnboardingCompleted = true }
        val host = onMain { Host().also { hosts.add(it) } }
        val root = onMain {
            RootComponent(host.context, repository, preferences, { true }, GoogleAuth(context)).also { host.resume() }
        }
        waitUntil { root.stack.value.active.instance is RootComponent.Child.Auth }
        onMain {
            (root.stack.value.active.instance as RootComponent.Child.Auth).component.onLogin()
            val login = (root.stack.value.active.instance as RootComponent.Child.Login).component
            login.onUpdate { it.copy(email = "name@domainname.com", password = "password") }
            login.onLogin()
        }
        withTimeout(5000) { repository.loginStarted.await() }
        onMain { host.backDispatcher.back() }
        withTimeout(5000) { repository.loginCancelled.await() }
        onMain {
            assertTrue(root.stack.value.active.instance is RootComponent.Child.Auth)
            assertFalse(repository.hasSession)
        }
    }

    @Test
    fun systemBackPopsRegistrationStepsBeforeLeavingRegistration() = runBlocking {
        val repository = FakeRepository()
        val preferences = AppPreferences(context).apply { isOnboardingCompleted = true }
        val host = onMain { Host().also { hosts.add(it) } }
        val root = onMain {
            RootComponent(host.context, repository, preferences, { true }, GoogleAuth(context)).also { host.resume() }
        }
        waitUntil { root.stack.value.active.instance is RootComponent.Child.Auth }
        onMain {
            (root.stack.value.active.instance as RootComponent.Child.Auth).component.onRegister()
            val registration = (root.stack.value.active.instance as RootComponent.Child.Registration).component
            val credentials = (registration.stack.value.active.instance as RegistrationComponent.Child.Credentials).component
            credentials.onUpdate {
                it.copy(email = "name@domainname.com", password = "password", repeatedPassword = "password", acceptedTerms = true)
            }
            credentials.onNext()
            host.backDispatcher.back()
            assertTrue(root.stack.value.active.instance is RootComponent.Child.Registration)
            assertTrue(registration.stack.value.active.instance is RegistrationComponent.Child.Credentials)
            assertEquals("name@domainname.com", credentials.state.value.email)
            host.backDispatcher.back()
            assertTrue(root.stack.value.active.instance is RootComponent.Child.Auth)
        }
    }

    @Test
    fun registrationSuccessClearsLoginAndRegistrationFromRootStack() = runBlocking {
        val repository = FakeRepository()
        val preferences = AppPreferences(context).apply { isOnboardingCompleted = true }
        val host = onMain { Host().also { hosts.add(it) } }
        val root = onMain {
            RootComponent(host.context, repository, preferences, { true }, GoogleAuth(context)).also { host.resume() }
        }
        waitUntil { root.stack.value.active.instance is RootComponent.Child.Auth }
        onMain {
            (root.stack.value.active.instance as RootComponent.Child.Auth).component.onLogin()
            (root.stack.value.active.instance as RootComponent.Child.Login).component.onRegister()
            val registration = (root.stack.value.active.instance as RootComponent.Child.Registration).component
            val credentials = (registration.stack.value.active.instance as RegistrationComponent.Child.Credentials).component
            credentials.onUpdate {
                it.copy(email = "name@domainname.com", password = "password", repeatedPassword = "password", acceptedTerms = true)
            }
            credentials.onNext()
            val profile = (registration.stack.value.active.instance as RegistrationComponent.Child.Profile).component
            profile.onUpdate { it.copy(lastName = "Ivanov", firstName = "Ivan", birthDate = "12/31/2000", gender = "Мужской") }
            profile.onNext()
            val documents = (registration.stack.value.active.instance as RegistrationComponent.Child.Documents).component
            documents.onUpdate {
                it.copy(licenseNumber = "1234567890", issueDate = "01/01/2020", licensePhoto = "content://test/license", passportPhoto = "content://test/passport")
            }
            documents.onNext()
        }
        waitUntil { root.stack.value.active.instance is RootComponent.Child.Success }
        onMain {
            assertTrue(root.stack.value.backStack.isEmpty())
            (root.stack.value.active.instance as RootComponent.Child.Success).component.onNext()
            assertTrue(root.stack.value.active.instance is RootComponent.Child.Main)
            assertTrue(root.stack.value.backStack.isEmpty())
            assertTrue(repository.isRegistrationCompleted)
        }
    }

    @Test
    fun registrationRetainsFieldsAcrossBackAndRestoresAfterProcessRecreation() {
        val repository = FakeRepository()
        val firstHost = onMain { Host().also { hosts.add(it) } }
        val first = onMain {
            RegistrationComponent(firstHost.context, repository, {}, {}).also { firstHost.resume() }
        }
        onMain {
            val credentials = (first.stack.value.active.instance as RegistrationComponent.Child.Credentials).component
            credentials.onUpdate {
                it.copy(email = "name@domainname.com", password = "password", repeatedPassword = "password", acceptedTerms = true)
            }
            credentials.onNext()
            val profile = (first.stack.value.active.instance as RegistrationComponent.Child.Profile).component
            profile.onUpdate { it.copy(lastName = "Ivanov", firstName = "Ivan", birthDate = "12/31/2000", gender = "Мужской") }
            profile.onBack()
            assertEquals("password", credentials.state.value.password)
            credentials.onNext()
        }
        instrumentation.waitForIdleSync()
        val secondHost = onMain {
            val savedState = firstHost.stateKeeper.save()
            firstHost.destroy()
            Host(StateKeeperDispatcher(savedState)).also { hosts.add(it) }
        }
        val restored = onMain {
            RegistrationComponent(secondHost.context, repository, {}, {}).also { secondHost.resume() }
        }
        onMain {
            val profile = (restored.stack.value.active.instance as RegistrationComponent.Child.Profile).component
            assertEquals("Ivanov", profile.state.value.lastName)
            assertEquals("12/31/2000", profile.state.value.birthDate)
            profile.onBack()
            val credentials = (restored.stack.value.active.instance as RegistrationComponent.Child.Credentials).component
            assertEquals("name@domainname.com", credentials.state.value.email)
            assertTrue(credentials.state.value.acceptedTerms)
        }
    }

    private suspend fun waitUntil(condition: () -> Boolean) {
        withTimeout(5000) {
            while (!onMain(condition)) delay(10)
        }
    }

    private fun <T> onMain(action: () -> T): T {
        var result: Result<T>? = null
        instrumentation.runOnMainSync { result = runCatching(action) }
        return result!!.getOrThrow()
    }

    private class Host(
        val stateKeeper: StateKeeperDispatcher = StateKeeperDispatcher(),
        val instanceKeeper: InstanceKeeperDispatcher = InstanceKeeperDispatcher()
    ) {
        private val lifecycle = LifecycleRegistry()
        val backDispatcher = BackDispatcher()
        val context = DefaultComponentContext(
            lifecycle = lifecycle,
            stateKeeper = stateKeeper,
            instanceKeeper = instanceKeeper,
            backHandler = backDispatcher
        ).asJetpackComponentContext()

        fun resume() {
            lifecycle.resume()
        }

        fun detach() {
            lifecycle.destroy()
        }

        fun pause() {
            lifecycle.pause()
        }

        fun destroy() {
            lifecycle.destroy()
            instanceKeeper.destroy()
        }
    }

    private class FakeRepository : AuthenticationRepository {
        override var hasSession = false
        override var email = ""
        override var isRegistrationCompleted = true
        val loginStarted = CompletableDeferred<Unit>()
        val finishLogin = CompletableDeferred<Unit>()
        val loginCancelled = CompletableDeferred<Unit>()

        override suspend fun login(email: String, password: String) {
            loginStarted.complete(Unit)
            try {
                finishLogin.await()
                this.email = email
                hasSession = true
            } catch (exception: CancellationException) {
                loginCancelled.complete(Unit)
                throw exception
            }
        }

        override suspend fun register(email: String, password: String, profile: Map<String, String>) {
            this.email = email
            hasSession = true
            isRegistrationCompleted = false
        }

        override fun saveGoogleSession(email: String, token: String) {
            this.email = email
            hasSession = true
        }

        override fun completeRegistration() {
            isRegistrationCompleted = true
        }
    }
}

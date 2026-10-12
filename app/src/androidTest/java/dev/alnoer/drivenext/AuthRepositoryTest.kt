package dev.alnoer.drivenext

import android.content.Context
import android.content.ContextWrapper
import android.content.SharedPreferences
import androidx.test.platform.app.InstrumentationRegistry
import dev.alnoer.drivenext.data.auth.AuthRepository
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import java.util.UUID

class AuthRepositoryTest {
    private val preferencesName = "auth_test_${UUID.randomUUID()}"
    private val context = object : ContextWrapper(InstrumentationRegistry.getInstrumentation().targetContext) {
        override fun getSharedPreferences(name: String, mode: Int): SharedPreferences =
            super.getSharedPreferences(preferencesName, mode)
    }

    @After
    fun clearPreferences() {
        context.deleteSharedPreferences(preferencesName)
    }

    @Test
    fun registrationPersistsAccountAndSessionWithoutPlainPassword() = runBlocking {
        val repository = AuthRepository(context)
        assertFalse(repository.hasSession)
        repository.register("NAME@domainname.com", "secret-password", mapOf("firstName" to "Alex"))
        val restored = AuthRepository(context)
        assertTrue(restored.hasSession)
        assertEquals("name@domainname.com", restored.email)
        assertFalse(restored.isRegistrationCompleted)
        val stored = context.getSharedPreferences("auth_preferences", Context.MODE_PRIVATE)
            .getString("accounts", "").orEmpty()
        assertFalse(stored.contains("secret-password"))
        restored.completeRegistration()
        assertTrue(AuthRepository(context).isRegistrationCompleted)
    }

    @Test
    fun loginRejectsWrongPasswordAndAcceptsRegisteredAccount() = runBlocking {
        val repository = AuthRepository(context)
        repository.register("name@domainname.ru", "password", emptyMap())
        assertTrue(runCatching { repository.login("name@domainname.ru", "wrong") }.isFailure)
        assertTrue(runCatching { repository.login("unknown@domainname.ru", "password") }.isFailure)
        repository.login(" NAME@domainname.ru ", "password")
        assertTrue(repository.hasSession)
        assertEquals("name@domainname.ru", repository.email)
    }

    @Test
    fun duplicateRegistrationKeepsOriginalAccount() = runBlocking {
        val repository = AuthRepository(context)
        repository.register("name@domainname.com", "password", emptyMap())
        assertTrue(runCatching { repository.register("NAME@domainname.com", "other", emptyMap()) }.isFailure)
        repository.login("name@domainname.com", "password")
    }
}

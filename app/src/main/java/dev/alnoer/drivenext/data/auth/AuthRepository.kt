package dev.alnoer.drivenext.data.auth

import android.content.Context
import android.util.Base64
import androidx.core.content.edit
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.security.MessageDigest
import java.security.SecureRandom
import java.util.UUID
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.PBEKeySpec

class AuthRepository(context: Context) : AuthenticationRepository {
    private val preferences = context.getSharedPreferences("auth_preferences", Context.MODE_PRIVATE)

    override val hasSession: Boolean
        get() = !preferences.getString("token", null).isNullOrBlank()

    override val email: String
        get() = preferences.getString("email", "").orEmpty()

    override suspend fun register(email: String, password: String, profile: Map<String, String>) =
        withContext(Dispatchers.IO) {
            val accounts = JSONObject(preferences.getString("accounts", "{}").orEmpty())
            val key = email.trim().lowercase()
            check(!accounts.has(key)) { "Аккаунт с этой электронной почтой уже существует." }
            val salt = ByteArray(16).also { SecureRandom().nextBytes(it) }
            accounts.put(key, JSONObject().apply {
                put("salt", Base64.encodeToString(salt, Base64.NO_WRAP))
                put("password", Base64.encodeToString(hash(password, salt), Base64.NO_WRAP))
                put("profile", JSONObject(profile))
            })
            preferences.edit {
                putString("accounts", accounts.toString())
                putString("email", key)
                putString("token", UUID.randomUUID().toString())
                putBoolean("registration_completed", false)
            }
        }

    override suspend fun login(email: String, password: String) = withContext(Dispatchers.IO) {
        val key = email.trim().lowercase()
        val account = JSONObject(preferences.getString("accounts", "{}").orEmpty()).optJSONObject(key)
        check(account != null) { "Неверная электронная почта или пароль." }
        val salt = Base64.decode(account.getString("salt"), Base64.NO_WRAP)
        val expected = Base64.decode(account.getString("password"), Base64.NO_WRAP)
        check(MessageDigest.isEqual(expected, hash(password, salt))) {
            "Неверная электронная почта или пароль."
        }
        saveSession(key, UUID.randomUUID().toString())
    }

    override fun saveGoogleSession(email: String, token: String) {
        saveSession(email, token)
    }

    override val isRegistrationCompleted: Boolean
        get() = preferences.getBoolean("registration_completed", true)

    override fun completeRegistration() {
        preferences.edit { putBoolean("registration_completed", true) }
    }

    private fun saveSession(email: String, token: String) {
        preferences.edit {
            putString("email", email)
            putString("token", token)
            putBoolean("registration_completed", true)
        }
    }

    private fun hash(password: String, salt: ByteArray): ByteArray {
        val spec = PBEKeySpec(password.toCharArray(), salt, 120000, 256)
        return try {
            SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256").generateSecret(spec).encoded
        } finally {
            spec.clearPassword()
        }
    }
}

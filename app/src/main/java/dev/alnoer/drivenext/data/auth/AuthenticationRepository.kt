package dev.alnoer.drivenext.data.auth

interface AuthenticationRepository {
    val hasSession: Boolean
    val email: String
    val isRegistrationCompleted: Boolean

    suspend fun register(email: String, password: String, profile: Map<String, String>)
    suspend fun login(email: String, password: String)
    fun saveGoogleSession(email: String, token: String)
    fun completeRegistration()
}

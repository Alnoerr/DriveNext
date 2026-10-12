package dev.alnoer.drivenext.presentation.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import dev.alnoer.drivenext.data.auth.AuthenticationRepository
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

@Serializable
data class AuthState(
    val email: String = "",
    val password: String = "",
    val repeatedPassword: String = "",
    val acceptedTerms: Boolean = false,
    val lastName: String = "",
    val firstName: String = "",
    val middleName: String = "",
    val birthDate: String = "",
    val gender: String = "",
    val licenseNumber: String = "",
    val issueDate: String = "",
    val profilePhoto: String = "",
    val licensePhoto: String = "",
    val passportPhoto: String = "",
    val pendingPhoto: String = "",
    val pendingPhotoTarget: PhotoTarget? = null,
    val isLoading: Boolean = false,
    val error: String? = null
) {
    val canLogin: Boolean
        get() = email.isNotBlank() && password.isNotBlank() && !isLoading

    val canContinueProfile: Boolean
        get() = lastName.isNotBlank() && firstName.isNotBlank() && birthDate.isNotBlank() && gender.isNotBlank()

    val canRegister: Boolean
        get() = licenseNumber.isNotBlank() && issueDate.isNotBlank() && licensePhoto.isNotBlank() &&
            passportPhoto.isNotBlank() && !isLoading
}

@Serializable
enum class PhotoTarget { Profile, License, Passport }

class AuthViewModel(
    private val repository: AuthenticationRepository,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {
    private val mutableState = MutableStateFlow(
        savedStateHandle.get<String>("auth_state")?.let {
            runCatching { Json.decodeFromString<AuthState>(it).copy(isLoading = false) }.getOrNull()
        } ?: AuthState(email = repository.email)
    )
    val state = mutableState.asStateFlow()
    private val mutableResult = MutableStateFlow(
        savedStateHandle.get<String>("auth_result")?.let { name -> Result.entries.find { it.name == name } }
    )
    val result = mutableResult.asStateFlow()

    init {
        viewModelScope.launch {
            state.collect { savedStateHandle["auth_state"] = Json.encodeToString(it) }
        }
    }

    fun update(transform: (AuthState) -> AuthState) {
        mutableState.update { transform(it).copy(error = null) }
    }

    fun showError(message: String) {
        mutableState.update { it.copy(error = message, isLoading = false) }
    }

    fun consumeResult() {
        savedStateHandle.set<String>("auth_result", null)
        mutableResult.value = null
    }

    fun validateCredentials(): Boolean {
        val current = state.value
        val error = when {
            !AuthValidation.isEmailValid(current.email) -> "Введите корректный адрес электронной почты."
            current.password.isBlank() -> "Пожалуйста, заполните все обязательные поля."
            current.password != current.repeatedPassword -> "Пароли не совпадают."
            !current.acceptedTerms -> "Необходимо согласиться с условиями обслуживания и политикой конфиденциальности."
            else -> null
        }
        if (error != null) showError(error)
        return error == null
    }

    fun validateProfile(): Boolean {
        val current = state.value
        val error = when {
            !current.canContinueProfile -> "Пожалуйста, заполните все обязательные поля."
            !AuthValidation.isBirthDateValid(current.birthDate) -> "Введите корректную дату рождения."
            else -> null
        }
        if (error != null) showError(error)
        return error == null
    }

    fun setPhoto(target: PhotoTarget, uri: String) {
        update {
            when (target) {
                PhotoTarget.Profile -> it.copy(profilePhoto = uri)
                PhotoTarget.License -> it.copy(licensePhoto = uri)
                PhotoTarget.Passport -> it.copy(passportPhoto = uri)
            }
        }
    }

    fun login() {
        if (state.value.isLoading) return
        if (!AuthValidation.isEmailValid(state.value.email)) {
            showError("Введите корректный адрес электронной почты.")
            return
        }
        if (state.value.password.isBlank()) {
            showError("Пожалуйста, заполните все обязательные поля.")
            return
        }
        val current = state.value
        submit(Result.Login) { repository.login(current.email, current.password) }
    }

    fun register() {
        if (state.value.isLoading || !validateCredentials() || !validateProfile()) return
        val current = state.value
        val error = when {
            current.licenseNumber.isBlank() || current.issueDate.isBlank() -> "Пожалуйста, заполните все обязательные поля."
            current.licenseNumber.length != 10 -> "Номер водительского удостоверения должен содержать 10 символов."
            !AuthValidation.isIssueDateValid(current.issueDate, current.birthDate) -> "Введите корректную дату выдачи."
            current.licensePhoto.isBlank() || current.passportPhoto.isBlank() -> "Пожалуйста, загрузите все необходимые фото."
            else -> null
        }
        if (error != null) {
            showError(error)
            return
        }
        submit(Result.Registration) {
            repository.register(current.email, current.password, mapOf(
                "lastName" to current.lastName.trim(),
                "firstName" to current.firstName.trim(),
                "middleName" to current.middleName.trim(),
                "birthDate" to current.birthDate,
                "gender" to current.gender,
                "licenseNumber" to current.licenseNumber,
                "issueDate" to current.issueDate,
                "profilePhoto" to current.profilePhoto,
                "licensePhoto" to current.licensePhoto,
                "passportPhoto" to current.passportPhoto
            ))
        }
    }

    fun googleLogin(email: String, token: String) {
        repository.saveGoogleSession(email, token)
        mutableState.value = AuthState(email = email)
        publishResult(Result.Login)
    }

    private fun submit(result: Result, action: suspend () -> Unit) {
        mutableState.update { it.copy(isLoading = true, error = null) }
        viewModelScope.launch {
            try {
                action()
                mutableState.value = AuthState(email = repository.email)
                publishResult(result)
            } catch (exception: CancellationException) {
                throw exception
            } catch (exception: Exception) {
                showError(exception.message ?: "Не удалось выполнить запрос. Повторите попытку.")
            } finally {
                mutableState.update { it.copy(isLoading = false) }
            }
        }
    }

    private fun publishResult(result: Result) {
        savedStateHandle["auth_result"] = result.name
        mutableResult.value = result
    }

    enum class Result { Login, Registration }
}

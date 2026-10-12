package dev.alnoer.drivenext.data.auth

import android.content.Context
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.credentials.exceptions.GetCredentialCancellationException
import androidx.credentials.exceptions.NoCredentialException
import com.google.android.libraries.identity.googleid.GetSignInWithGoogleOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import dev.alnoer.drivenext.R
import kotlinx.coroutines.CancellationException

class GoogleAuth(context: Context) {
    private val applicationContext = context.applicationContext
    private val clientId = applicationContext.getString(R.string.google_web_client_id)

    val isConfigured: Boolean
        get() = clientId.isNotBlank()

    val unavailableMessage: String
        get() = applicationContext.getString(R.string.auth_google_unavailable)

    suspend fun signIn(context: Context): Result = try {
        val request = GetCredentialRequest.Builder()
            .addCredentialOption(GetSignInWithGoogleOption.Builder(clientId).build())
            .build()
        val credential = CredentialManager.create(context).getCredential(context, request).credential
        check(credential is CustomCredential && credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL)
        val googleCredential = GoogleIdTokenCredential.createFrom(credential.data)
        Result.Success(googleCredential.id, googleCredential.idToken)
    } catch (_: GetCredentialCancellationException) {
        Result.Cancelled
    } catch (_: NoCredentialException) {
        Result.Error(applicationContext.getString(R.string.auth_google_error))
    } catch (exception: CancellationException) {
        throw exception
    } catch (_: Exception) {
        Result.Error(applicationContext.getString(R.string.auth_google_error))
    }

    sealed interface Result {
        data class Success(val email: String, val token: String) : Result
        data class Error(val message: String) : Result
        data object Cancelled : Result
    }
}

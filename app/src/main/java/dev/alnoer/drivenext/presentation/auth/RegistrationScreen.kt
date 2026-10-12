package dev.alnoer.drivenext.presentation.auth

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Checkbox
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.PreviewScreenSizes
import dev.alnoer.drivenext.R
import dev.alnoer.drivenext.presentation.theme.DriveNextTheme

@Composable
fun RegistrationCredentialsScreen(
    state: AuthState,
    onUpdate: ((AuthState) -> AuthState) -> Unit,
    onNext: () -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    AuthForm(
        stringResource(R.string.auth_create_title), stringResource(R.string.auth_next),
        onNext, onBack, modifier, error = state.error
    ) {
        AuthField(
            stringResource(R.string.auth_email), stringResource(R.string.auth_email_placeholder),
            state.email, { value -> onUpdate { it.copy(email = value) } }, keyboardType = KeyboardType.Email
        )
        AuthField(
            stringResource(R.string.auth_password_create), stringResource(R.string.auth_password_placeholder),
            state.password, { value -> onUpdate { it.copy(password = value) } }, password = true
        )
        AuthField(
            stringResource(R.string.auth_password_repeat), stringResource(R.string.auth_password_placeholder),
            state.repeatedPassword, { value -> onUpdate { it.copy(repeatedPassword = value) } }, password = true
        )
        Row(
            Modifier.fillMaxWidth().clickable { onUpdate { it.copy(acceptedTerms = !it.acceptedTerms) } },
            verticalAlignment = Alignment.CenterVertically
        ) {
            Checkbox(state.acceptedTerms, { value -> onUpdate { it.copy(acceptedTerms = value) } })
            Text(stringResource(R.string.auth_terms), style = MaterialTheme.typography.bodyMedium)
        }
    }
}

@Composable
fun RegistrationProfileScreen(
    state: AuthState,
    onUpdate: ((AuthState) -> AuthState) -> Unit,
    onNext: () -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    AuthForm(
        stringResource(R.string.auth_create_title), stringResource(R.string.auth_next),
        onNext, onBack, modifier, enabled = state.canContinueProfile, error = state.error
    ) {
        AuthField(
            stringResource(R.string.auth_last_name), stringResource(R.string.auth_last_name_placeholder),
            state.lastName, { value -> onUpdate { it.copy(lastName = value) } }
        )
        AuthField(
            stringResource(R.string.auth_first_name), stringResource(R.string.auth_first_name_placeholder),
            state.firstName, { value -> onUpdate { it.copy(firstName = value) } }
        )
        AuthField(
            stringResource(R.string.auth_middle_name), stringResource(R.string.auth_middle_name_placeholder),
            state.middleName, { value -> onUpdate { it.copy(middleName = value) } }
        )
        AuthDateField(
            stringResource(R.string.auth_birth_date), stringResource(R.string.auth_birth_date_placeholder),
            state.birthDate, "MM/dd/uuuu", { value -> onUpdate { it.copy(birthDate = value) } }
        )
        Text(stringResource(R.string.auth_gender), style = MaterialTheme.typography.bodyMedium)
        Row(horizontalArrangement = Arrangement.SpaceBetween) {
            listOf(R.string.auth_male, R.string.auth_female).forEach { resource ->
                val gender = stringResource(resource)
                Row(
                    Modifier.clickable { onUpdate { it.copy(gender = gender) } },
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    RadioButton(state.gender == gender, { onUpdate { it.copy(gender = gender) } })
                    Text(gender, style = MaterialTheme.typography.bodyMedium)
                }
            }
        }
    }
}

@PreviewScreenSizes
@Composable
private fun RegistrationCredentialsScreenPreview() {
    DriveNextTheme {
        Surface { RegistrationCredentialsScreen(AuthState(), {}, {}, {}) }
    }
}

@PreviewScreenSizes
@Composable
private fun RegistrationProfileScreenPreview() {
    DriveNextTheme {
        Surface { RegistrationProfileScreen(AuthState(), {}, {}, {}) }
    }
}

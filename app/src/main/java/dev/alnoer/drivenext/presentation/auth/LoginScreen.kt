package dev.alnoer.drivenext.presentation.auth

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.PreviewScreenSizes
import androidx.compose.ui.unit.dp
import dev.alnoer.drivenext.R
import dev.alnoer.drivenext.presentation.theme.DriveNextButton
import dev.alnoer.drivenext.presentation.theme.DriveNextTheme

@Composable
fun LoginScreen(
    state: AuthState,
    onUpdate: ((AuthState) -> AuthState) -> Unit,
    onLogin: () -> Unit,
    onGoogle: () -> Unit,
    onRegister: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier.fillMaxSize().imePadding().padding(horizontal = 24.dp).verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Spacer(Modifier.height(64.dp))
        Text(stringResource(R.string.auth_login_title), style = MaterialTheme.typography.titleLarge, textAlign = TextAlign.Center)
        Text(stringResource(R.string.auth_login_description), style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Spacer(Modifier.height(8.dp))
        AuthField(
            stringResource(R.string.auth_email), stringResource(R.string.auth_email_placeholder),
            state.email, { value -> onUpdate { it.copy(email = value) } }, keyboardType = KeyboardType.Email
        )
        AuthField(
            stringResource(R.string.auth_password), stringResource(R.string.auth_password_placeholder),
            state.password, { value -> onUpdate { it.copy(password = value) } }, password = true
        )
        if (state.error != null) Text(state.error, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodyMedium)
        Spacer(Modifier.height(16.dp))
        DriveNextButton(onLogin, enabled = state.canLogin) {
            Text(stringResource(if (state.isLoading) R.string.auth_loading else R.string.auth_login))
        }
        OutlinedButton(
            onGoogle,
            enabled = !state.isLoading,
            modifier = Modifier.widthIn(max = dimensionResource(R.dimen.button_max_width)).fillMaxWidth().height(52.dp),
            shape = RoundedCornerShape(8.dp)
        ) {
            Icon(painterResource(R.drawable.ic_google), null, Modifier.size(20.dp), tint = Color.Unspecified)
            Spacer(Modifier.width(12.dp))
            Text(stringResource(R.string.auth_google))
        }
        Spacer(Modifier.height(48.dp))
        TextButton(onRegister, enabled = !state.isLoading) { Text(stringResource(R.string.auth_register)) }
        Spacer(Modifier.height(24.dp))
    }
}

@PreviewScreenSizes
@Composable
private fun LoginScreenPreview() {
    DriveNextTheme {
        Surface { LoginScreen(AuthState(), {}, {}, {}, {}) }
    }
}

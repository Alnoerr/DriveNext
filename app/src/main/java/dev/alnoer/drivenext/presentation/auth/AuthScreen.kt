package dev.alnoer.drivenext.presentation.auth

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.PreviewScreenSizes
import androidx.compose.ui.unit.dp
import dev.alnoer.drivenext.R
import dev.alnoer.drivenext.presentation.theme.DriveNextButton
import dev.alnoer.drivenext.presentation.theme.DriveNextTheme

@Composable
fun AuthScreen(
    onLogin: () -> Unit,
    onRegister: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier.fillMaxSize().padding(horizontal = 24.dp), horizontalAlignment = Alignment.CenterHorizontally) {
        Column(
            Modifier.weight(1f).fillMaxWidth().verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.Center
        ) {
            Text(stringResource(R.string.app_name), color = MaterialTheme.colorScheme.primary, style = MaterialTheme.typography.titleLarge)
            Spacer(Modifier.height(8.dp))
            Text(stringResource(R.string.slogan), style = MaterialTheme.typography.bodyMedium)
            Spacer(Modifier.height(48.dp))
            Image(
                painterResource(R.drawable.splash_logo),
                contentDescription = null,
                modifier = Modifier.size(280.dp).align(Alignment.CenterHorizontally)
            )
            Spacer(Modifier.height(32.dp))
        }
        DriveNextButton(onLogin) { Text(stringResource(R.string.auth_login)) }
        Spacer(Modifier.height(16.dp))
        OutlinedButton(
            onRegister,
            modifier = Modifier.widthIn(max = dimensionResource(R.dimen.button_max_width)).fillMaxWidth().height(52.dp),
            shape = RoundedCornerShape(8.dp)
        ) { Text(stringResource(R.string.auth_register)) }
        Spacer(Modifier.height(48.dp))
    }
}

@PreviewScreenSizes
@Composable
private fun AuthScreenPreview() {
    DriveNextTheme {
        Surface { AuthScreen({}, {}) }
    }
}

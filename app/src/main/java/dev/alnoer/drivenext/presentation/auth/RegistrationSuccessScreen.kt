package dev.alnoer.drivenext.presentation.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.PreviewScreenSizes
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.alnoer.drivenext.R
import dev.alnoer.drivenext.presentation.theme.DriveNextButton
import dev.alnoer.drivenext.presentation.theme.DriveNextTheme

@Composable
fun RegistrationSuccessScreen(onNext: () -> Unit, modifier: Modifier = Modifier) {
    Column(modifier.fillMaxSize().padding(horizontal = 24.dp), horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            stringResource(R.string.auth_success_title),
            modifier = Modifier.padding(vertical = 24.dp),
            style = MaterialTheme.typography.titleLarge.copy(fontSize = 20.sp),
            textAlign = TextAlign.Center
        )
        Column(
            Modifier.weight(1f).fillMaxWidth().verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                painterResource(R.drawable.ic_check), null,
                Modifier.size(82.dp).background(MaterialTheme.colorScheme.primary, CircleShape).padding(20.dp),
                tint = MaterialTheme.colorScheme.onPrimary
            )
            Spacer(Modifier.height(40.dp))
            Text(stringResource(R.string.auth_congratulations), style = MaterialTheme.typography.titleLarge)
            Spacer(Modifier.height(32.dp))
            Text(stringResource(R.string.auth_success_description), textAlign = TextAlign.Center, style = MaterialTheme.typography.bodyMedium)
        }
        Spacer(Modifier.height(16.dp))
        DriveNextButton(onNext) { Text(stringResource(R.string.auth_next)) }
        Spacer(Modifier.height(24.dp))
    }
}

@PreviewScreenSizes
@Composable
private fun RegistrationSuccessScreenPreview() {
    DriveNextTheme {
        Surface { RegistrationSuccessScreen({}) }
    }
}

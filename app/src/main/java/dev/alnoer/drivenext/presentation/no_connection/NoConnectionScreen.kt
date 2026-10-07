package dev.alnoer.drivenext.presentation.no_connection

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
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
import dev.alnoer.drivenext.R
import dev.alnoer.drivenext.presentation.theme.DriveNextButton
import dev.alnoer.drivenext.presentation.theme.DriveNextTheme

@Composable
fun NoConnectionScreen(
    onRetry: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = modifier
            .padding(horizontal = 16.dp)
            .fillMaxSize()
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .verticalScroll(rememberScrollState())
        ) {
            Image(
                painterResource(R.drawable.ic_no_connection),
                contentDescription = null,
                modifier = Modifier.size(96.dp)
            )

            Spacer(Modifier.height(32.dp))

            Text(
                stringResource(R.string.error_title_no_connection),
                style = MaterialTheme.typography.titleLarge,
                textAlign = TextAlign.Center
            )

            Spacer(Modifier.height(24.dp))

            Text(
                stringResource(R.string.error_body_no_connection),
                textAlign = TextAlign.Center
            )
        }

        Spacer(Modifier.height(16.dp))

        DriveNextButton(onClick = onRetry) {
            Text(stringResource(R.string.error_action_retry))
        }

        Spacer(Modifier.height(16.dp))
    }
}


@PreviewScreenSizes
@Composable
private fun NoConnectionScreenPreview() {
    DriveNextTheme {
        Surface {
            NoConnectionScreen({})
        }
    }
}

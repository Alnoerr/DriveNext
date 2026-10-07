package dev.alnoer.drivenext.presentation.theme

import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.unit.dp
import dev.alnoer.drivenext.R

@Composable
fun DriveNextButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable (RowScope.() -> Unit)
) {
    Button(
        onClick = onClick,
        modifier = modifier
            .widthIn(max = dimensionResource(R.dimen.button_max_width))
            .fillMaxWidth()
            .heightIn(min = 52.dp),
        shape = RoundedCornerShape(8.dp),
        content = content
    )
}

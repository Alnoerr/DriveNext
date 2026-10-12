package dev.alnoer.drivenext.presentation.auth

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.SelectableDates
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import dev.alnoer.drivenext.R
import dev.alnoer.drivenext.presentation.theme.DriveNextButton
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter

@Composable
fun AuthForm(
    title: String,
    button: String,
    onNext: () -> Unit,
    onBack: (() -> Unit)?,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    error: String? = null,
    content: @Composable ColumnScope.() -> Unit
) {
    Column(modifier.fillMaxSize().imePadding().padding(horizontal = 24.dp), horizontalAlignment = Alignment.CenterHorizontally) {
        Box(Modifier.fillMaxWidth().height(72.dp), contentAlignment = Alignment.Center) {
            Text(title, style = MaterialTheme.typography.titleLarge, textAlign = TextAlign.Center)
            if (onBack != null) {
                IconButton(onBack, Modifier.align(Alignment.CenterStart)) {
                    Icon(painterResource(R.drawable.ic_back), stringResource(R.string.auth_back))
                }
            }
        }
        Column(
            Modifier.weight(1f).fillMaxWidth().verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Spacer(Modifier.height(8.dp))
            content()
            if (error != null) Text(error, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodyMedium)
            Spacer(Modifier.height(8.dp))
        }
        Spacer(Modifier.height(16.dp))
        DriveNextButton(onNext, enabled = enabled) { Text(button) }
        Spacer(Modifier.height(24.dp))
    }
}

@Composable
fun AuthField(
    label: String,
    placeholder: String,
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    keyboardType: KeyboardType = KeyboardType.Text,
    password: Boolean = false
) {
    var visible by rememberSaveable { mutableStateOf(false) }
    Column(modifier, verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Text(label, style = MaterialTheme.typography.bodyMedium)
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text(placeholder, style = MaterialTheme.typography.bodyMedium) },
            singleLine = true,
            shape = RoundedCornerShape(8.dp),
            colors = OutlinedTextFieldDefaults.colors(unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant),
            textStyle = MaterialTheme.typography.bodyMedium,
            keyboardOptions = KeyboardOptions(keyboardType = if (password) KeyboardType.Password else keyboardType),
            visualTransformation = if (password && !visible) PasswordVisualTransformation() else VisualTransformation.None,
            trailingIcon = if (password) {
                {
                    IconButton({ visible = !visible }) {
                        Icon(
                            if (visible) painterResource(R.drawable.ic_visibility) else painterResource(R.drawable.ic_visibility_off),
                            stringResource(if (visible) R.string.auth_password_hide else R.string.auth_password_show)
                        )
                    }
                }
            } else null
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AuthDateField(
    label: String,
    placeholder: String,
    value: String,
    pattern: String,
    onValueChange: (String) -> Unit,
    minimumDate: LocalDate? = null
) {
    var showPicker by rememberSaveable { mutableStateOf(false) }
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Text(label, style = MaterialTheme.typography.bodyMedium)
        Box {
            OutlinedTextField(
                value = value,
                onValueChange = {},
                readOnly = true,
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text(placeholder, style = MaterialTheme.typography.bodyMedium) },
                singleLine = true,
                shape = RoundedCornerShape(8.dp),
                colors = OutlinedTextFieldDefaults.colors(unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant),
                textStyle = MaterialTheme.typography.bodyMedium,
                leadingIcon = {
                    IconButton({ showPicker = true }) {
                        Icon(painterResource(R.drawable.ic_calendar), stringResource(R.string.auth_date_choose))
                    }
                }
            )
            Box(
                Modifier.matchParentSize()
                    .semantics { contentDescription = "$label, ${value.ifBlank { placeholder }}" }
                    .clickable { showPicker = true }
            )
        }
    }
    if (showPicker) {
        val selectedDate = AuthValidation.date(value, pattern)
        val pickerState = rememberDatePickerState(
            initialSelectedDateMillis = selectedDate?.atStartOfDay(ZoneOffset.UTC)?.toInstant()?.toEpochMilli(),
            selectableDates = object : SelectableDates {
                override fun isSelectableDate(utcTimeMillis: Long): Boolean {
                    val date = Instant.ofEpochMilli(utcTimeMillis).atZone(ZoneOffset.UTC).toLocalDate()
                    return !date.isAfter(LocalDate.now()) && (minimumDate == null || date.isAfter(minimumDate))
                }
            }
        )
        DatePickerDialog(
            onDismissRequest = { showPicker = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        pickerState.selectedDateMillis?.let {
                            val date = Instant.ofEpochMilli(it).atZone(ZoneOffset.UTC).toLocalDate()
                            onValueChange(date.format(DateTimeFormatter.ofPattern(pattern)))
                        }
                        showPicker = false
                    },
                    enabled = pickerState.selectedDateMillis != null
                ) { Text(stringResource(R.string.auth_date_confirm)) }
            },
            dismissButton = { TextButton({ showPicker = false }) { Text(stringResource(R.string.auth_cancel)) } }
        ) { DatePicker(state = pickerState) }
    }
}

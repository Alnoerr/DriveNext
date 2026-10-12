package dev.alnoer.drivenext.presentation.auth

import android.content.Intent
import android.graphics.BitmapFactory
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.PreviewScreenSizes
import androidx.compose.ui.unit.dp
import androidx.core.content.FileProvider
import androidx.core.net.toUri
import dev.alnoer.drivenext.R
import dev.alnoer.drivenext.presentation.theme.DriveNextTheme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File

@Composable
fun RegistrationDocumentsScreen(
    state: AuthState,
    onUpdate: ((AuthState) -> AuthState) -> Unit,
    onPhoto: (PhotoTarget, String) -> Unit,
    onError: (String) -> Unit,
    onNext: () -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val photoError = stringResource(R.string.auth_photo_error)
    var photoTarget by rememberSaveable { mutableStateOf<PhotoTarget?>(null) }
    val gallery = rememberLauncherForActivityResult(ActivityResultContracts.OpenDocument()) { uri ->
        val target = state.pendingPhotoTarget
        onUpdate { it.copy(pendingPhotoTarget = null) }
        if (uri != null && target != null) {
            try {
                context.contentResolver.takePersistableUriPermission(uri, Intent.FLAG_GRANT_READ_URI_PERMISSION)
                onPhoto(target, uri.toString())
            } catch (_: Exception) {
                onError(photoError)
            }
        }
    }
    val camera = rememberLauncherForActivityResult(ActivityResultContracts.TakePicture()) { success ->
        val target = state.pendingPhotoTarget
        val photo = state.pendingPhoto
        onUpdate { it.copy(pendingPhoto = "", pendingPhotoTarget = null) }
        if (success && target != null && photo.isNotBlank()) {
            onPhoto(target, photo)
        }
    }
    AuthForm(
        stringResource(R.string.auth_create_title),
        stringResource(if (state.isLoading) R.string.auth_loading else R.string.auth_next),
        onNext, if (state.isLoading) null else onBack, modifier,
        enabled = state.canRegister, error = state.error
    ) {
        Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
            Box(Modifier.size(128.dp).clickable { photoTarget = PhotoTarget.Profile }) {
                PhotoPreview(state.profilePhoto, Modifier.size(128.dp).clip(CircleShape))
                Icon(
                    painterResource(R.drawable.ic_add), stringResource(R.string.auth_profile_photo),
                    Modifier.size(28.dp).align(Alignment.BottomEnd), tint = MaterialTheme.colorScheme.primary
                )
            }
        }
        Text(stringResource(R.string.auth_photo_description), style = MaterialTheme.typography.bodyMedium)
        AuthField(
            stringResource(R.string.auth_license), stringResource(R.string.auth_license_placeholder),
            state.licenseNumber, { value -> onUpdate { it.copy(licenseNumber = value.take(10)) } },
            keyboardType = KeyboardType.Number
        )
        AuthDateField(
            stringResource(R.string.auth_issue_date), stringResource(R.string.auth_issue_date_placeholder),
            state.issueDate, "dd/MM/uuuu", { value -> onUpdate { it.copy(issueDate = value) } },
            minimumDate = AuthValidation.date(state.birthDate, "MM/dd/uuuu")
        )
        DocumentPhoto(stringResource(R.string.auth_license_photo), state.licensePhoto) { photoTarget = PhotoTarget.License }
        DocumentPhoto(stringResource(R.string.auth_passport_photo), state.passportPhoto) { photoTarget = PhotoTarget.Passport }
    }
    if (photoTarget != null) {
        AlertDialog(
            onDismissRequest = { photoTarget = null },
            title = { Text(stringResource(R.string.auth_upload)) },
            text = {
                Column {
                    TextButton({
                        onUpdate { it.copy(pendingPhotoTarget = photoTarget) }
                        photoTarget = null
                        try {
                            gallery.launch(arrayOf("image/*"))
                        } catch (_: Exception) {
                            onError(photoError)
                        }
                    }) { Text(stringResource(R.string.auth_gallery)) }
                    TextButton({
                        try {
                            val directory = File(context.filesDir, "photos").apply { mkdirs() }
                            val file = File.createTempFile("photo_", ".jpg", directory)
                            val uri = FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", file)
                            onUpdate { it.copy(pendingPhotoTarget = photoTarget, pendingPhoto = uri.toString()) }
                            photoTarget = null
                            camera.launch(uri)
                        } catch (_: Exception) {
                            photoTarget = null
                            onError(photoError)
                        }
                    }) { Text(stringResource(R.string.auth_camera)) }
                }
            },
            confirmButton = { TextButton({ photoTarget = null }) { Text(stringResource(R.string.auth_cancel)) } }
        )
    }
}

@Composable
private fun DocumentPhoto(label: String, uri: String, onClick: () -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Text(label, style = MaterialTheme.typography.bodyMedium)
        Row(Modifier.fillMaxWidth().clickable(onClick = onClick), verticalAlignment = Alignment.CenterVertically) {
            OutlinedButton(
                onClick,
                modifier = Modifier.size(48.dp),
                contentPadding = PaddingValues(0.dp),
                shape = RoundedCornerShape(8.dp)
            ) {
                if (uri.isNotBlank()) {
                    PhotoPreview(uri, Modifier.size(48.dp))
                } else {
                    Icon(painterResource(R.drawable.ic_upload), null, Modifier.size(24.dp), tint = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
            TextButton(onClick) {
                Text(stringResource(if (uri.isBlank()) R.string.auth_upload else R.string.auth_uploaded))
            }
        }
    }
}

@Composable
private fun PhotoPreview(uri: String, modifier: Modifier = Modifier) {
    val context = LocalContext.current
    val bitmap by produceState<ImageBitmap?>(null, uri) {
        value = withContext(Dispatchers.IO) {
            if (uri.isBlank()) return@withContext null
            try {
                val options = BitmapFactory.Options().apply { inJustDecodeBounds = true }
                context.contentResolver.openInputStream(uri.toUri())?.use { BitmapFactory.decodeStream(it, null, options) }
                options.inSampleSize = (maxOf(options.outWidth, options.outHeight) / 256).coerceAtLeast(1)
                options.inJustDecodeBounds = false
                context.contentResolver.openInputStream(uri.toUri())?.use {
                    BitmapFactory.decodeStream(it, null, options)?.asImageBitmap()
                }
            } catch (_: Exception) {
                null
            }
        }
    }
    if (bitmap != null) {
        Image(bitmap!!, null, modifier, contentScale = ContentScale.Crop)
    } else {
        Icon(
            if (uri.isBlank()) painterResource(R.drawable.ic_account) else painterResource(R.drawable.ic_check_circle),
            null, modifier, tint = MaterialTheme.colorScheme.outlineVariant
        )
    }
}

@PreviewScreenSizes
@Composable
private fun RegistrationDocumentsScreenPreview() {
    DriveNextTheme {
        Surface { RegistrationDocumentsScreen(AuthState(), {}, { _, _ -> }, {}, {}, {}) }
    }
}

package com.example.communityeventmanagement.util

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddPhotoAlternate
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.compose.SubcomposeAsyncImage
import coil.request.ImageRequest

/**
 * Reusable image picker composable.
 * Shows a placeholder when [imageUri] is null, or the selected image with a remove button.
 *
 * @param imageUri  Current image URI string (null = nothing selected)
 * @param onImageSelected Called with the new URI string when user picks an image, or null to clear
 * @param height    Height of the picker area
 * @param label     Label shown inside the placeholder
 */
@Composable
fun ImagePickerBox(
    imageUri: String?,
    onImageSelected: (String?) -> Unit,
    modifier: Modifier = Modifier,
    height: Dp = 180.dp,
    label: String = "Tambah Gambar Cover"
) {
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        onImageSelected(uri?.toString())
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(height)
            .clip(RoundedCornerShape(16.dp))
            .then(
                if (imageUri == null)
                    Modifier.border(
                        width = 1.5.dp,
                        color = MaterialTheme.colorScheme.outlineVariant,
                        shape = RoundedCornerShape(16.dp)
                    ).background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f))
                else Modifier
            )
            .clickable { launcher.launch("image/*") },
        contentAlignment = Alignment.Center
    ) {
        if (imageUri != null) {
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(imageUri)
                    .crossfade(true)
                    .build(),
                contentDescription = "Cover image",
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
            // Remove button overlay
            IconButton(
                onClick = { onImageSelected(null) },
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(8.dp)
                    .size(32.dp)
                    .background(
                        MaterialTheme.colorScheme.surface.copy(alpha = 0.85f),
                        RoundedCornerShape(50)
                    )
            ) {
                Icon(
                    Icons.Default.Close,
                    contentDescription = "Hapus gambar",
                    tint = MaterialTheme.colorScheme.error,
                    modifier = Modifier.size(18.dp)
                )
            }
        } else {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(
                    Icons.Default.AddPhotoAlternate,
                    contentDescription = null,
                    modifier = Modifier.size(36.dp),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.55f)
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = label,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = "Ketuk untuk pilih dari galeri",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f)
                )
            }
        }
    }
}

/**
 * Async image with fallback placeholder (for display, not picking).
 * Improved to handle local assets and resources.
 */
@Composable
fun CoverImage(
    imageUri: String?,
    modifier: Modifier = Modifier,
    contentScale: ContentScale = ContentScale.Crop,
    placeholder: @Composable () -> Unit = {}
) {
    if (imageUri != null) {
        val model = remember(imageUri) {
            if (imageUri.isNullOrEmpty()) {
                null
            } else if (imageUri.startsWith("http") || 
                imageUri.startsWith("content://") || 
                imageUri.startsWith("file://") ||
                imageUri.startsWith("data:")) {
                imageUri
            } else {
                // Ensure it points to the assets folder correctly
                "file:///android_asset/images/$imageUri"
            }
        }

        SubcomposeAsyncImage(
            model = ImageRequest.Builder(LocalContext.current)
                .data(model)
                .crossfade(true)
                .build(),
            contentDescription = null,
            contentScale = contentScale,
            modifier = modifier,
            loading = { placeholder() },
            error = { placeholder() }
        )
    } else {
        Box(modifier = modifier) { placeholder() }
    }
}

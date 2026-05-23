package com.example.communityeventmanagement.util

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.rememberTransformableState
import androidx.compose.foundation.gestures.transformable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AddPhotoAlternate
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.core.content.FileProvider
import coil.compose.AsyncImage
import coil.compose.SubcomposeAsyncImage
import coil.request.ImageRequest
import com.example.communityeventmanagement.R
import java.io.File

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ImagePickerBox(
    imageUri: String?,
    onImageSelected: (String?) -> Unit,
    modifier: Modifier = Modifier,
    height: Dp = 180.dp,
    label: String = "Tambah Gambar Cover",
    isProfile: Boolean = false,
    userName: String = ""
) {
    var showSheet by remember { mutableStateOf(false) }
    var showDeleteConfirm by remember { mutableStateOf(false) }
    var tempUriToCrop by remember { mutableStateOf<Uri?>(null) }
    
    val context = LocalContext.current
    val shape = if (isProfile) CircleShape else MaterialTheme.shapes.small

    // Process picked URI by opening Crop Dialog
    fun handlePickedUri(uri: Uri?) {
        if (uri != null) {
            tempUriToCrop = uri
        }
        showSheet = false
    }

    // Gallery Launcher
    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri -> handlePickedUri(uri) }

    // Camera Launcher
    var tempCameraUri by remember { mutableStateOf<Uri?>(null) }
    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { success ->
        if (success && tempCameraUri != null) {
            handlePickedUri(tempCameraUri)
        }
    }

    fun launchCamera() {
        val file = File(context.cacheDir, "images/temp_raw_${System.currentTimeMillis()}.jpg")
        file.parentFile?.mkdirs()
        val uri = FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", file)
        tempCameraUri = uri
        cameraLauncher.launch(uri)
    }

    // Actual UI
    Box(
        modifier = modifier
            .then(if (isProfile) Modifier.size(height) else Modifier.fillMaxWidth().height(height))
            .clip(shape)
            .background(
                if (imageUri == null && isProfile) ImageUtils.getColorFromName(userName)
                else MaterialTheme.colorScheme.surfaceVariant
            )
            .then(
                if (imageUri == null)
                    Modifier.border(width = 1.dp, color = MaterialTheme.colorScheme.outlineVariant, shape = shape)
                else Modifier
            )
            .clickable { showSheet = true },
        contentAlignment = Alignment.Center
    ) {
        if (!imageUri.isNullOrEmpty()) {
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(imageUri)
                    .crossfade(true)
                    .build(),
                contentDescription = "Selected image",
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
            
            if (!isProfile) {
                IconButton(
                    onClick = { showDeleteConfirm = true },
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(8.dp)
                        .size(32.dp)
                        .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.85f), CircleShape)
                ) {
                    Icon(Icons.Default.Close, contentDescription = "Hapus", tint = MaterialTheme.colorScheme.error, modifier = Modifier.size(18.dp))
                }
            }
        } else {
            if (isProfile) {
                val initial = if (userName.isNotEmpty()) userName.take(1).uppercase() else "?"
                val bgColor = ImageUtils.getColorFromName(userName)
                Box(
                    modifier = Modifier.fillMaxSize().background(bgColor),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = initial,
                        style = MaterialTheme.typography.headlineLarge.copy(
                            fontSize = (height.value * 0.4).sp,
                            fontWeight = FontWeight.Black,
                            color = Color.White
                        )
                    )
                }
            } else {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        Icons.Default.AddPhotoAlternate,
                        contentDescription = null,
                        modifier = Modifier.size(36.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = label,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    }

    // Bottom Sheet Options
    if (showSheet) {
        ModalBottomSheet(
            onDismissRequest = { showSheet = false },
            sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
            containerColor = MaterialTheme.colorScheme.surface,
            shape = MaterialTheme.shapes.large
        ) {
            Column(modifier = Modifier.fillMaxWidth().padding(bottom = 32.dp, start = 24.dp, end = 24.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = { showSheet = false }) { Icon(Icons.Default.Close, contentDescription = "Close") }
                    Text(
                        text = if (isProfile) "Foto Profil" else "Pilih Gambar",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                    if (!imageUri.isNullOrEmpty()) {
                        IconButton(onClick = { showDeleteConfirm = true }) {
                            Icon(Icons.Default.Delete, contentDescription = "Delete", tint = MaterialTheme.colorScheme.error)
                        }
                    } else {
                        Spacer(Modifier.size(48.dp))
                    }
                }
                
                Spacer(modifier = Modifier.height(24.dp))
                PickerOptionItem(icon = Icons.Default.CameraAlt, label = "Kamera", onClick = { launchCamera() })
                PickerOptionItem(icon = Icons.Default.PhotoLibrary, label = "Galeri", onClick = { galleryLauncher.launch("image/*") })
            }
        }
    }

    // Delete Confirmation
    if (showDeleteConfirm) {
        AlertDialog(
            onDismissRequest = { showDeleteConfirm = false },
            title = { Text(stringResource(R.string.dialog_delete_image_title)) },
            text = { Text(stringResource(R.string.dialog_delete_image_msg)) },
            confirmButton = {
                TextButton(onClick = {
                    onImageSelected(null)
                    showDeleteConfirm = false
                    showSheet = false
                }) { Text(stringResource(R.string.action_delete), color = MaterialTheme.colorScheme.error) }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteConfirm = false }) { Text(stringResource(R.string.btn_cancel)) }
            }
        )
    }

    // Crop Dialog
    tempUriToCrop?.let { uri ->
        CropDialog(
            uri = uri,
            isProfile = isProfile,
            onDismiss = { tempUriToCrop = null },
            onDone = { finalUri ->
                onImageSelected(finalUri.toString())
                tempUriToCrop = null
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CropDialog(
    uri: Uri,
    isProfile: Boolean,
    onDismiss: () -> Unit,
    onDone: (Uri) -> Unit
) {
    val context = LocalContext.current
    var scale by remember { mutableFloatStateOf(1f) }
    var offset by remember { mutableStateOf(Offset.Zero) }
    var containerSize by remember { mutableStateOf(IntSize.Zero) }
    val transformState = rememberTransformableState { zoomChange, offsetChange, _ ->
        scale *= zoomChange
        offset += offsetChange
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Sesuaikan Gambar", fontWeight = FontWeight.Black) },
                    navigationIcon = {
                        IconButton(onClick = onDismiss) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Kembali")
                        }
                    },
                    actions = {
                        TextButton(onClick = {
                            val finalUri = ImageUtils.cropAndSaveImage(
                                context = context,
                                uri = uri,
                                scale = scale,
                                offset = offset,
                                containerWidthPx = containerSize.width,
                                containerHeightPx = containerSize.height,
                                isProfile = isProfile
                            )
                            if (finalUri != null) onDone(finalUri)
                        }) {
                            Text("Selesai", fontWeight = FontWeight.Black, fontSize = 16.sp)
                        }
                    }
                )
            }
        ) { padding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .background(Color.Black),
                contentAlignment = Alignment.Center
            ) {
                // Background dimmed
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(if (isProfile) 1f else 16 / 9f)
                        .onGloballyPositioned { containerSize = it.size }
                        .clip(if (isProfile) CircleShape else RoundedCornerShape(0.dp))
                        .background(MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    AsyncImage(
                        model = uri,
                        contentDescription = null,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .fillMaxSize()
                            .graphicsLayer(
                                scaleX = maxOf(1f, scale),
                                scaleY = maxOf(1f, scale),
                                translationX = offset.x,
                                translationY = offset.y
                            )
                            .transformable(state = transformState)
                    )
                }
                
                Text(
                    text = "Gunakan dua jari untuk zoom & geser",
                    color = Color.White,
                    style = MaterialTheme.typography.labelSmall,
                    modifier = Modifier.align(Alignment.BottomCenter).padding(bottom = 32.dp)
                )
            }
        }
    }
}

@Composable
private fun PickerOptionItem(icon: ImageVector, label: String, onClick: () -> Unit) {
    Surface(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.small,
        color = Color.Transparent
    ) {
        Row(
            modifier = Modifier.padding(vertical = 16.dp, horizontal = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Icon(icon, contentDescription = null, modifier = Modifier.size(24.dp))
            Text(text = label, style = MaterialTheme.typography.bodyLarge)
        }
    }
}

@Composable
fun CoverImage(
    imageUri: String?,
    modifier: Modifier = Modifier,
    contentScale: ContentScale = ContentScale.Crop,
    placeholder: @Composable () -> Unit = {}
) {
    if (imageUri != null) {
        val model = remember(imageUri) {
            if (imageUri.isEmpty()) null
            else if (imageUri.startsWith("http") || imageUri.startsWith("content://") || imageUri.startsWith("file://") || imageUri.startsWith("data:")) imageUri
            else "file:///android_asset/images/$imageUri"
        }

        SubcomposeAsyncImage(
            model = ImageRequest.Builder(LocalContext.current)
                .data(model)
                .crossfade(false)
                .build(),
            contentDescription = null,
            contentScale = contentScale,
            modifier = modifier,
            loading = { Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { placeholder() } },
            error = { Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { placeholder() } }
        )
    } else {
        Box(modifier = modifier, contentAlignment = Alignment.Center) { placeholder() }
    }
}

@Composable
fun AvatarImage(
    imageUri: String?,
    name: String,
    modifier: Modifier = Modifier,
    contentScale: ContentScale = ContentScale.Crop
) {
    CoverImage(
        imageUri = imageUri,
        modifier = modifier,
        contentScale = contentScale,
        placeholder = {
            val initial = if (name.isNotEmpty()) name.take(1).uppercase() else "?"
            val bgColor = ImageUtils.getColorFromName(name)
            Box(
                modifier = Modifier.fillMaxSize().background(bgColor),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = initial,
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Black,
                        color = Color.White
                    )
                )
            }
        }
    )
}

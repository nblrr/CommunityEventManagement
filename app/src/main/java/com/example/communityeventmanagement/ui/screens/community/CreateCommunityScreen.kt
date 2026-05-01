package com.example.communityeventmanagement.ui.screens.community

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.communityeventmanagement.data.model.*
import com.example.communityeventmanagement.data.repository.AppState
import com.example.communityeventmanagement.ui.components.CommunityCard
import com.example.communityeventmanagement.ui.components.InlineLoading
import com.example.communityeventmanagement.util.ImagePickerBox
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

private val categoryOptions = listOf(
    "Teknologi", "Desain", "Bisnis", "Edukasi",
    "Kesehatan", "Seni", "Musik", "Olahraga", "Sosial",
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateCommunityScreen(
    currentUser: UserProfile?,
    onCreateSuccess: (Int) -> Unit,
    onNavigateBack: () -> Unit
) {
    var name by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf(categoryOptions.first()) }
    var coverImageUri by remember { mutableStateOf<String?>(null) }
    var showSuccessDialog by remember { mutableStateOf(false) }
    var newCommunityId by remember { mutableIntStateOf(0) }
    var isSubmitting by remember { mutableStateOf(false) }

    val scope = rememberCoroutineScope()
    
    val nameLimit = 40
    val descLimit = 500
    
    val isNameValid = name.isNotBlank() && (name.length <= nameLimit)
    val isDescriptionValid = description.isNotBlank() && (description.length <= descLimit)
    val isFormValid = isNameValid && isDescriptionValid

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Buat Komunitas", fontWeight = FontWeight.ExtraBold) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Kembali")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    titleContentColor = MaterialTheme.colorScheme.onSurface
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp)
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            SectionHeader(title = "Gambar Cover", subtitle = "Pilih gambar yang mewakili komunitasmu")
            
            ImagePickerBox(
                imageUri = coverImageUri,
                onImageSelected = { coverImageUri = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp),
                label = "Ketuk untuk tambah cover"
            )

            Spacer(modifier = Modifier.height(16.dp))

            SectionHeader(title = "Informasi Dasar", subtitle = "Berikan nama dan deskripsi yang menarik")

            OutlinedTextField(
                value = name,
                onValueChange = { if (it.length <= nameLimit) name = it },
                label = { Text("Nama Komunitas") },
                placeholder = { Text("Contoh: Android Developer Jogja") },
                leadingIcon = { Icon(Icons.Default.Groups, contentDescription = null) },
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                isError = name.length > nameLimit,
                supportingText = {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        if (name.length > nameLimit) Text("Nama terlalu panjang") else Text("Wajib diisi")
                        Text("${name.length}/$nameLimit")
                    }
                }
            )

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = description,
                onValueChange = { if (it.length <= descLimit) description = it },
                label = { Text("Deskripsi") },
                placeholder = { Text("Ceritakan visi, misi, dan kegiatan komunitas ini...") },
                leadingIcon = { Icon(Icons.Default.Description, contentDescription = null) },
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth(),
                minLines = 4,
                maxLines = 8,
                isError = description.length > descLimit,
                supportingText = {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        if (description.length > descLimit) Text("Deskripsi terlalu panjang") else Text("Wajib diisi")
                        Text("${description.length}/$descLimit")
                    }
                }
            )

            Spacer(modifier = Modifier.height(24.dp))

            SectionHeader(title = "Kategori", subtitle = "Pilih kategori yang paling sesuai")

            LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                contentPadding = PaddingValues(end = 20.dp)
            ) {
                items(categoryOptions) { category ->
                    val isSelected = category == selectedCategory
                    FilterChip(
                        selected = isSelected,
                        onClick = { selectedCategory = category },
                        label = { Text(category) },
                        shape = RoundedCornerShape(12.dp),
                        leadingIcon = if (isSelected) {
                            { Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(16.dp)) }
                        } else null,
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                            selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            AnimatedVisibility(
                visible = name.isNotBlank(),
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                Column {
                    SectionHeader(title = "Pratinjau", subtitle = "Tampilan komunitasmu di daftar")
                    Box(modifier = Modifier.padding(vertical = 12.dp)) {
                        CommunityCard(
                            community = Community(
                                id = 0,
                                name = name,
                                description = description,
                                category = selectedCategory,
                                coverImageUri = coverImageUri,
                                organizerId = currentUser?.id ?: "",
                                organizerName = currentUser?.name ?: "Organizer",
                                memberIds = emptyList()
                            ),
                            isJoined = false
                        ) {}
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                }
            }

            Button(
                onClick = {
                    scope.launch {
                        isSubmitting = true
                        delay(1000)
                        
                        val newId = (AppState.communities.maxOfOrNull { it.id } ?: 0) + 1
                        val newCommunity = Community(
                            id = newId,
                            name = name.trim(),
                            description = description.trim(),
                            category = selectedCategory,
                            coverImageUri = coverImageUri,
                            organizerId = currentUser?.id ?: "",
                            organizerName = currentUser?.name ?: "Organizer",
                            memberIds = listOf(currentUser?.id ?: ""),
                            events = emptyList(),
                            forumMessages = emptyList()
                        )
                        
                        AppState.communities.add(newCommunity)
                        AppState.joinedCommunityIds.add(newId)
                        AppState.saveCommunityData()
                        
                        newCommunityId = newId
                        isSubmitting = false
                        showSuccessDialog = true
                    }
                },
                enabled = isFormValid && !isSubmitting,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(16.dp),
                elevation = ButtonDefaults.buttonElevation(defaultElevation = 2.dp)
            ) {
                if (isSubmitting) {
                    InlineLoading(modifier = Modifier.size(24.dp))
                    Spacer(modifier = Modifier.width(12.dp))
                    Text("Memproses...", fontWeight = FontWeight.Bold)
                } else {
                    Icon(Icons.Default.AddCircleOutline, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Publikasikan Komunitas", fontWeight = FontWeight.ExtraBold, fontSize = 16.sp)
                }
            }

            Spacer(modifier = Modifier.height(40.dp))
        }
    }

    if (showSuccessDialog) {
        AlertDialog(
            onDismissRequest = { },
            icon = { Icon(Icons.Default.CheckCircle, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(48.dp)) },
            title = { 
                Text(
                    "Komunitas Berhasil Dibuat!", 
                    fontWeight = FontWeight.ExtraBold,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                ) 
            },
            text = { 
                Text(
                    "Komunitas \"$name\" kini sudah aktif dan bisa ditemukan oleh anggota lainnya.",
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                ) 
            },
            confirmButton = {
                Button(
                    onClick = {
                        showSuccessDialog = false
                        onCreateSuccess(newCommunityId)
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Buka Komunitas Saya")
                }
            },
            shape = RoundedCornerShape(24.dp),
            containerColor = MaterialTheme.colorScheme.surface,
            tonalElevation = 6.dp
        )
    }
}

@Composable
private fun SectionHeader(
    title: String,
    subtitle: String,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.padding(bottom = 8.dp)) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.ExtraBold,
            color = MaterialTheme.colorScheme.onSurface
        )
        Text(
            text = subtitle,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

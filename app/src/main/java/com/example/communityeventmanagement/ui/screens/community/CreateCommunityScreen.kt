package com.example.communityeventmanagement.ui.screens.community

import androidx.compose.foundation.background
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.communityeventmanagement.data.model.*
import com.example.communityeventmanagement.data.repository.AppState
import com.example.communityeventmanagement.util.ImagePickerBox

private val categoryOptions = listOf(
    "Technology", "Design", "Business", "Education",
    "Health", "Art", "Music", "Sports", "Social"
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
    var selectedCategory by remember { mutableStateOf("Technology") }
    var imageUrl by remember { mutableStateOf("") }
    var showSuccessDialog by remember { mutableStateOf(false) }
    var newCommunityId by remember { mutableIntStateOf(0) }

    val isFormValid = name.isNotBlank() && description.isNotBlank()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Buat Komunitas", fontWeight = FontWeight.ExtraBold) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Kembali", tint = MaterialTheme.colorScheme.primary)
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier.fillMaxSize().padding(paddingValues).verticalScroll(rememberScrollState()).padding(horizontal = 20.dp)
        ) {
            Spacer(Modifier.height(16.dp))
            Box(modifier = Modifier.fillMaxWidth().padding(bottom = 24.dp), contentAlignment = Alignment.Center) {
                Box(modifier = Modifier.size(80.dp).clip(RoundedCornerShape(22.dp)).background(MaterialTheme.colorScheme.primaryContainer), contentAlignment = Alignment.Center) {
                    Icon(Icons.Default.Groups, contentDescription = null, modifier = Modifier.size(40.dp), tint = MaterialTheme.colorScheme.primary)
                }
            }

            OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("Nama Komunitas *") }, shape = RoundedCornerShape(14.dp), modifier = Modifier.fillMaxWidth(), singleLine = true)
            Spacer(Modifier.height(12.dp))
            OutlinedTextField(value = description, onValueChange = { description = it }, label = { Text("Deskripsi *") }, shape = RoundedCornerShape(14.dp), modifier = Modifier.fillMaxWidth(), minLines = 3)
            Spacer(Modifier.height(16.dp))
            
            Text(text = "Gambar Sampul", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold, modifier = Modifier.padding(bottom = 8.dp))
            ImagePickerBox(
                imageUri = imageUrl.ifBlank { null },
                onImageSelected = { imageUrl = it ?: "" },
                label = "Pilih Gambar Komunitas"
            )
            
            Spacer(Modifier.height(20.dp))
            Text(text = "Pilih Kategori", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold, modifier = Modifier.padding(bottom = 10.dp))
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                items(categoryOptions) { category ->
                    val isSelected = category == selectedCategory
                    FilterChip(selected = isSelected, onClick = { selectedCategory = category }, label = { Text(category) })
                }
            }

            Spacer(Modifier.height(28.dp))
            Button(
                onClick = {
                    val newId = (AppState.communities.maxOfOrNull { it.id } ?: 0) + 1
                    val newCommunity = Community(
                        id = newId,
                        name = name.trim(),
                        description = description.trim(),
                        category = selectedCategory,
                        coverImageUri = imageUrl.ifBlank { null },
                        organizerId = currentUser?.id ?: "",
                        organizerName = currentUser?.name ?: "Organizer",
                        memberIds = mutableListOf(),
                        events = emptyList(),
                        forumMessages = emptyList()
                    )
                    AppState.communities.add(newCommunity)
                    AppState.saveCommunityData()
                    AppState.joinedCommunityIds.add(newId)
                    newCommunityId = newId
                    showSuccessDialog = true
                },
                enabled = isFormValid,
                modifier = Modifier.fillMaxWidth().height(52.dp),
                shape = RoundedCornerShape(14.dp)
            ) {
                Icon(Icons.Default.Add, contentDescription = null)
                Spacer(Modifier.width(8.dp))
                Text("Buat Komunitas", fontWeight = FontWeight.ExtraBold)
            }
            Spacer(Modifier.height(32.dp))
        }
    }

    if (showSuccessDialog) {
        AlertDialog(
            onDismissRequest = {},
            title = { Text("Komunitas Berhasil Dibuat!", fontWeight = FontWeight.ExtraBold) },
            text = { Text("\"$name\" sudah aktif. Mulai undang anggota dan buat event pertama!") },
            confirmButton = {
                Button(onClick = { onCreateSuccess(newCommunityId) }) { Text("Lihat Komunitas") }
            }
        )
    }
}

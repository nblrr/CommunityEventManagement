package com.example.communityeventmanagement.ui.screens.organizer

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.communityeventmanagement.data.model.OrganizerProfile
import com.example.communityeventmanagement.data.model.UserProfile
import com.example.communityeventmanagement.data.repository.AppState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrganizerRegisterScreen(
    currentUser: UserProfile?,
    onRegisterSuccess: (UserProfile) -> Unit,
    onNavigateBack: () -> Unit
) {
    var communityName by remember { mutableStateOf("") }
    var personInCharge by remember { mutableStateOf(currentUser?.name ?: "") }
    var description by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var showSuccessDialog by remember { mutableStateOf(false) }

    val isFormValid = communityName.isNotBlank() && personInCharge.isNotBlank() && description.isNotBlank() && phone.isNotBlank()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Daftar Organizer", fontWeight = FontWeight.ExtraBold) },
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
            Spacer(Modifier.height(8.dp))
            // Header visual
            Box(
                modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(20.dp)).background(Brush.linearGradient(colors = listOf(MaterialTheme.colorScheme.primary, MaterialTheme.colorScheme.tertiary))).padding(24.dp)
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
                    Icon(Icons.Default.Star, contentDescription = null, modifier = Modifier.size(40.dp), tint = Color.White)
                    Spacer(Modifier.height(8.dp))
                    Text(text = "Jadi Organizer", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.ExtraBold, color = Color.White, textAlign = TextAlign.Center)
                    Text(text = "Buat & kelola komunitas sendiri", style = MaterialTheme.typography.bodyMedium, color = Color.White.copy(alpha = 0.8f), textAlign = TextAlign.Center)
                }
            }

            Spacer(Modifier.height(24.dp))
            Text(text = "Data Organizer", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.ExtraBold, modifier = Modifier.padding(bottom = 16.dp))

            OutlinedTextField(value = communityName, onValueChange = { communityName = it }, label = { Text("Nama Organizer *") }, shape = RoundedCornerShape(14.dp), modifier = Modifier.fillMaxWidth(), singleLine = true)
            Spacer(Modifier.height(12.dp))
            OutlinedTextField(value = personInCharge, onValueChange = { personInCharge = it }, label = { Text("Penanggung Jawab *") }, shape = RoundedCornerShape(14.dp), modifier = Modifier.fillMaxWidth(), singleLine = true)
            Spacer(Modifier.height(12.dp))
            OutlinedTextField(value = phone, onValueChange = { phone = it }, label = { Text("Telepon *") }, shape = RoundedCornerShape(14.dp), keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone), modifier = Modifier.fillMaxWidth(), singleLine = true)
            Spacer(Modifier.height(12.dp))
            OutlinedTextField(value = description, onValueChange = { description = it }, label = { Text("Deskripsi *") }, shape = RoundedCornerShape(14.dp), modifier = Modifier.fillMaxWidth(), minLines = 3)

            Spacer(Modifier.height(28.dp))
            Button(
                onClick = { showSuccessDialog = true },
                enabled = isFormValid,
                modifier = Modifier.fillMaxWidth().height(52.dp),
                shape = RoundedCornerShape(14.dp)
            ) {
                Text("Daftar Sekarang", fontWeight = FontWeight.ExtraBold)
            }
            Spacer(Modifier.height(32.dp))
        }
    }

    // Dialog Sukses
    if (showSuccessDialog) {
        AlertDialog(
            onDismissRequest = {},
            title = { Text("Selamat!", fontWeight = FontWeight.ExtraBold) },
            text = { Text("Sekarang kamu terdaftar sebagai Organizer.") },
            confirmButton = {
                Button(
                    onClick = {
                        val updatedUser = currentUser?.copy(
                            role = "Organizer",
                            organizerProfile = OrganizerProfile(communityName.trim(), personInCharge.trim(), description.trim(), phone.trim())
                        )
                        if (updatedUser != null) {
                            val idx = AppState.allUsers.indexOfFirst { it.id == updatedUser.id }
                            if (idx != -1) {
                                AppState.allUsers[idx] = updatedUser
                                AppState.saveUserData()
                            }
                            onRegisterSuccess(updatedUser)
                        }
                    }
                ) { Text("Mulai", fontWeight = FontWeight.Bold) }
            }
        )
    }
}

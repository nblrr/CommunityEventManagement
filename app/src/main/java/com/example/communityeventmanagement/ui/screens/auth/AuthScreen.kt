package com.example.communityeventmanagement.ui.screens.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.communityeventmanagement.data.model.UserProfile
import com.example.communityeventmanagement.data.repository.AppState
import com.example.communityeventmanagement.util.InputValidation

// Screen Login
@Composable
fun LoginScreen(
    onLoginSuccess: (UserProfile) -> Unit,
    onNavigateToRegister: () -> Unit,
    onNavigateBack: () -> Unit
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    val isFormValid = email.isNotBlank() && password.isNotBlank()

    Box(modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)) {
        // Dekorasi header
        Box(modifier = Modifier.fillMaxWidth().height(260.dp).background(Brush.verticalGradient(colors = listOf(MaterialTheme.colorScheme.primary.copy(alpha = 0.12f), Color.Transparent))))

        IconButton(onClick = onNavigateBack, modifier = Modifier.padding(top = 40.dp, start = 12.dp).statusBarsPadding()) {
            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Kembali", tint = MaterialTheme.colorScheme.primary)
        }

        Column(modifier = Modifier.fillMaxSize().padding(horizontal = 24.dp), verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.CenterHorizontally) {
            // Icon Logo
            Box(modifier = Modifier.size(72.dp).clip(RoundedCornerShape(22.dp)).background(Brush.linearGradient(colors = listOf(MaterialTheme.colorScheme.primary, MaterialTheme.colorScheme.secondary))), contentAlignment = Alignment.Center) {
                Icon(Icons.Default.Groups, contentDescription = null, modifier = Modifier.size(40.dp), tint = Color.White)
            }
            Spacer(Modifier.height(20.dp))
            Text(text = "Selamat Datang!", style = MaterialTheme.typography.headlineLarge, fontWeight = FontWeight.ExtraBold, textAlign = TextAlign.Center)
            Text(text = "Masuk ke akun komunitasmu", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.55f), textAlign = TextAlign.Center, modifier = Modifier.padding(top = 6.dp, bottom = 32.dp))

            // Input Email
            OutlinedTextField(value = email, onValueChange = { email = it; errorMessage = null }, label = { Text("Email") }, leadingIcon = { Icon(Icons.Default.Email, contentDescription = null) }, shape = RoundedCornerShape(14.dp), keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email), modifier = Modifier.fillMaxWidth(), singleLine = true, isError = errorMessage != null)
            Spacer(modifier = Modifier.height(12.dp))

            // Input Password
            OutlinedTextField(value = password, onValueChange = { password = it; errorMessage = null }, label = { Text("Password") }, leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null) }, trailingIcon = { IconButton(onClick = { passwordVisible = !passwordVisible }) { Icon(if (passwordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility, contentDescription = null) } }, visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(), shape = RoundedCornerShape(14.dp), keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password), modifier = Modifier.fillMaxWidth(), singleLine = true, isError = errorMessage != null)

            if (errorMessage != null) {
                Text(text = errorMessage!!, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.error, modifier = Modifier.fillMaxWidth().padding(top = 6.dp))
            }

            Spacer(modifier = Modifier.height(28.dp))
            Button(
                onClick = {
                    val result = AppState.loginWithCredentials(email, password)
                    when (result) {
                        is com.example.communityeventmanagement.data.repository.LoginResult.Success -> onLoginSuccess(result.user)
                        is com.example.communityeventmanagement.data.repository.LoginResult.Error -> errorMessage = result.message
                    }
                },
                enabled = isFormValid, modifier = Modifier.fillMaxWidth().height(52.dp), shape = RoundedCornerShape(14.dp)
            ) { Text("Masuk", fontWeight = FontWeight.ExtraBold, fontSize = 16.sp) }

            Spacer(modifier = Modifier.height(16.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("Belum punya akun?", style = MaterialTheme.typography.bodyMedium)
                TextButton(onClick = onNavigateToRegister) { Text("Daftar", fontWeight = FontWeight.Bold) }
            }
        }
    }
}

// Screen Register
@Composable
fun RegisterScreen(
    onRegisterSuccess: (UserProfile) -> Unit,
    onNavigateToLogin: () -> Unit,
    onNavigateBack: () -> Unit
) {
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    val isFormValid = name.isNotBlank() && email.isNotBlank() && password.isNotBlank()

    Box(modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)) {
        Box(modifier = Modifier.fillMaxWidth().height(260.dp).background(Brush.verticalGradient(colors = listOf(MaterialTheme.colorScheme.secondary.copy(alpha = 0.1f), Color.Transparent))))

        IconButton(onClick = onNavigateBack, modifier = Modifier.padding(top = 40.dp, start = 12.dp).statusBarsPadding()) {
            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null, tint = MaterialTheme.colorScheme.secondary)
        }

        Column(modifier = Modifier.fillMaxSize().padding(horizontal = 24.dp), verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.CenterHorizontally) {
            Box(modifier = Modifier.size(72.dp).clip(RoundedCornerShape(22.dp)).background(Brush.linearGradient(colors = listOf(MaterialTheme.colorScheme.secondary, MaterialTheme.colorScheme.primary))), contentAlignment = Alignment.Center) {
                Icon(Icons.Default.Star, contentDescription = null, modifier = Modifier.size(40.dp), tint = Color.White)
            }
            Spacer(Modifier.height(20.dp))
            Text(text = "Buat Akun", style = MaterialTheme.typography.headlineLarge, fontWeight = FontWeight.ExtraBold)
            Text(text = "Gabung bersama komunitas kami", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.55f), textAlign = TextAlign.Center, modifier = Modifier.padding(top = 6.dp, bottom = 28.dp))

            // Input Data
            OutlinedTextField(value = name, onValueChange = { name = it; errorMessage = null }, label = { Text("Nama Lengkap") }, leadingIcon = { Icon(Icons.Default.Person, contentDescription = null) }, shape = RoundedCornerShape(14.dp), modifier = Modifier.fillMaxWidth(), singleLine = true)
            Spacer(modifier = Modifier.height(12.dp))
            OutlinedTextField(value = email, onValueChange = { email = it; errorMessage = null }, label = { Text("Email") }, leadingIcon = { Icon(Icons.Default.Email, contentDescription = null) }, shape = RoundedCornerShape(14.dp), keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email), modifier = Modifier.fillMaxWidth(), singleLine = true, isError = errorMessage != null)
            Spacer(modifier = Modifier.height(12.dp))
            OutlinedTextField(value = password, onValueChange = { password = it; errorMessage = null }, label = { Text("Password") }, leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null) }, trailingIcon = { IconButton(onClick = { passwordVisible = !passwordVisible }) { Icon(if (passwordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility, contentDescription = null) } }, visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(), shape = RoundedCornerShape(14.dp), keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password), modifier = Modifier.fillMaxWidth(), singleLine = true)

            if (errorMessage != null) {
                Text(text = errorMessage!!, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.error, modifier = Modifier.fillMaxWidth().padding(top = 6.dp))
            }

            Spacer(modifier = Modifier.height(28.dp))
            Button(
                onClick = {
                    val trimmedEmail = email.trim()
                    val validation = InputValidation.validateRegisterForm(name, trimmedEmail, password, password)
                    if (validation is InputValidation.ValidationResult.Invalid) { errorMessage = validation.message; return@Button }
                    if (AppState.allUsers.any { it.email.equals(trimmedEmail, ignoreCase = true) }) { errorMessage = "Email sudah terdaftar" }
                    else {
                        val newUser = UserProfile(id = "user_${System.currentTimeMillis()}", name = name.trim(), email = trimmedEmail, password = password)
                        AppState.allUsers.add(newUser); AppState.saveUserData(); AppState.login(newUser); onRegisterSuccess(newUser)
                    }
                },
                enabled = isFormValid, modifier = Modifier.fillMaxWidth().height(52.dp), shape = RoundedCornerShape(14.dp), colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary)
            ) { Text("Daftar Sekarang", fontWeight = FontWeight.ExtraBold, fontSize = 16.sp) }

            Spacer(modifier = Modifier.height(16.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("Sudah punya akun?", style = MaterialTheme.typography.bodyMedium)
                TextButton(onClick = onNavigateToLogin) { Text("Masuk", fontWeight = FontWeight.Bold) }
            }
        }
    }
}

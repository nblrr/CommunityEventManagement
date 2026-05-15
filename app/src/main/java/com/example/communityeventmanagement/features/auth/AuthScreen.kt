package com.example.communityeventmanagement.features.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.communityeventmanagement.R
import com.example.communityeventmanagement.data.model.UserProfile
import com.example.communityeventmanagement.ui.AppViewModelProvider

// Screen Login
@Composable
fun LoginScreen(
    onLoginSuccess: (UserProfile) -> Unit,
    onNavigateToRegister: () -> Unit,
    onNavigateBack: () -> Unit,
    viewModel: AuthViewModel = viewModel(factory = AppViewModelProvider.Factory),
) {
    Box(modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)) {
        // Dekorasi header
        Box(modifier = Modifier.fillMaxWidth().height(260.dp).background(Brush.verticalGradient(colors = listOf(MaterialTheme.colorScheme.primary.copy(alpha = 0.12f), Color.Transparent))))

        IconButton(onClick = onNavigateBack, modifier = Modifier.padding(top = 40.dp, start = 12.dp).statusBarsPadding()) {
            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = stringResource(R.string.back), tint = MaterialTheme.colorScheme.primary)
        }

        Column(modifier = Modifier.fillMaxSize().padding(horizontal = 24.dp), verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.CenterHorizontally) {
            // Icon Logo
            Box(modifier = Modifier.size(72.dp).clip(RoundedCornerShape(22.dp)).background(Brush.linearGradient(colors = listOf(MaterialTheme.colorScheme.primary, MaterialTheme.colorScheme.secondary))), contentAlignment = Alignment.Center) {
                Icon(Icons.Default.Groups, contentDescription = null, modifier = Modifier.size(40.dp), tint = Color.White)
            }
            Spacer(Modifier.height(20.dp))
            Text(text = stringResource(R.string.welcome_back), style = MaterialTheme.typography.headlineLarge, fontWeight = FontWeight.ExtraBold, textAlign = TextAlign.Center)
            Text(text = stringResource(R.string.login_hint), style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.55f), textAlign = TextAlign.Center, modifier = Modifier.padding(top = 6.dp, bottom = 32.dp))

            // Input Email
            OutlinedTextField(value = viewModel.loginEmail, onValueChange = { viewModel.loginEmail = it; viewModel.clearErrors() }, label = { Text(stringResource(R.string.label_email)) }, leadingIcon = { Icon(Icons.Default.Email, contentDescription = null) }, shape = RoundedCornerShape(14.dp), keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email), modifier = Modifier.fillMaxWidth(), singleLine = true, isError = viewModel.loginErrorMessage != null)
            Spacer(modifier = Modifier.height(12.dp))

            // Input Password
            OutlinedTextField(value = viewModel.loginPassword, onValueChange = { viewModel.loginPassword = it; viewModel.clearErrors() }, label = { Text(stringResource(R.string.label_password)) }, leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null) }, trailingIcon = { IconButton(onClick = { viewModel.loginPasswordVisible = !viewModel.loginPasswordVisible }) { Icon(if (viewModel.loginPasswordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility, contentDescription = null) } }, visualTransformation = if (viewModel.loginPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(), shape = RoundedCornerShape(14.dp), keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password), modifier = Modifier.fillMaxWidth(), singleLine = true, isError = viewModel.loginErrorMessage != null)

            if (viewModel.loginErrorMessage != null) {
                Text(text = viewModel.loginErrorMessage!!, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.error, modifier = Modifier.fillMaxWidth().padding(top = 6.dp))
            }

            Spacer(modifier = Modifier.height(28.dp))
            val context = androidx.compose.ui.platform.LocalContext.current
            Button(
                onClick = { viewModel.login(onLoginSuccess, context) },
                enabled = viewModel.isLoginFormValid, modifier = Modifier.fillMaxWidth().height(52.dp), shape = RoundedCornerShape(14.dp)
            ) { Text(stringResource(R.string.btn_login), fontWeight = FontWeight.ExtraBold, fontSize = 16.sp) }

            Spacer(modifier = Modifier.height(16.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(stringResource(R.string.txt_no_account), style = MaterialTheme.typography.bodyMedium)
                TextButton(onClick = onNavigateToRegister) { Text(stringResource(R.string.btn_register), fontWeight = FontWeight.Bold) }
            }
        }
    }
}

// Screen Register
@Composable
fun RegisterScreen(
    onRegisterSuccess: (UserProfile) -> Unit,
    onNavigateToLogin: () -> Unit,
    onNavigateBack: () -> Unit,
    viewModel: AuthViewModel = viewModel(factory = AppViewModelProvider.Factory),
) {
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
            Text(text = stringResource(R.string.title_create_account), style = MaterialTheme.typography.headlineLarge, fontWeight = FontWeight.ExtraBold)
            Text(text = stringResource(R.string.register_hint), style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.55f), textAlign = TextAlign.Center, modifier = Modifier.padding(top = 6.dp, bottom = 28.dp))

            // Input Data
            OutlinedTextField(value = viewModel.registerName, onValueChange = { viewModel.registerName = it; viewModel.clearErrors() }, label = { Text(stringResource(R.string.label_full_name)) }, leadingIcon = { Icon(Icons.Default.Person, contentDescription = null) }, shape = RoundedCornerShape(14.dp), modifier = Modifier.fillMaxWidth(), singleLine = true)
            Spacer(modifier = Modifier.height(12.dp))
            OutlinedTextField(value = viewModel.registerEmail, onValueChange = { viewModel.registerEmail = it; viewModel.clearErrors() }, label = { Text(stringResource(R.string.label_email)) }, leadingIcon = { Icon(Icons.Default.Email, contentDescription = null) }, shape = RoundedCornerShape(14.dp), keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email), modifier = Modifier.fillMaxWidth(), singleLine = true, isError = viewModel.registerErrorMessage != null)
            Spacer(modifier = Modifier.height(12.dp))
            OutlinedTextField(value = viewModel.registerPassword, onValueChange = { viewModel.registerPassword = it; viewModel.clearErrors() }, label = { Text(stringResource(R.string.label_password)) }, leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null) }, trailingIcon = { IconButton(onClick = { viewModel.registerPasswordVisible = !viewModel.registerPasswordVisible }) { Icon(if (viewModel.registerPasswordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility, contentDescription = null) } }, visualTransformation = if (viewModel.registerPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(), shape = RoundedCornerShape(14.dp), keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password), modifier = Modifier.fillMaxWidth(), singleLine = true)

            if (viewModel.registerErrorMessage != null) {
                Text(text = viewModel.registerErrorMessage!!, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.error, modifier = Modifier.fillMaxWidth().padding(top = 6.dp))
            }

            Spacer(modifier = Modifier.height(28.dp))
            val context = androidx.compose.ui.platform.LocalContext.current
            Button(
                onClick = { viewModel.register(onRegisterSuccess, context) },
                enabled = viewModel.isRegisterFormValid, modifier = Modifier.fillMaxWidth().height(52.dp), shape = RoundedCornerShape(14.dp), colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary)
            ) { Text(stringResource(R.string.btn_register_now), fontWeight = FontWeight.ExtraBold, fontSize = 16.sp) }

            Spacer(modifier = Modifier.height(16.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(stringResource(R.string.txt_already_have_account), style = MaterialTheme.typography.bodyMedium)
                TextButton(onClick = onNavigateToLogin) { Text(stringResource(R.string.btn_login), fontWeight = FontWeight.Bold) }
            }
        }
    }
}

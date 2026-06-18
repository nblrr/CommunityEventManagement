package com.example.communityeventmanagementsystem.presentation.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Mail
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.example.communityeventmanagementsystem.ui.theme.*

@Composable
fun LoginScreen(
    viewModel: LoginViewModel,
    onNavigateToHome: () -> Unit,
    onNavigateToRegister: () -> Unit
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }

    var emailError by remember { mutableStateOf<String?>(null) }
    var passwordError by remember { mutableStateOf<String?>(null) }

    fun validateEmail(value: String) {
        emailError = when {
            value.isBlank() -> "Email tidak boleh kosong"
            !android.util.Patterns.EMAIL_ADDRESS.matcher(value).matches() -> "Format email tidak valid"
            else -> null
        }
    }

    fun validatePassword(value: String) {
        passwordError = when {
            value.isBlank() -> "Password tidak boleh kosong"
            value.length < 8 -> "Password minimal 8 karakter"
            else -> null
        }
    }

    LaunchedEffect(key1 = Unit) {
        viewModel.effect.collect { effect ->
            when (effect) {
                is LoginContract.Effect.NavigationToHome -> {
                    onNavigateToHome()
                }
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Surface)
            .imePadding()
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Surface(
            shape = Shapes.ExtraLarge,
            color = SurfaceContainerLowest,
            shadowElevation = 4.dp,
            modifier = Modifier
                .fillMaxWidth()
                .widthIn(max = 400.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(192.dp)
                ) {
                    AsyncImage(
                        model = "https://lh3.googleusercontent.com/aida-public/AB6AXuAEqvcEcNcIN1vlR7Jn2NxAqFnxymvs7vGJAodYn3Oi0q3tj0dhHapWxEJYiislNrMGH2JUUF27XS0ikYSh-WDpYvDzmrYAon6LzVEJXKgwgXXWG3aCqnEKNlLXhsE0nvIlAj59YhS5Slm4AILOMBF6c0O5KP0El5fkDm1dHLQo74C9ftyptphpLwsk3Svep-ObQjt66S2yFXKbnNX9oITQCl4R3vOmtY9SaLJfnb2R9Dm5Y2RAYeSg060dmRQ85j2zfIFL3vAm1J6s",
                        contentDescription = "Event Crowd",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                Brush.verticalGradient(
                                    colors = listOf(Color.Transparent, SurfaceContainerLowest),
                                    startY = 50f
                                )
                            )
                    )
                }

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = Dimens.SpacingLg, vertical = Dimens.SpacingMd)
                ) {
                    Column(
                        modifier = Modifier.fillMaxWidth().padding(bottom = Dimens.SpacingXl),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text("Communitix", style = HeadlineLg, color = Primary, modifier = Modifier.padding(bottom = 8.dp))
                        Text("Welcome back. Let's get managing.", style = BodyMd, color = OnSurfaceVariant)
                    }

                    Column(verticalArrangement = Arrangement.spacedBy(Dimens.SpacingLg)) {
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Text("Email", style = LabelMd, color = OnSurface)
                            OutlinedTextField(
                                value = email,
                                onValueChange = { 
                                    email = it
                                    validateEmail(it)
                                },
                                placeholder = { Text("you@company.com", style = BodyMd, color = OutlineVariant) },
                                leadingIcon = { Icon(Icons.Default.Mail, contentDescription = null, tint = Outline) },
                                modifier = Modifier.fillMaxWidth(),
                                shape = Shapes.Large,
                                singleLine = true,
                                isError = emailError != null,
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email, imeAction = ImeAction.Next),
                                colors = OutlinedTextFieldDefaults.colors(
                                    unfocusedContainerColor = SurfaceContainerLowest,
                                    focusedContainerColor = SurfaceContainerLowest,
                                    unfocusedBorderColor = OutlineVariant,
                                    focusedBorderColor = Primary
                                )
                            )
                            if (emailError != null) {
                                Text(emailError!!, style = BodySm, color = Error)
                            }
                        }

                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text("Password", style = LabelMd, color = OnSurface)
                            }
                            OutlinedTextField(
                                value = password,
                                onValueChange = { 
                                    password = it
                                    validatePassword(it)
                                },
                                placeholder = { Text("••••••••", style = BodyMd, color = OutlineVariant) },
                                leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null, tint = Outline) },
                                trailingIcon = {
                                    IconButton(onClick = { passwordVisible = !passwordVisible }) {
                                        Icon(
                                            imageVector = if (passwordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                            contentDescription = if (passwordVisible) "Hide password" else "Show password",
                                            tint = Outline
                                        )
                                    }
                                },
                                visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                                modifier = Modifier.fillMaxWidth(),
                                shape = Shapes.Large,
                                singleLine = true,
                                isError = passwordError != null,
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password, imeAction = ImeAction.Done),
                                colors = OutlinedTextFieldDefaults.colors(
                                    unfocusedContainerColor = SurfaceContainerLowest,
                                    focusedContainerColor = SurfaceContainerLowest,
                                    unfocusedBorderColor = OutlineVariant,
                                    focusedBorderColor = Primary
                                )
                            )
                            if (passwordError != null) {
                                Text(passwordError!!, style = BodySm, color = Error)
                            }
                        }

                        if (state.error != null) {
                            Text(
                                text = state.error ?: "",
                                color = Error,
                                style = BodySm,
                                modifier = Modifier.padding(top = 4.dp)
                            )
                        }

                        Button(
                            onClick = {
                                validateEmail(email)
                                validatePassword(password)
                                
                                if (emailError == null && passwordError == null) {
                                    viewModel.setEvent(LoginContract.Event.OnLoginClicked(email, password))
                                }
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp),
                            shape = Shapes.Large,
                            enabled = !state.isLoading,
                            colors = ButtonDefaults.buttonColors(containerColor = Primary, contentColor = OnPrimary)
                        ) {
                            if (state.isLoading) {
                                CircularProgressIndicator(modifier = Modifier.size(24.dp), color = OnPrimary)
                            } else {
                                Text("Login", style = LabelMd)
                            }
                        }
                    }

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = Dimens.SpacingXl, bottom = Dimens.SpacingSm),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Text("Don't have an account? ", style = BodySm, color = OnSurfaceVariant)
                        Text(
                            text = "Register",
                            style = BodySm.copy(fontWeight = FontWeight.Bold),
                            color = Primary,
                            modifier = Modifier.clickable { onNavigateToRegister() }
                        )
                    }
                }
            }
        }
    }
}

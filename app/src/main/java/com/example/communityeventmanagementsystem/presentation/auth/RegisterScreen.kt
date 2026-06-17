package com.example.communityeventmanagementsystem.presentation.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.HowToReg
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.communityeventmanagementsystem.ui.theme.*

@Composable
fun RegisterScreen(
    viewModel: RegisterViewModel,
    onNavigateToHome: () -> Unit,
    onNavigateToLogin: () -> Unit
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    var fullName by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var termsAccepted by remember { mutableStateOf(false) }

    var fullNameError by remember { mutableStateOf<String?>(null) }
    var emailError by remember { mutableStateOf<String?>(null) }
    var passwordError by remember { mutableStateOf<String?>(null) }

    fun validateFullName(value: String) {
        fullNameError = if (value.isBlank()) "Nama lengkap tidak boleh kosong" else null
    }

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
                is RegisterContract.Effect.NavigationToHome -> {
                    onNavigateToHome()
                }
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Background)
            .imePadding()
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Surface(
            shape = Shapes.ExtraLarge,
            color = Surface,
            shadowElevation = 8.dp,
            border = androidx.compose.foundation.BorderStroke(1.dp, OutlineVariant.copy(alpha = 0.3f)),
            modifier = Modifier
                .fillMaxWidth()
                .widthIn(max = 400.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
                    .padding(Dimens.SpacingLg),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Header
                Surface(
                    shape = CircleShape,
                    color = PrimaryContainer,
                    shadowElevation = 2.dp,
                    modifier = Modifier.padding(bottom = Dimens.SpacingMd)
                ) {
                    Icon(
                        Icons.Default.HowToReg,
                        contentDescription = null,
                        tint = OnPrimaryContainer,
                        modifier = Modifier.padding(16.dp).size(32.dp)
                    )
                }
                Text("Create Account", style = HeadlineLgMobile, color = OnSurface, modifier = Modifier.padding(bottom = Dimens.SpacingXs))
                Text("Join Communitix and connect with your community.", style = BodyMd, color = OnSurfaceVariant, modifier = Modifier.padding(bottom = Dimens.SpacingXl), textAlign = androidx.compose.ui.text.style.TextAlign.Center)

                // Form
                Column(verticalArrangement = Arrangement.spacedBy(Dimens.SpacingMd), modifier = Modifier.fillMaxWidth()) {
                    Column {
                        OutlinedTextField(
                            value = fullName,
                            onValueChange = { 
                                fullName = it
                                validateFullName(it)
                            },
                            label = { Text("Full Name", style = LabelMd) },
                            modifier = Modifier.fillMaxWidth(),
                            shape = Shapes.Medium,
                            singleLine = true,
                            isError = fullNameError != null,
                            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                            colors = OutlinedTextFieldDefaults.colors(
                                unfocusedContainerColor = SurfaceContainerLowest,
                                focusedContainerColor = SurfaceContainerLowest,
                                unfocusedBorderColor = OutlineVariant,
                                focusedBorderColor = Primary
                            )
                        )
                        if (fullNameError != null) {
                            Text(fullNameError!!, style = BodySm, color = Error, modifier = Modifier.padding(start = 4.dp, top = 2.dp))
                        }
                    }

                    Column {
                        OutlinedTextField(
                            value = email,
                            onValueChange = { 
                                email = it
                                validateEmail(it)
                            },
                            label = { Text("Email Address", style = LabelMd) },
                            modifier = Modifier.fillMaxWidth(),
                            shape = Shapes.Medium,
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
                            Text(emailError!!, style = BodySm, color = Error, modifier = Modifier.padding(start = 4.dp, top = 2.dp))
                        }
                    }

                    Column {
                        OutlinedTextField(
                            value = password,
                            onValueChange = { 
                                password = it
                                validatePassword(it)
                            },
                            label = { Text("Password", style = LabelMd) },
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
                            shape = Shapes.Medium,
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
                            Text(passwordError!!, style = BodySm, color = Error, modifier = Modifier.padding(start = 4.dp, top = 2.dp))
                        } else {
                            Text("Must be at least 8 characters long.", style = BodySm.copy(fontSize = 12.sp), color = OnSurfaceVariant, modifier = Modifier.padding(top = 4.dp, start = 4.dp))
                        }
                    }

                    Row(
                        verticalAlignment = Alignment.Top,
                        modifier = Modifier.padding(top = Dimens.SpacingSm, bottom = Dimens.SpacingMd)
                    ) {
                        Checkbox(
                            checked = termsAccepted,
                            onCheckedChange = { termsAccepted = it },
                            colors = CheckboxDefaults.colors(checkedColor = Primary, uncheckedColor = OutlineVariant),
                            modifier = Modifier.offset(x = (-12).dp, y = (-12).dp)
                        )
                        Text(
                            "I agree to the Terms of Service and Privacy Policy.",
                            style = BodySm,
                            color = OnSurfaceVariant,
                            modifier = Modifier.offset(x = (-12).dp)
                        )
                    }

                    if (state.error != null) {
                        Text(
                            text = state.error ?: "",
                            color = Error,
                            style = BodySm,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                    }

                    Button(
                        onClick = {
                            validateFullName(fullName)
                            validateEmail(email)
                            validatePassword(password)
                            
                            if (fullNameError == null && emailError == null && passwordError == null && termsAccepted) {
                                viewModel.setEvent(
                                    RegisterContract.Event.OnRegisterClicked(
                                        fullName, email, password, password
                                    )
                                )
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .shadow(4.dp, Shapes.Full),
                        shape = Shapes.Full,
                        enabled = !state.isLoading,
                        colors = ButtonDefaults.buttonColors(containerColor = Primary, contentColor = OnPrimary)
                    ) {
                        if (state.isLoading) {
                            CircularProgressIndicator(modifier = Modifier.size(24.dp), color = OnPrimary)
                        } else {
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.Center) {
                                Text("Register", style = LabelMd)
                                Spacer(modifier = Modifier.width(8.dp))
                                Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null, modifier = Modifier.size(20.dp))
                            }
                        }
                    }
                }

                // Divider
                Row(
                    modifier = Modifier.fillMaxWidth().padding(vertical = Dimens.SpacingLg),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    HorizontalDivider(modifier = Modifier.weight(1f), color = OutlineVariant.copy(alpha = 0.5f))
                    Text("Or", style = BodySm, color = OnSurfaceVariant, modifier = Modifier.padding(horizontal = Dimens.SpacingSm))
                    HorizontalDivider(modifier = Modifier.weight(1f), color = OutlineVariant.copy(alpha = 0.5f))
                }

                // Login Link
                Row(
                    modifier = Modifier.fillMaxWidth().padding(bottom = Dimens.SpacingSm),
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text("Already have an account? ", style = BodySm, color = OnSurfaceVariant)
                    Text(
                        text = "Log in",
                        style = LabelMd.copy(fontWeight = FontWeight.Bold),
                        color = Primary,
                        modifier = Modifier.clickable { onNavigateToLogin() }
                    )
                }
            }
        }
    }
}

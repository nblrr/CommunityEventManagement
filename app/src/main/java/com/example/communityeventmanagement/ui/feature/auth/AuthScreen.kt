package com.example.communityeventmanagement.ui.feature.auth

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.example.communityeventmanagement.R
import com.example.communityeventmanagement.domain.entities.User
import com.example.communityeventmanagement.ui.theme.CommunityEventManagementTheme
import com.example.communityeventmanagement.ui.theme.ThemePreviews
import kotlinx.coroutines.launch

@Composable
fun LoginScreen(
    onLoginSuccess: (User) -> Unit,
    onNavigateToRegister: () -> Unit,
    onNavigateBack: () -> Unit,
    viewModel: AuthViewModel = hiltViewModel()
) {
    val scope = rememberCoroutineScope()

    LoginContent(
        email = viewModel.loginEmail,
        password = viewModel.loginPassword,
        passwordVisible = viewModel.loginPasswordVisible,
        errorMessage = viewModel.loginErrorMessageResId?.let { stringResource(it) },
        isValid = viewModel.isLoginFormValid,
        onEmailChange = { 
            viewModel.loginEmail = it 
            viewModel.clearErrors()
        },
        onPasswordChange = { 
            viewModel.loginPassword = it 
            viewModel.clearErrors()
        },
        onTogglePassword = { viewModel.loginPasswordVisible = !viewModel.loginPasswordVisible },
        onLoginClick = { 
            scope.launch {
                viewModel.login(onLoginSuccess)
            }
        },
        onRegisterClick = onNavigateToRegister,
        onNavigateBack = onNavigateBack
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginContent(
    email: String,
    password: String,
    passwordVisible: Boolean,
    errorMessage: String?,
    isValid: Boolean,
    onEmailChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    onTogglePassword: () -> Unit,
    onLoginClick: () -> Unit,
    onRegisterClick: () -> Unit,
    onNavigateBack: () -> Unit,
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.login), fontWeight = FontWeight.Black) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = stringResource(R.string.back))
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier.fillMaxSize().padding(paddingValues).padding(24.dp).verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            Text(stringResource(R.string.login_hint), style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
            
            AuthTextField(
                value = email,
                onValueChange = onEmailChange,
                label = stringResource(R.string.label_email),
                icon = Icons.Default.Email,
                keyboardType = KeyboardType.Email
            )

            AuthTextField(
                value = password,
                onValueChange = onPasswordChange,
                label = stringResource(R.string.label_password),
                icon = Icons.Default.Lock,
                keyboardType = KeyboardType.Password,
                isPassword = true,
                passwordVisible = passwordVisible,
                onTogglePassword = onTogglePassword
            )

            if (errorMessage != null) {
                Text(errorMessage, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
            }

            Button(
                onClick = onLoginClick,
                enabled = isValid,
                modifier = Modifier.fillMaxWidth().height(56.dp),
                shape = MaterialTheme.shapes.medium
            ) { Text(stringResource(R.string.btn_login), fontWeight = FontWeight.Bold) }

            TextButton(onClick = onRegisterClick) {
                Text(stringResource(R.string.txt_no_account) + " " + stringResource(R.string.btn_register))
            }
        }
    }
}

@Composable
fun RegisterScreen(
    onRegisterSuccess: (User) -> Unit,
    onNavigateToLogin: () -> Unit,
    onNavigateBack: () -> Unit,
    viewModel: AuthViewModel = hiltViewModel()
) {
    val scope = rememberCoroutineScope()

    RegisterContent(
        name = viewModel.registerName,
        email = viewModel.registerEmail,
        password = viewModel.registerPassword,
        passwordVisible = viewModel.registerPasswordVisible,
        errorMessage = viewModel.registerErrorMessageResId?.let { stringResource(it) },
        isValid = viewModel.isRegisterFormValid,
        onNameChange = { 
            viewModel.registerName = it 
            viewModel.clearErrors()
        },
        onEmailChange = { 
            viewModel.registerEmail = it 
            viewModel.clearErrors()
        },
        onPasswordChange = { 
            viewModel.registerPassword = it 
            viewModel.clearErrors()
        },
        onTogglePassword = { viewModel.registerPasswordVisible = !viewModel.registerPasswordVisible },
        onRegisterClick = { 
            scope.launch {
                viewModel.register(onRegisterSuccess)
            }
        },
        onLoginClick = onNavigateToLogin,
        onNavigateBack = onNavigateBack
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterContent(
    name: String,
    email: String,
    password: String,
    passwordVisible: Boolean,
    errorMessage: String?,
    isValid: Boolean,
    onNameChange: (String) -> Unit,
    onEmailChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    onTogglePassword: () -> Unit,
    onRegisterClick: () -> Unit,
    onLoginClick: () -> Unit,
    onNavigateBack: () -> Unit,
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.title_create_account), fontWeight = FontWeight.Black) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = stringResource(R.string.back))
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier.fillMaxSize().padding(paddingValues).padding(24.dp).verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            Text(stringResource(R.string.register_hint), style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)

            AuthTextField(
                value = name,
                onValueChange = onNameChange,
                label = stringResource(R.string.label_full_name),
                icon = Icons.Default.Person
            )
            
            AuthTextField(
                value = email,
                onValueChange = onEmailChange,
                label = stringResource(R.string.label_email),
                icon = Icons.Default.Email,
                keyboardType = KeyboardType.Email
            )

            AuthTextField(
                value = password,
                onValueChange = onPasswordChange,
                label = stringResource(R.string.label_password),
                icon = Icons.Default.Lock,
                keyboardType = KeyboardType.Password,
                isPassword = true,
                passwordVisible = passwordVisible,
                onTogglePassword = onTogglePassword
            )

            if (errorMessage != null) {
                Text(errorMessage, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
            }

            Button(
                onClick = onRegisterClick,
                enabled = isValid,
                modifier = Modifier.fillMaxWidth().height(56.dp),
                shape = MaterialTheme.shapes.medium
            ) { Text(stringResource(R.string.btn_register_now), fontWeight = FontWeight.Bold) }

            TextButton(onClick = onLoginClick) {
                Text(stringResource(R.string.txt_already_have_account) + " " + stringResource(R.string.btn_login))
            }
        }
    }
}

@Composable
fun AuthTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    icon: ImageVector,
    keyboardType: KeyboardType = KeyboardType.Text,
    isPassword: Boolean = false,
    passwordVisible: Boolean = false,
    onTogglePassword: () -> Unit = {}
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = Modifier.fillMaxWidth(),
        label = { Text(label) },
        leadingIcon = { Icon(icon, contentDescription = null, tint = MaterialTheme.colorScheme.primary) },
        trailingIcon = {
            if (isPassword) {
                IconButton(onClick = onTogglePassword) {
                    Icon(
                        imageVector = if (passwordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                        contentDescription = null
                    )
                }
            }
        },
        visualTransformation = if (isPassword && !passwordVisible) PasswordVisualTransformation() else VisualTransformation.None,
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
        singleLine = true,
        shape = MaterialTheme.shapes.medium
    )
}

@ThemePreviews
@Composable
fun AuthPreview() {
    CommunityEventManagementTheme {
        LoginContent(
            email = "",
            password = "",
            passwordVisible = false,
            errorMessage = null,
            isValid = false,
            onEmailChange = {},
            onPasswordChange = {},
            onTogglePassword = {},
            onLoginClick = {},
            onRegisterClick = {},
            onNavigateBack = {}
        )
    }
}

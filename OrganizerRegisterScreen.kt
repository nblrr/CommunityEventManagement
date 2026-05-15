package com.example.communityeventmanagement.features.organizer

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Business
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.communityeventmanagement.R
import com.example.communityeventmanagement.data.model.UserProfile
import com.example.communityeventmanagement.ui.AppViewModelProvider
import com.example.communityeventmanagement.ui.theme.CommunityEventManagementTheme
import com.example.communityeventmanagement.ui.theme.ThemePreviews

@Composable
fun OrganizerRegisterScreen(
    onRegisterSuccess: (UserProfile) -> Unit,
    onNavigateBack: () -> Unit,
    viewModel: OrganizerRegisterViewModel = viewModel(factory = AppViewModelProvider.Factory),
) {
    OrganizerRegisterContent(
        organizerName = viewModel.organizerName,
        personInCharge = viewModel.personInCharge,
        phone = viewModel.phone,
        description = viewModel.description,
        errorMessage = viewModel.errorMessage,
        isFormValid = viewModel.isFormValid,
        onOrganizerNameChange = { viewModel.organizerName = it },
        onPersonInChargeChange = { viewModel.personInCharge = it },
        onPhoneChange = { viewModel.phone = it; viewModel.errorMessage = null },
        onDescriptionChange = { viewModel.description = it },
        onRegisterClick = { viewModel.registerAsOrganizer(onRegisterSuccess) },
        onNavigateBack = onNavigateBack
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrganizerRegisterContent(
    organizerName: String,
    personInCharge: String,
    phone: String,
    description: String,
    errorMessage: Int?,
    isFormValid: Boolean,
    onOrganizerNameChange: (String) -> Unit,
    onPersonInChargeChange: (String) -> Unit,
    onPhoneChange: (String) -> Unit,
    onDescriptionChange: (String) -> Unit,
    onRegisterClick: () -> Unit,
    onNavigateBack: () -> Unit,
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.title_organizer_register), fontWeight = FontWeight.Black) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = stringResource(R.string.back))
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier.fillMaxSize().padding(paddingValues).verticalScroll(rememberScrollState()).padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(stringResource(R.string.hint_organizer_register), style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
            
            OutlinedTextField(value = organizerName, onValueChange = onOrganizerNameChange, label = { Text(stringResource(R.string.label_organizer_name)) }, leadingIcon = { Icon(Icons.Default.Business, contentDescription = null) }, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(14.dp))
            OutlinedTextField(value = personInCharge, onValueChange = onPersonInChargeChange, label = { Text(stringResource(R.string.label_pic)) }, leadingIcon = { Icon(Icons.Default.Person, contentDescription = null) }, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(14.dp))
            
            OutlinedTextField(
                value = phone, 
                onValueChange = onPhoneChange, 
                label = { Text(stringResource(R.string.label_whatsapp)) }, 
                leadingIcon = { Icon(Icons.Default.Phone, contentDescription = null) }, 
                modifier = Modifier.fillMaxWidth(), 
                shape = RoundedCornerShape(14.dp),
                isError = errorMessage != null,
                supportingText = {
                    if (errorMessage != null) {
                        Text(stringResource(errorMessage), color = MaterialTheme.colorScheme.error)
                    }
                }
            )

            OutlinedTextField(value = description, onValueChange = onDescriptionChange, label = { Text(stringResource(R.string.label_short_desc)) }, leadingIcon = { Icon(Icons.Default.Description, contentDescription = null) }, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(14.dp), minLines = 3)

            Spacer(Modifier.height(32.dp))
            Button(
                onClick = onRegisterClick,
                enabled = isFormValid,
                modifier = Modifier.fillMaxWidth().height(56.dp),
                shape = RoundedCornerShape(16.dp)
            ) { Text(stringResource(R.string.btn_register_now), fontWeight = FontWeight.Black, fontSize = 16.sp) }
        }
    }
}

@ThemePreviews
@Composable
fun OrganizerRegisterScreenPreview() {
    CommunityEventManagementTheme {
        OrganizerRegisterContent(
            organizerName = "Komunitas Kreatif",
            personInCharge = "Budi Santoso",
            phone = "08123456789",
            description = "Komunitas untuk berbagi ide-ide kreatif.",
            errorMessage = null,
            isFormValid = true,
            onOrganizerNameChange = {},
            onPersonInChargeChange = {},
            onPhoneChange = {},
            onDescriptionChange = {},
            onRegisterClick = {},
            onNavigateBack = {}
        )
    }
}

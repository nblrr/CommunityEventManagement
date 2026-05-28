package com.example.communityeventmanagement.ui.feature.organizer

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.example.communityeventmanagement.R
import com.example.communityeventmanagement.domain.entities.User
import com.example.communityeventmanagement.ui.theme.CommunityEventManagementTheme
import com.example.communityeventmanagement.ui.theme.ThemePreviews

@Composable
fun OrganizerRegisterScreen(
    currentUser: User? = null,
    onRegisterSuccess: (String) -> Unit,
    onNavigateBack: () -> Unit,
    viewModel: OrganizerRegisterViewModel = hiltViewModel()
) {
    var organizerName by remember { mutableStateOf("") }
    var personInCharge by remember { mutableStateOf("") }
    var contact by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }

    OrganizerRegisterContent(
        organizerName = organizerName,
        personInCharge = personInCharge,
        contact = contact,
        description = description,
        errorMessage = viewModel.errorMessageResId?.let { stringResource(it) },
        onOrganizerNameChange = { 
            organizerName = it 
            viewModel.clearErrors()
        },
        onPersonInChargeChange = { 
            personInCharge = it 
            viewModel.clearErrors()
        },
        onContactChange = { 
            contact = it 
            viewModel.clearErrors()
        },
        onDescriptionChange = { 
            description = it 
            viewModel.clearErrors()
        },
        onRegisterClick = { 
            viewModel.register(organizerName, personInCharge, contact, description) {
                onRegisterSuccess(organizerName)
            }
        },
        onNavigateBack = onNavigateBack
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrganizerRegisterContent(
    organizerName: String,
    personInCharge: String,
    contact: String,
    description: String,
    errorMessage: String?,
    onOrganizerNameChange: (String) -> Unit,
    onPersonInChargeChange: (String) -> Unit,
    onContactChange: (String) -> Unit,
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
            modifier = Modifier.fillMaxSize().padding(paddingValues).padding(24.dp).verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            Text(stringResource(R.string.hint_organizer_register), style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
            
            OutlinedTextField(
                value = organizerName, 
                onValueChange = onOrganizerNameChange, 
                label = { Text(stringResource(R.string.label_organizer_name)) }, 
                modifier = Modifier.fillMaxWidth(),
                shape = MaterialTheme.shapes.medium
            )
            
            OutlinedTextField(
                value = personInCharge, 
                onValueChange = onPersonInChargeChange, 
                label = { Text(stringResource(R.string.label_pic)) }, 
                modifier = Modifier.fillMaxWidth(),
                shape = MaterialTheme.shapes.medium
            )

            OutlinedTextField(
                value = contact, 
                onValueChange = onContactChange, 
                label = { Text(stringResource(R.string.label_whatsapp)) }, 
                modifier = Modifier.fillMaxWidth(),
                shape = MaterialTheme.shapes.medium
            )

            OutlinedTextField(
                value = description, 
                onValueChange = onDescriptionChange, 
                label = { Text(stringResource(R.string.label_short_desc)) }, 
                modifier = Modifier.fillMaxWidth(), 
                minLines = 3,
                shape = MaterialTheme.shapes.medium
            )

            if (errorMessage != null) {
                Text(errorMessage, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
            }

            Spacer(Modifier.weight(1f))
            Button(
                onClick = onRegisterClick,
                enabled = organizerName.isNotBlank() && personInCharge.isNotBlank() && contact.isNotBlank(),
                modifier = Modifier.fillMaxWidth().height(56.dp),
                shape = MaterialTheme.shapes.medium
            ) { Text(stringResource(R.string.btn_register_now), fontWeight = FontWeight.Bold) }
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
            contact = "08123456789",
            description = "Komunitas seni dan desain.",
            errorMessage = null,
            onOrganizerNameChange = {},
            onPersonInChargeChange = {},
            onContactChange = {},
            onDescriptionChange = {},
            onRegisterClick = {},
            onNavigateBack = {}
        )
    }
}

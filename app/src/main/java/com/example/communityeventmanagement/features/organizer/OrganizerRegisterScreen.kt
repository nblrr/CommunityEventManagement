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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrganizerRegisterScreen(
    onRegisterSuccess: (UserProfile) -> Unit,
    onNavigateBack: () -> Unit,
    viewModel: OrganizerRegisterViewModel = viewModel(factory = AppViewModelProvider.Factory),
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
            
            OutlinedTextField(value = viewModel.organizerName, onValueChange = { viewModel.organizerName = it }, label = { Text(stringResource(R.string.label_organizer_name)) }, leadingIcon = { Icon(Icons.Default.Business, contentDescription = null) }, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(14.dp))
            OutlinedTextField(value = viewModel.personInCharge, onValueChange = { viewModel.personInCharge = it }, label = { Text(stringResource(R.string.label_pic)) }, leadingIcon = { Icon(Icons.Default.Person, contentDescription = null) }, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(14.dp))
            
            OutlinedTextField(
                value = viewModel.phone, 
                onValueChange = { viewModel.phone = it; viewModel.errorMessage = null }, 
                label = { Text(stringResource(R.string.label_whatsapp)) }, 
                leadingIcon = { Icon(Icons.Default.Phone, contentDescription = null) }, 
                modifier = Modifier.fillMaxWidth(), 
                shape = RoundedCornerShape(14.dp),
                isError = viewModel.errorMessage != null,
                supportingText = {
                    if (viewModel.errorMessage != null) {
                        Text(stringResource(viewModel.errorMessage!!), color = MaterialTheme.colorScheme.error)
                    }
                }
            )

            OutlinedTextField(value = viewModel.description, onValueChange = { viewModel.description = it }, label = { Text(stringResource(R.string.label_short_desc)) }, leadingIcon = { Icon(Icons.Default.Description, contentDescription = null) }, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(14.dp), minLines = 3)

            Spacer(Modifier.height(32.dp))
            Button(
                onClick = { viewModel.registerAsOrganizer(onRegisterSuccess) },
                enabled = viewModel.isFormValid,
                modifier = Modifier.fillMaxWidth().height(56.dp),
                shape = RoundedCornerShape(16.dp)
            ) { Text(stringResource(R.string.btn_register_now), fontWeight = FontWeight.Black, fontSize = 16.sp) }
        }
    }
}

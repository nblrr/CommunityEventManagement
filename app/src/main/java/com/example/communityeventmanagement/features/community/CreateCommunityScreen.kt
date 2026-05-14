package com.example.communityeventmanagement.features.community

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.communityeventmanagement.R
import com.example.communityeventmanagement.data.model.UserProfile
import com.example.communityeventmanagement.ui.AppViewModelProvider
import com.example.communityeventmanagement.util.ImagePickerBox

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateCommunityScreen(
    currentUser: UserProfile?,
    onCreateSuccess: (Int) -> Unit,
    onNavigateBack: () -> Unit,
    viewModel: CreateCommunityViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.title_create_community), fontWeight = FontWeight.Black) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = stringResource(R.string.back))
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier.fillMaxSize().padding(paddingValues).padding(20.dp).verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            OutlinedTextField(value = viewModel.name, onValueChange = { viewModel.name = it }, label = { Text(stringResource(R.string.label_community_name)) }, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(value = viewModel.category, onValueChange = { viewModel.category = it }, label = { Text(stringResource(R.string.label_category_hint)) }, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(value = viewModel.description, onValueChange = { viewModel.description = it }, label = { Text(stringResource(R.string.label_description)) }, modifier = Modifier.fillMaxWidth(), minLines = 4)
            
            ImagePickerBox(
                imageUri = viewModel.coverImageUri,
                onImageSelected = { viewModel.coverImageUri = it },
                label = stringResource(R.string.btn_choose_cover)
            )

            Spacer(Modifier.weight(1f))
            Button(
                onClick = { viewModel.createCommunity(onCreateSuccess) },
                enabled = viewModel.isFormValid,
                modifier = Modifier.fillMaxWidth().height(56.dp)
            ) { Text(stringResource(R.string.btn_create_now), fontWeight = FontWeight.Bold) }
        }
    }
}

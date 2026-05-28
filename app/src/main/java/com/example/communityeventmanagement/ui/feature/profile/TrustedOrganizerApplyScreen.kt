package com.example.communityeventmanagement.ui.feature.profile

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.example.communityeventmanagement.R
import com.example.communityeventmanagement.ui.components.PrimaryButton

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TrustedOrganizerApplyScreen(
    onNavigateBack: () -> Unit,
    onSuccess: () -> Unit,
    viewModel: TrustedOrganizerApplyViewModel = hiltViewModel()
) {
    val snackbarHostState = remember { SnackbarHostState() }
    val errorMsg = viewModel.errorMessageResId?.let { stringResource(it) }

    LaunchedEffect(errorMsg) {
        errorMsg?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearErrors()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.menu_apply_trusted)) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Rounded.ArrowBack, contentDescription = stringResource(R.string.back))
                    }
                }
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            Text(
                text = stringResource(R.string.menu_get_verification_badge),
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            OutlinedTextField(
                value = viewModel.reason,
                onValueChange = { viewModel.reason = it },
                label = { Text(stringResource(R.string.label_reason_apply_trusted)) },
                modifier = Modifier.fillMaxWidth(),
                minLines = 3
            )

            OutlinedTextField(
                value = viewModel.experience,
                onValueChange = { viewModel.experience = it },
                label = { Text(stringResource(R.string.label_experience_manage_community)) },
                modifier = Modifier.fillMaxWidth(),
                minLines = 3
            )

            Spacer(modifier = Modifier.weight(1f))

            PrimaryButton(
                text = stringResource(R.string.btn_send_application),
                onClick = { viewModel.submit(onSuccess) },
                enabled = viewModel.isFormValid && !viewModel.isSubmitting,
                isLoading = viewModel.isSubmitting,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

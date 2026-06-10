package com.example.communityeventmanagement.ui.feature.profile

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.example.communityeventmanagement.R
import com.example.communityeventmanagement.ui.components.PrimaryButton
import com.example.communityeventmanagement.util.UiEvent
import kotlinx.coroutines.flow.collectLatest

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TrustedOrganizerApplyScreen(
    onNavigateBack: () -> Unit,
    onSuccess: () -> Unit,
    onShowSnackbar: (String) -> Unit,
    viewModel: TrustedOrganizerApplyViewModel = hiltViewModel(),
) {
    val errorMsg = viewModel.errorMessageResId?.let { stringResource(it) }

    LaunchedEffect(key1 = true) {
        viewModel.uiEvent.collectLatest { event ->
            when (event) {
                is UiEvent.ShowSnackbar -> onShowSnackbar(event.message)
            }
        }
    }

    LaunchedEffect(errorMsg) {
        errorMsg?.let {
            onShowSnackbar(it)
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
        }
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

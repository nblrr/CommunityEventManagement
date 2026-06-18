package com.example.communityeventmanagementsystem.presentation.trusted

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.communityeventmanagementsystem.domain.model.TrustedApplication
import com.example.communityeventmanagementsystem.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TrustedAppScreen(
    viewModel: TrustedAppViewModel,
    onNavigateBack: () -> Unit
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.handleEvent(TrustedAppContract.Event.LoadMyApplication)
    }

    LaunchedEffect(key1 = Unit) {
        viewModel.effect.collect { effect ->
            when (effect) {
                is TrustedAppContract.Effect.ShowSuccessMessage -> {
                    // Application submitted successfully
                }
            }
        }
    }

    Scaffold(
        topBar = { TrustedAppTopBar(onNavigateBack) },
        containerColor = Background
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            if (state.isLoading && state.application == null) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = Primary)
                }
            } else if (state.error != null && state.application == null) {
                Box(
                    modifier = Modifier.fillMaxSize().padding(Dimens.ContainerPadding),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = state.error ?: "Terjadi kesalahan loading data pengajuan",
                            style = BodyLg,
                            color = Error,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(Dimens.SpacingMd))
                        Button(
                            onClick = { viewModel.handleEvent(TrustedAppContract.Event.LoadMyApplication) },
                            colors = ButtonDefaults.buttonColors(containerColor = Primary)
                        ) {
                            Text("Coba Lagi")
                        }
                    }
                }
            } else {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(horizontal = Dimens.ContainerPadding, vertical = Dimens.SpacingLg)
                ) {
                    TrustedAppHeader()
                    Spacer(modifier = Modifier.height(Dimens.SpacingLg))

                    val app = state.application
                    if (app != null) {
                        // User has already submitted an application, show the status card
                        ApplicationStatusCard(app)
                    } else {
                        // Show the form
                        TrustedAppForm(
                            communityName = state.communityName,
                            onCommunityNameChange = { viewModel.handleEvent(TrustedAppContract.Event.OnCommunityNameChanged(it)) },
                            reason = state.reason,
                            onReasonChange = { viewModel.handleEvent(TrustedAppContract.Event.OnReasonChanged(it)) },
                            experience = state.experience,
                            onExperienceChange = { viewModel.handleEvent(TrustedAppContract.Event.OnExperienceChanged(it)) },
                            isSubmitting = state.isSubmitting,
                            onSubmit = { viewModel.handleEvent(TrustedAppContract.Event.OnSubmitClicked) }
                        )
                    }
                    Spacer(modifier = Modifier.height(Dimens.SpacingXl))
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TrustedAppTopBar(onNavigateBack: () -> Unit) {
    TopAppBar(
        title = {
            Text(
                text = "Trusted Application",
                style = HeadlineMd,
                color = OnSurface,
                fontWeight = FontWeight.Bold
            )
        },
        navigationIcon = {
            IconButton(onClick = onNavigateBack) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = OnSurface)
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(containerColor = Surface)
    )
}

@Composable
fun TrustedAppHeader() {
    Column {
        Text(
            text = "Apply for Trusted Status",
            style = HeadlineLgMobile,
            color = OnSurface,
            modifier = Modifier.padding(bottom = Dimens.SpacingSm)
        )
        Text(
            text = "Gain exclusive benefits and signal reliability to your attendees. Trusted Organizers enjoy higher visibility and priority support.",
            style = BodyMd,
            color = OnSurfaceVariant
        )
    }
}

@Composable
fun ApplicationStatusCard(application: TrustedApplication) {
    val statusColor = when (application.status.lowercase()) {
        "approved" -> Color(0xFF4CAF50)
        "rejected" -> Error
        else -> Secondary
    }

    Surface(
        shape = Shapes.ExtraLarge,
        color = SurfaceContainerLowest,
        border = androidx.compose.foundation.BorderStroke(1.dp, SurfaceVariant),
        shadowElevation = 2.dp,
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(Dimens.SpacingLg), verticalArrangement = Arrangement.spacedBy(Dimens.SpacingMd)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Application Status", style = HeadlineMd, color = OnSurface, modifier = Modifier.weight(1f))
                Surface(
                    color = statusColor.copy(alpha = 0.15f),
                    shape = Shapes.Full
                ) {
                    Text(
                        text = application.status.uppercase(),
                        style = LabelMd.copy(fontWeight = FontWeight.Bold),
                        color = statusColor,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                        maxLines = 1
                    )
                }
            }

            HorizontalDivider(color = SurfaceVariant)

            Column(verticalArrangement = Arrangement.spacedBy(Dimens.SpacingSm)) {
                Text("Community Name", style = LabelMd, color = OnSurfaceVariant)
                Text(application.communityName, style = BodyLg.copy(fontWeight = FontWeight.SemiBold), color = OnSurface)
            }

            Column(verticalArrangement = Arrangement.spacedBy(Dimens.SpacingSm)) {
                Text("Reason for Application", style = LabelMd, color = OnSurfaceVariant)
                Text(application.reason ?: "-", style = BodyMd, color = OnSurface)
            }

            Column(verticalArrangement = Arrangement.spacedBy(Dimens.SpacingSm)) {
                Text("Organizer Experience", style = LabelMd, color = OnSurfaceVariant)
                Text(application.experience ?: "-", style = BodyMd, color = OnSurface)
            }

            if (!application.adminNotes.isNullOrBlank()) {
                Surface(
                    color = SurfaceContainerLow,
                    shape = Shapes.Large,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(Dimens.SpacingMd)) {
                        Text("Admin Notes", style = LabelMd, color = Primary)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(application.adminNotes, style = BodyMd, color = OnSurface)
                    }
                }
            }
        }
    }
}

@Composable
fun TrustedAppForm(
    communityName: String,
    onCommunityNameChange: (String) -> Unit,
    reason: String,
    onReasonChange: (String) -> Unit,
    experience: String,
    onExperienceChange: (String) -> Unit,
    isSubmitting: Boolean,
    onSubmit: () -> Unit
) {
    Surface(
        shape = Shapes.ExtraLarge,
        color = SurfaceContainerLowest,
        border = androidx.compose.foundation.BorderStroke(1.dp, SurfaceVariant),
        shadowElevation = 1.dp,
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(Dimens.SpacingLg),
            verticalArrangement = Arrangement.spacedBy(Dimens.SpacingLg)
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(Dimens.SpacingXs)) {
                Text("Community Name", style = LabelMd, color = OnSurface)
                OutlinedTextField(
                    value = communityName,
                    onValueChange = onCommunityNameChange,
                    placeholder = { Text("Enter your community name...", style = BodyMd) },
                    leadingIcon = { Icon(Icons.Default.Groups, contentDescription = null, tint = OutlineVariant) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = Shapes.Large,
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        unfocusedContainerColor = SurfaceBright,
                        focusedContainerColor = SurfaceBright,
                        unfocusedBorderColor = OutlineVariant,
                        focusedBorderColor = Primary
                    )
                )
            }

            Column(verticalArrangement = Arrangement.spacedBy(Dimens.SpacingXs)) {
                Text("Reason for Application", style = LabelMd, color = OnSurface)
                OutlinedTextField(
                    value = reason,
                    onValueChange = onReasonChange,
                    placeholder = { Text("Describe why your community should be granted trusted status...", style = BodyMd) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = Shapes.Large,
                    minLines = 4,
                    colors = OutlinedTextFieldDefaults.colors(
                        unfocusedContainerColor = SurfaceBright,
                        focusedContainerColor = SurfaceBright,
                        unfocusedBorderColor = OutlineVariant,
                        focusedBorderColor = Primary
                    )
                )
            }

            Column(verticalArrangement = Arrangement.spacedBy(Dimens.SpacingXs)) {
                Text("Organizer Experience", style = LabelMd, color = OnSurface)
                OutlinedTextField(
                    value = experience,
                    onValueChange = onExperienceChange,
                    placeholder = { Text("Describe your experience organizing events...", style = BodyMd) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = Shapes.Large,
                    minLines = 3,
                    colors = OutlinedTextFieldDefaults.colors(
                        unfocusedContainerColor = SurfaceBright,
                        focusedContainerColor = SurfaceBright,
                        unfocusedBorderColor = OutlineVariant,
                        focusedBorderColor = Primary
                    )
                )
            }

            HorizontalDivider(color = SurfaceVariant)

            Button(
                onClick = onSubmit,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = Shapes.Large,
                colors = ButtonDefaults.buttonColors(containerColor = Primary, contentColor = OnPrimary),
                elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp),
                enabled = !isSubmitting
            ) {
                Text("Submit Application", style = LabelMd.copy(fontWeight = FontWeight.Bold))
                Spacer(modifier = Modifier.width(Dimens.SpacingSm))
                Icon(Icons.AutoMirrored.Filled.Send, contentDescription = null, modifier = Modifier.size(18.dp))
            }
        }
    }
}

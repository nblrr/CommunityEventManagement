package com.example.communityeventmanagementsystem.presentation.profile

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AssignmentInd
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.communityeventmanagementsystem.presentation.components.AppButton
import com.example.communityeventmanagementsystem.presentation.components.StandardTopAppBar
import com.example.communityeventmanagementsystem.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrganizerRegistrationScreen(
    onNavigateBack: () -> Unit,
    onNavigateToDashboard: () -> Unit,
    viewModel: OrganizerRegistrationViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.effect.collect { effect ->
            when (effect) {
                is OrganizerRegistrationContract.Effect.NavigateBack -> onNavigateBack()
                is OrganizerRegistrationContract.Effect.NavigateToOrganizerDashboard -> onNavigateToDashboard()
            }
        }
    }

    Scaffold(
        topBar = {
            StandardTopAppBar(
                title = "Daftar Jadi Organizer",
                onNavigateBack = onNavigateBack
            )
        },
        containerColor = Background
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            if (state.isSuccess) {
                RegistrationSuccessContent(onNavigateToDashboard)
            } else {
                RegistrationFormContent(
                    isLoading = state.isLoading,
                    error = state.error,
                    onSubmit = { viewModel.handleEvent(OrganizerRegistrationContract.Event.OnSubmitClicked) }
                )
            }
        }
    }
}

@Composable
fun RegistrationFormContent(
    isLoading: Boolean,
    error: String?,
    onSubmit: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(Dimens.ContainerPadding),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Surface(
            shape = CircleShape,
            color = PrimaryContainer,
            modifier = Modifier.size(120.dp)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    imageVector = Icons.Default.AssignmentInd,
                    contentDescription = null,
                    tint = OnPrimaryContainer,
                    modifier = Modifier.size(64.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(Dimens.SpacingLg))

        Text(
            text = "Jadi Organizer Event",
            style = HeadlineMd,
            color = OnSurface,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(Dimens.SpacingSm))

        Text(
            text = "Dengan menjadi organizer, Anda dapat membuat dan mengelola komunitas serta event Anda sendiri.",
            style = BodyMd,
            color = OnSurfaceVariant,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(Dimens.SpacingXl))

        Card(
            colors = CardDefaults.cardColors(containerColor = SurfaceContainerLowest),
            shape = Shapes.Large,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(Dimens.SpacingLg)) {
                Text("Keuntungan Menjadi Organizer:", style = LabelLg, color = Primary)
                Spacer(modifier = Modifier.height(Dimens.SpacingMd))
                BenefitItem("Buat Komunitas Anda sendiri")
                BenefitItem("Publikasikan Event ke seluruh pengguna")
                BenefitItem("Kelola peserta dan kehadiran")
                BenefitItem("Akses ke Dashboard Organizer")
            }
        }

        Spacer(modifier = Modifier.height(Dimens.SpacingXl))

        if (error != null) {
            Text(error, color = Error, style = BodySm, modifier = Modifier.padding(bottom = Dimens.SpacingMd))
        }

        AppButton(
            text = "Konfirmasi Jadi Organizer",
            onClick = onSubmit,
            isLoading = isLoading,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
fun BenefitItem(text: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(vertical = 4.dp)
    ) {
        Icon(Icons.Default.CheckCircle, contentDescription = null, tint = Primary, modifier = Modifier.size(18.dp))
        Spacer(modifier = Modifier.width(8.dp))
        Text(text, style = BodyMd, color = OnSurface)
    }
}

@Composable
fun RegistrationSuccessContent(onNavigateToDashboard: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(Dimens.ContainerPadding),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Default.CheckCircle,
            contentDescription = null,
            tint = Color(0xFF4CAF50),
            modifier = Modifier.size(100.dp)
        )
        Spacer(modifier = Modifier.height(Dimens.SpacingLg))
        Text("Selamat!", style = HeadlineLg, color = OnSurface)
        Text(
            "Anda sekarang adalah seorang Organizer.",
            style = BodyLg,
            color = OnSurfaceVariant,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(Dimens.SpacingXl))
        AppButton(
            text = "Ke Dashboard Organizer",
            onClick = onNavigateToDashboard,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

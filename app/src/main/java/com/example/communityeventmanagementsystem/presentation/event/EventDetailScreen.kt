package com.example.communityeventmanagementsystem.presentation.event

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.example.communityeventmanagementsystem.domain.model.Event as DomainEvent
import com.example.communityeventmanagementsystem.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EventDetailScreen(
    onNavigateBack: () -> Unit,
    viewModel: EventDetailViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    Scaffold(
        topBar = { EventDetailTopBar(onNavigateBack) },
        bottomBar = {
            state.event?.let { event ->
                EventDetailBottomBar(
                    event = event,
                    isRegistered = state.isRegistered,
                    isRegistering = state.isRegistering,
                    onRegisterClick = { viewModel.handleEvent(EventDetailContract.Event.Register) },
                    onUnregisterClick = { viewModel.handleEvent(EventDetailContract.Event.Unregister) }
                )
            }
        },
        containerColor = Background
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            if (state.isLoading) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = Primary)
                }
            } else if (state.error != null) {
                Box(
                    modifier = Modifier.fillMaxSize().padding(Dimens.ContainerPadding),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = state.error ?: "Terjadi kesalahan loading detail event",
                            style = BodyLg,
                            color = Error,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(Dimens.SpacingMd))
                        Button(
                            onClick = { viewModel.handleEvent(EventDetailContract.Event.LoadDetail(state.event?.id ?: 0L)) },
                            colors = ButtonDefaults.buttonColors(containerColor = Primary)
                        ) {
                            Text("Coba Lagi")
                        }
                    }
                }
            } else {
                state.event?.let { event ->
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(rememberScrollState())
                    ) {
                        EventHeroImage(event)
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = Dimens.ContainerPadding, vertical = Dimens.SpacingLg),
                            verticalArrangement = Arrangement.spacedBy(Dimens.SpacingLg)
                        ) {
                            EventHeader(event)
                            OrganizerSection(event)
                            RatingsSection(event)
                            EventInfoGrid(event)
                            AboutEventSection(event)
                            Spacer(modifier = Modifier.height(Dimens.SpacingXl))
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EventDetailTopBar(onNavigateBack: () -> Unit) {
    TopAppBar(
        title = {
            Text(
                text = "Detail Event",
                style = HeadlineMd,
                color = OnSurface,
                modifier = Modifier.fillMaxWidth().padding(end = 40.dp),
                textAlign = TextAlign.Center
            )
        },
        navigationIcon = {
            IconButton(onClick = onNavigateBack) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = OnSurface)
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(containerColor = SurfaceContainerLowest.copy(alpha = 0.95f))
    )
}

@Composable
fun EventHeroImage(event: DomainEvent) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(4f / 3f)
            .background(SurfaceVariant)
    ) {
        AsyncImage(
            model = event.coverImageUrl ?: "https://lh3.googleusercontent.com/aida-public/AB6AXuAR3Dcj-Q2OPloo0NJlSBB_1zGtFpGMraHJK3C4BvnFUETyoAv-vp5tQ0xhAGAgY_0NB4RA-ro__H7XpSqSVbbnjt8V3uSMCE2X8j5uQK8WMbfkOn6dosqQY8hZhfscXtioXbSVr5RE1WIEb9tgMYk9puhGgsEXwGErhoUQyN_Oz18HRfI04Q7vXZFx5vB571RVw_6YwQX-mokykP9D42HFHIoqFSGvpEXpwMZnmBVTjghc7y2JNKsPzv_vtxxLZsCd1RCOfDMDA4eD",
            contentDescription = "Event cover photo",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )
    }
}

@Composable
fun EventHeader(event: DomainEvent) {
    Column {
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.padding(bottom = Dimens.SpacingSm)) {
            Surface(
                color = PrimaryContainer,
                shape = Shapes.Full
            ) {
                Text(
                    text = (event.categoryName ?: "Kategori").uppercase(),
                    style = LabelMd.copy(fontSize = 12.sp),
                    color = OnPrimaryContainer,
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
                )
            }
            Surface(
                color = SurfaceVariant,
                shape = Shapes.Full
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
                ) {
                    Box(modifier = Modifier.size(8.dp).background(Secondary, CircleShape))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = event.status.uppercase(),
                        style = LabelMd.copy(fontSize = 12.sp),
                        color = OnSurfaceVariant
                    )
                }
            }
        }
        Text(
            text = event.title,
            style = HeadlineLgMobile,
            color = OnSurface
        )
    }
}

@Composable
fun OrganizerSection(event: DomainEvent) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = Dimens.SpacingMd)
    ) {
        AsyncImage(
            model = event.organizerImageUrl ?: "https://lh3.googleusercontent.com/aida-public/AB6AXuDXzYJxfBHMg5nORM-L_m2ehnpEY317yqoJXYyREIe9GOLVo-rDRXa-y2pLpNdGioT2Tc_62nZRXw-ro69F1xRoMVDQqn9yVICyBTDgtj65nmLu3j8b5G0DTQ3Dm-2wRe5A0pqUM3tE_cTew1qqUs2bT0xpq5-7DXoQ_GhAvtXyzCi1cJ5OZMRQ8LAvZHl_3knfhC4nDrZhW_FH3dAxxcLjura0JZUP6tDBkTHfJM9mJImW39NnKK4hWg_JPaYm7GCcxqdmrg04tu6X",
            contentDescription = "Organizer",
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .border(1.dp, OutlineVariant.copy(alpha = 0.5f), CircleShape),
            contentScale = ContentScale.Crop
        )
        Spacer(modifier = Modifier.width(Dimens.SpacingSm))
        Column {
            Text("Organized by", style = BodySm, color = Outline)
            Text(event.organizerName ?: "Admin Komunitas", style = LabelMd, color = OnSurface)
        }
    }
    HorizontalDivider(color = OutlineVariant.copy(alpha = 0.3f))
}

@Composable
fun RatingsSection(event: DomainEvent) {
    if (event.rating > 0f) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth().padding(bottom = Dimens.SpacingMd)
        ) {
            Text(event.rating.toString(), style = HeadlineMd, color = OnSurface)
            Spacer(modifier = Modifier.width(4.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(2.dp)) {
                repeat(5) { index ->
                    val isFull = event.rating >= (index + 1)
                    Icon(
                        imageVector = if (isFull) Icons.Default.Star else Icons.Default.StarBorder,
                        contentDescription = null,
                        tint = Primary,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
            Spacer(modifier = Modifier.width(4.dp))
            Text("(${event.reviewCount} Review)", style = BodyMd, color = OutlineVariant)
        }
        HorizontalDivider(color = OutlineVariant.copy(alpha = 0.3f))
    }
}

@Composable
fun EventInfoGrid(event: DomainEvent) {
    Column(verticalArrangement = Arrangement.spacedBy(Dimens.SpacingMd)) {
        InfoCard(
            icon = Icons.Default.CalendarMonth,
            title = event.eventDate,
            subtitle = event.eventTime
        )
        InfoCard(
            icon = Icons.Default.LocationOn,
            title = if (event.isOnline) "Online" else "Offline",
            subtitle = event.location
        )
        InfoCard(
            icon = Icons.Default.Groups,
            title = "${event.attendeeCount} / ${event.maxAttendees} Peserta",
            subtitle = "",
            isCapacity = true,
            ratio = if (event.maxAttendees > 0) (event.attendeeCount.toFloat() / event.maxAttendees) else 0f
        )
    }
}

@Composable
fun InfoCard(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    subtitle: String,
    isCapacity: Boolean = false,
    ratio: Float = 0f
) {
    Surface(
        shape = Shapes.Large,
        color = SurfaceContainerLow,
        border = androidx.compose.foundation.BorderStroke(1.dp, OutlineVariant.copy(alpha = 0.4f)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(Dimens.SpacingMd),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                shape = Shapes.Medium,
                color = SurfaceContainerLowest,
                shadowElevation = 1.dp,
                modifier = Modifier.size(40.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(icon, contentDescription = null, tint = Primary)
                }
            }
            Spacer(modifier = Modifier.width(Dimens.SpacingMd))
            if (isCapacity) {
                Column(modifier = Modifier.weight(1f)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(title, style = LabelMd, color = OnSurface)
                        if (ratio > 0.8f) {
                            Text("Filling Fast", style = BodySm.copy(fontWeight = FontWeight.SemiBold), color = Secondary)
                        }
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(6.dp)
                            .background(SurfaceVariant, Shapes.Full)
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth(ratio.coerceIn(0f, 1f))
                                .height(6.dp)
                                .background(Primary, Shapes.Full)
                        )
                    }
                }
            } else {
                Column {
                    Text(title, style = LabelMd, color = OnSurface)
                    Text(subtitle, style = BodySm, color = OnSurfaceVariant, maxLines = 2, overflow = TextOverflow.Ellipsis)
                }
            }
        }
    }
}

@Composable
fun AboutEventSection(event: DomainEvent) {
    Column {
        Text("Tentang Event", style = HeadlineMd, color = OnSurface, modifier = Modifier.padding(bottom = Dimens.SpacingMd))
        Text(
            text = event.description ?: "Tidak ada deskripsi untuk kegiatan ini.",
            style = BodyMd,
            color = OnSurfaceVariant
        )
    }
}

@Composable
fun EventDetailBottomBar(
    event: DomainEvent,
    isRegistered: Boolean,
    isRegistering: Boolean,
    onRegisterClick: () -> Unit,
    onUnregisterClick: () -> Unit
) {
    Surface(
        color = SurfaceContainerLowest.copy(alpha = 0.95f),
        shadowElevation = 8.dp,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = Dimens.ContainerPadding, vertical = Dimens.SpacingMd),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                if (event.price > 0) {
                    Text("Rp ${event.price}", style = HeadlineMd.copy(fontWeight = FontWeight.Bold), color = Primary)
                } else {
                    Text("Gratis", style = HeadlineMd.copy(fontWeight = FontWeight.Bold), color = Primary)
                }
            }
            Button(
                onClick = { if (isRegistered) onUnregisterClick() else onRegisterClick() },
                modifier = Modifier
                    .widthIn(min = 160.dp)
                    .height(48.dp)
                    .shadow(4.dp, Shapes.Full),
                shape = Shapes.Full,
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (isRegistered) SurfaceVariant else Primary,
                    contentColor = if (isRegistered) OnSurfaceVariant else OnPrimary
                ),
                enabled = !isRegistering
            ) {
                Text(if (isRegistered) "Batal Daftar" else "Daftar Sekarang", style = LabelMd)
            }
        }
    }
}

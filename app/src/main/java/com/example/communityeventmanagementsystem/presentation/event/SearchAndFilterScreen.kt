package com.example.communityeventmanagementsystem.presentation.event

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.communityeventmanagementsystem.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchAndFilterScreen(
    onNavigateBack: () -> Unit = {}
) {
    var searchQuery by remember { mutableStateOf("") }
    var showFilterSheet by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopSearchAnchor(
                searchQuery = searchQuery,
                onSearchQueryChange = { searchQuery = it },
                onFilterClick = { showFilterSheet = true },
                onNavigateBack = onNavigateBack
            )
        },
        containerColor = Background
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentPadding = PaddingValues(horizontal = Dimens.ContainerPadding, vertical = Dimens.SpacingLg),
            verticalArrangement = Arrangement.spacedBy(Dimens.SpacingMd)
        ) {
            if (searchQuery.isEmpty()) {
                item {
                    Text("Recommended for You", style = HeadlineMd, color = OnSurface, modifier = Modifier.padding(bottom = Dimens.SpacingSm))
                }
                item {
                    SearchResultCard(
                        title = "Photography Masterclass",
                        badge = "TRENDING",
                        date = "Oct 30 • 09:00 AM",
                        location = "Art Center, Jakarta",
                        joined = "12/20 Joined",
                        price = "Free",
                        imageUrl = "https://images.unsplash.com/photo-1452780212940-6f5c0d14d848",
                        onClick = {}
                    )
                }
                item {
                    SearchResultCard(
                        title = "Startup Networking Night",
                        badge = "POPULAR",
                        date = "Nov 05 • 07:00 PM",
                        location = "Co-working Space, Bandung",
                        joined = "85/100 Joined",
                        price = "Paid",
                        imageUrl = "https://images.unsplash.com/photo-1515187029135-18ee286d815b",
                        onClick = {}
                    )
                }
            } else {
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(bottom = Dimens.SpacingSm),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Search Results", style = HeadlineMd, color = OnSurface)
                        Text("12 matches", style = BodySm, color = Outline)
                    }
                }

                item {
                    SearchResultCard(
                        title = "AI in Design Workshop",
                        badge = "MINT SUCCESS",
                        date = "Oct 24 • 10:00 AM",
                        location = "Design District, SF",
                        joined = "45/100 Joined",
                        price = "Free",
                        imageUrl = "https://lh3.googleusercontent.com/aida-public/AB6AXuDCtZGQquUTNVDY2t9TQKrBQrHn46lWBtFu8aikpgANPFRBC8z3UxcMkTdxG9fi9WmbCul7g-p8Cb8E-P19QVm9tVRYi9PwqAlqDqAeNVGGOSdLNchpp22Rb3BpB_OkK67ju-yAJdJBZg4nND4qEmMUp0HhjdSfZVFrLYdpMzy61Gk6xCgGoW5ISU-tP1Nq63fltcrK_BC9kLvK5JUXvjI_KT86RrBHGenhjdwzrr-BSmZ6ZO2XKmDbPsMbRHrT1r0rPI0hKHDtlP5y",
                        onClick = {}
                    )
                }
                
                item {
                    SearchResultCard(
                        title = "Future of Fintech",
                        badge = null,
                        date = "Nov 12 • 02:00 PM",
                        location = "Tech Hub, New York",
                        joined = "128/200 Joined",
                        price = "$49.00",
                        imageUrl = "https://lh3.googleusercontent.com/aida-public/AB6AXuCj0jsbhBb5-180dAmoXnwCqwVHmalxQDopeIFk8Gj6tA5PHsS0ObracqsZd4l_yycDAGZy1KMVBlchsNWwHwFJ6Hx1OtPyWPi5TCaxolsnrp_qOOAf3GTHh1T2-zqrdcHhfslDXfxqSLItLt0kG0M0JfU_Pk0spNttc1nRjNRkZ0WV0e8XWUnn_UlUPxODMSGH-A4DnUry-9FZ-Zdw1G_w_wGAkne65yHdQ5PnmdMtG7an5BSgT3RzbSZwUaI1XuU0-Cyqmu9nULyb",
                        onClick = {}
                    )
                }
                
                item {
                    SearchResultCard(
                        title = "Cybersecurity Seminar",
                        badge = null,
                        date = "Oct 28 • 09:30 AM",
                        location = "Virtual Office • Online",
                        joined = "82/150 Joined",
                        price = "Free",
                        imageUrl = "https://lh3.googleusercontent.com/aida-public/AB6AXuARFD86az0ZNwf-Ds5msAQgPH6WUOkFiOknCMZIs6GvEmWe1_7mtUXHwqzDJ4Cz6C6dY99kd3tjRWnY6Zb0XjbtwFNQkGa2qE9R8-tBxsXMTwUaXZC7nPYCQng1tVg2xNO1kx3ForF90wVVNSXLmhgDE4k6PiC4oaWe2jLBDEu7RNcwEMQIOO5c4mVNEojJDJdsFkXsyFbAHNka0GV0jfZ5OsFoXvTZMqH2Zcz-yvCkqYEvR9ZrStGRLyviFLrKH6gmMZsuWfwTxVyQ",
                        onClick = {}
                    )
                }
            }
        }
    }

    if (showFilterSheet) {
        ModalBottomSheet(
            onDismissRequest = { showFilterSheet = false },
            containerColor = Surface
        ) {
            FilterSheetContent(onDismiss = { showFilterSheet = false })
        }
    }
}

@Composable
fun TopSearchAnchor(
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit,
    onFilterClick: () -> Unit,
    onNavigateBack: () -> Unit
) {
    Surface(
        color = Surface.copy(alpha = 0.8f),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(horizontal = Dimens.ContainerPadding, vertical = Dimens.SpacingMd),
            horizontalArrangement = Arrangement.spacedBy(Dimens.SpacingSm),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onNavigateBack) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Primary)
            }
            OutlinedTextField(
                value = searchQuery,
                onValueChange = onSearchQueryChange,
                placeholder = { Text("Search...") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = Primary) },
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = { onSearchQueryChange("") }) {
                            Icon(Icons.Default.Close, contentDescription = "Clear", tint = Outline)
                        }
                    }
                },
                modifier = Modifier.weight(1f),
                shape = Shapes.Large,
                singleLine = true,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                colors = OutlinedTextFieldDefaults.colors(
                    unfocusedContainerColor = SurfaceContainerLow,
                    focusedContainerColor = SurfaceContainerLow,
                    unfocusedBorderColor = Color.Transparent,
                    focusedBorderColor = Primary
                )
            )
            Button(
                onClick = onFilterClick,
                modifier = Modifier.size(56.dp),
                shape = Shapes.Large,
                contentPadding = PaddingValues(0.dp),
                colors = ButtonDefaults.buttonColors(containerColor = PrimaryFixed, contentColor = OnPrimaryFixed)
            ) {
                Icon(Icons.Default.Tune, contentDescription = "Filter")
            }
        }
    }
}

@Composable
fun SearchResultCard(
    title: String,
    badge: String?,
    date: String,
    location: String,
    joined: String,
    price: String,
    imageUrl: String,
    onClick: () -> Unit
) {
    Surface(
        shape = Shapes.Large,
        color = SurfaceContainerLowest,
        border = androidx.compose.foundation.BorderStroke(1.dp, SurfaceVariant.copy(alpha = 0.3f)),
        shadowElevation = 1.dp,
        modifier = Modifier.fillMaxWidth().clickable { onClick() }
    ) {
        Column {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(2f / 1f)
                    .background(SurfaceContainerHigh)
            ) {
                AsyncImage(
                    model = imageUrl,
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
                if (badge != null) {
                    Surface(
                        color = SecondaryContainer.copy(alpha = 0.9f),
                        shape = Shapes.Full,
                        modifier = Modifier.align(Alignment.TopEnd).padding(12.dp)
                    ) {
                        Text(badge, style = LabelMd, color = OnSecondaryContainer, modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp))
                    }
                }
            }
            Column(modifier = Modifier.padding(Dimens.SpacingMd)) {
                Text(title, style = HeadlineMd, color = OnSurface, modifier = Modifier.padding(bottom = 8.dp))
                Column(verticalArrangement = Arrangement.spacedBy(4.dp), modifier = Modifier.padding(bottom = 16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.CalendarToday, contentDescription = null, tint = Outline, modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(date, style = BodySm, color = Outline)
                    }
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.LocationOn, contentDescription = null, tint = Outline, modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(location, style = BodySm, color = Outline)
                    }
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Row(modifier = Modifier.padding(end = 8.dp)) {
                            Box(modifier = Modifier.size(24.dp).clip(CircleShape).background(PrimaryFixed).border(2.dp, Surface, CircleShape))
                            Box(modifier = Modifier.size(24.dp).clip(CircleShape).background(SecondaryFixed).border(2.dp, Surface, CircleShape).offset(x = (-8).dp))
                        }
                        Text(joined, style = LabelMd, color = Primary, modifier = Modifier.offset(x = (-8).dp))
                    }
                    Text(price, style = LabelMd.copy(fontWeight = if (price == "Free") FontWeight.SemiBold else FontWeight.Bold), color = if (price == "Free") Secondary else OnSurfaceVariant)
                }
            }
        }
    }
}

@Composable
fun FilterSheetContent(onDismiss: () -> Unit) {
    var selectedCategory by remember { mutableStateOf("All") }
    var selectedDate by remember { mutableStateOf("Today") }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = Dimens.ContainerPadding)
            .padding(bottom = Dimens.SpacingXl)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(bottom = Dimens.SpacingLg),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Filters", style = HeadlineMd, color = OnSurface)
            Text("Reset", style = LabelMd, color = Primary, modifier = Modifier.clickable { 
                selectedCategory = "All"
                selectedDate = "Today"
            })
        }

        FilterSection(title = "Categories") {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                listOf("All", "Tech", "Music", "Sports").forEach { category ->
                    FilterChipCustom(category, selectedCategory == category) {
                        selectedCategory = category
                    }
                }
            }
        }

        FilterSection(title = "Date") {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                listOf("Today", "This Week", "This Month").forEach { date ->
                    FilterChipCustom(date, selectedDate == date) {
                        selectedDate = date
                    }
                }
            }
        }

        Button(
            onClick = onDismiss,
            modifier = Modifier.fillMaxWidth().height(56.dp).padding(top = Dimens.SpacingMd),
            shape = Shapes.Large,
            colors = ButtonDefaults.buttonColors(containerColor = Primary, contentColor = OnPrimary)
        ) {
            Text("Show Results", style = HeadlineMd.copy(fontSize = 18.sp))
        }
    }
}

@Composable
fun FilterSection(title: String, content: @Composable () -> Unit) {
    Column(modifier = Modifier.padding(bottom = Dimens.SpacingLg)) {
        Text(title, style = LabelMd, color = Outline, modifier = Modifier.padding(bottom = Dimens.SpacingSm))
        content()
    }
}

@Composable
fun FilterChipCustom(text: String, isSelected: Boolean, onClick: () -> Unit) {
    Surface(
        shape = Shapes.Full,
        color = if (isSelected) Primary else Color.Transparent,
        border = androidx.compose.foundation.BorderStroke(1.dp, if (isSelected) Primary else OutlineVariant),
        modifier = Modifier.clickable { onClick() }
    ) {
        Text(
            text = text,
            style = LabelMd,
            color = if (isSelected) OnPrimary else OnSurfaceVariant,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
        )
    }
}

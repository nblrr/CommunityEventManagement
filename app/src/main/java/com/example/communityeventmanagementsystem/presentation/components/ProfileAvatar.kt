package com.example.communityeventmanagementsystem.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import coil.compose.AsyncImage
import com.example.communityeventmanagementsystem.ui.theme.OnPrimaryContainer
import com.example.communityeventmanagementsystem.ui.theme.PrimaryContainer

@Composable
fun ProfileAvatar(
    imageUrl: String?,
    name: String,
    modifier: Modifier = Modifier,
    textStyle: TextStyle = TextStyle(fontWeight = FontWeight.Bold)
) {
    Box(
        modifier = modifier
            .clip(CircleShape)
            .background(PrimaryContainer),
        contentAlignment = Alignment.Center
    ) {
        if (!imageUrl.isNullOrBlank()) {
            AsyncImage(
                model = imageUrl,
                contentDescription = "Profile Avatar",
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
        } else {
            val initials = if (name.isNotBlank()) name.take(1).uppercase() else "?"
            Text(
                text = initials,
                style = textStyle,
                color = OnPrimaryContainer
            )
        }
    }
}

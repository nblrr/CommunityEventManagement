package com.example.communityeventmanagementsystem.presentation.components

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.communityeventmanagementsystem.ui.theme.*

@Composable
fun AppError(
    message: String,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier,
    errorCode: Int? = null
) {
    val isSessionExpired = errorCode == 401
    
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(Dimens.ContainerPadding),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (!isSessionExpired) {
            Icon(
                imageVector = Icons.Default.ErrorOutline,
                contentDescription = "Error icon",
                tint = MaterialTheme.colorScheme.error,
                modifier = Modifier.size(72.dp)
            )
            Spacer(modifier = Modifier.height(20.dp))
            Text(
                text = "Oops! Something went wrong",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onBackground
            )
            Spacer(modifier = Modifier.height(8.dp))
        }

        Text(
            text = message,
            style = MaterialTheme.typography.bodyLarge.copy(
                fontWeight = if (isSessionExpired) FontWeight.Medium else FontWeight.Normal,
                fontSize = if (isSessionExpired) 18.sp else 16.sp
            ),
            color = if (isSessionExpired) Color(0xFFB71C1C) else MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f),
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 16.dp)
        )
        
        Spacer(modifier = Modifier.height(24.dp))
        
        Button(
            onClick = onRetry,
            colors = ButtonDefaults.buttonColors(
                containerColor = Primary
            ),
            shape = Shapes.Full,
            modifier = Modifier.widthIn(min = 120.dp)
        ) {
            Text(
                text = "Coba Lagi",
                style = LabelMd,
                modifier = Modifier.padding(horizontal = 8.dp)
            )
        }
    }
}

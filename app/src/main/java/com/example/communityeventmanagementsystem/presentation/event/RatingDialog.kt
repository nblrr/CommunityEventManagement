package com.example.communityeventmanagementsystem.presentation.event

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.StarBorder
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.communityeventmanagementsystem.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RatingDialog(
    onDismiss: () -> Unit,
    onSubmit: (Int, String) -> Unit
) {
    var rating by remember { mutableIntStateOf(5) }
    var comment by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Berikan Rating Event", style = HeadlineSm) },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(Dimens.SpacingMd)
            ) {
                Text("Bagaimana pengalaman Anda di event ini?", style = BodyMd, color = OnSurfaceVariant)
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    repeat(5) { index ->
                        val currentStar = index + 1
                        Icon(
                            imageVector = if (rating >= currentStar) Icons.Default.Star else Icons.Default.StarBorder,
                            contentDescription = null,
                            tint = Primary,
                            modifier = Modifier
                                .size(40.dp)
                                .clickable { rating = currentStar }
                        )
                    }
                }

                OutlinedTextField(
                    value = comment,
                    onValueChange = { comment = it },
                    label = { Text("Komentar (Opsional)") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = Shapes.Large,
                    minLines = 3
                )
            }
        },
        confirmButton = {
            Button(
                onClick = { onSubmit(rating, comment) },
                colors = ButtonDefaults.buttonColors(containerColor = Primary)
            ) {
                Text("Kirim")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Batal", color = Primary)
            }
        },
        containerColor = SurfaceContainerLowest
    )
}

package com.example.communityeventmanagementsystem.presentation.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

enum class ButtonVariant {
    PRIMARY, SECONDARY, OUTLINED, TEXT
}

@Composable
fun AppButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    variant: ButtonVariant = ButtonVariant.PRIMARY,
    isLoading: Boolean = false,
    enabled: Boolean = true,
    leadingIcon: @Composable (() -> Unit)? = null
) {
    val shape = RoundedCornerShape(16.dp)
    val contentPadding = PaddingValues(horizontal = 24.dp, vertical = 16.dp)

    val colors = when (variant) {
        ButtonVariant.PRIMARY -> ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.primary,
            contentColor = MaterialTheme.colorScheme.onPrimary,
            disabledContainerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f),
            disabledContentColor = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.5f)
        )
        ButtonVariant.SECONDARY -> ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.secondary,
            contentColor = MaterialTheme.colorScheme.onSecondary,
            disabledContainerColor = MaterialTheme.colorScheme.secondary.copy(alpha = 0.5f),
            disabledContentColor = MaterialTheme.colorScheme.onSecondary.copy(alpha = 0.5f)
        )
        ButtonVariant.OUTLINED -> ButtonDefaults.outlinedButtonColors(
            contentColor = MaterialTheme.colorScheme.primary,
            disabledContentColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.38f)
        )
        ButtonVariant.TEXT -> ButtonDefaults.textButtonColors(
            contentColor = MaterialTheme.colorScheme.primary,
            disabledContentColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.38f)
        )
    }

    if (variant == ButtonVariant.OUTLINED) {
        OutlinedButton(
            onClick = onClick,
            modifier = modifier.heightIn(min = 56.dp),
            enabled = enabled && !isLoading,
            shape = shape,
            colors = colors as ButtonColors,
            border = ButtonDefaults.outlinedButtonBorder.copy(
                width = 1.5.dp,
                brush = androidx.compose.ui.graphics.SolidColor(MaterialTheme.colorScheme.outline)
            ),
            contentPadding = contentPadding
        ) {
            ButtonContent(text, isLoading, leadingIcon, MaterialTheme.colorScheme.primary)
        }
    } else if (variant == ButtonVariant.TEXT) {
        TextButton(
            onClick = onClick,
            modifier = modifier.heightIn(min = 56.dp),
            enabled = enabled && !isLoading,
            shape = shape,
            colors = colors as ButtonColors,
            contentPadding = contentPadding
        ) {
            ButtonContent(text, isLoading, leadingIcon, MaterialTheme.colorScheme.primary)
        }
    } else {
        Button(
            onClick = onClick,
            modifier = modifier.heightIn(min = 56.dp),
            enabled = enabled && !isLoading,
            shape = shape,
            colors = colors as ButtonColors,
            contentPadding = contentPadding
        ) {
            val contentColor = if (variant == ButtonVariant.PRIMARY) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSecondary
            ButtonContent(text, isLoading, leadingIcon, contentColor)
        }
    }
}

@Composable
private fun RowScope.ButtonContent(
    text: String,
    isLoading: Boolean,
    leadingIcon: @Composable (() -> Unit)?,
    contentColor: Color
) {
    if (isLoading) {
        CircularProgressIndicator(
            modifier = Modifier.size(20.dp),
            color = contentColor,
            strokeWidth = 2.dp
        )
    } else {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            if (leadingIcon != null) {
                leadingIcon()
                Spacer(modifier = Modifier.width(8.dp))
            }
            Text(
                text = text,
                style = MaterialTheme.typography.labelLarge
            )
        }
    }
}

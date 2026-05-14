package com.example.communityeventmanagement.util

import androidx.compose.foundation.clickable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DatePickerField(
    label: String,
    selectedDateMillis: Long?,
    onDateSelected: (Long?) -> Unit,
    modifier: Modifier = Modifier
) {
    var showPicker by remember { mutableStateOf(false) }

    val displayText = selectedDateMillis?.let { millis ->
        SimpleDateFormat("d MMMM yyyy", Locale.forLanguageTag("id-ID")).format(Date(millis))
    } ?: "Pilih tanggal..."

    OutlinedTextField(
        value = displayText,
        onValueChange = {},
        label = { Text(label) },
        readOnly = true,
        trailingIcon = {
            Icon(Icons.Default.CalendarToday, contentDescription = "Pilih tanggal")
        },
        shape = RoundedCornerShape(14.dp),
        modifier = modifier.then(
            Modifier.clickable { showPicker = true }
        ),
        enabled = false, // disable keyboard, buka dialog via clickable
        colors = OutlinedTextFieldDefaults.colors(
            disabledTextColor = MaterialTheme.colorScheme.onSurface,
            disabledBorderColor = MaterialTheme.colorScheme.outline,
            disabledLabelColor = MaterialTheme.colorScheme.onSurfaceVariant,
            disabledTrailingIconColor = MaterialTheme.colorScheme.onSurfaceVariant
        )
    )

    if (showPicker) {
        val pickerState = rememberDatePickerState(
            initialSelectedDateMillis = selectedDateMillis ?: System.currentTimeMillis()
        )
        DatePickerDialog(
            onDismissRequest = { showPicker = false },
            confirmButton = {
                TextButton(onClick = {
                    onDateSelected(pickerState.selectedDateMillis)
                    showPicker = false
                }) { Text("Pilih") }
            },
            dismissButton = {
                TextButton(onClick = { showPicker = false }) { Text("Batal") }
            }
        ) {
            DatePicker(state = pickerState)
        }
    }
}

object DateFormatter {
    fun formatEventDate(dateStr: String): String {
        return try {
            val parts = dateStr.trim().split(" ")
            if (parts.size == 3) {
                val day = parts[0].toInt()
                val month = parts[1].toInt()
                val year = parts[2].toInt()
                val calendar = Calendar.getInstance().apply {
                    set(year, month - 1, day)
                }
                SimpleDateFormat("d MMMM yyyy", Locale.forLanguageTag("id-ID")).format(calendar.time)
            } else {
                dateStr
            }
        } catch (_: Exception) {
            dateStr
        }
    }

    fun isUpcoming(dateStr: String): Boolean {
        return try {
            val parts = dateStr.trim().split(" ")
            if (parts.size == 3) {
                val day = parts[0].toIntOrNull() ?: 1
                val month = parts[1].toIntOrNull() ?: 1
                val year = parts[2].toIntOrNull() ?: 0
                val eventCal = Calendar.getInstance().apply {
                    set(year, month - 1, day, 23, 59, 59)
                }
                eventCal.after(Calendar.getInstance())
            } else {
                val currentYear = Calendar.getInstance().get(Calendar.YEAR)
                val yearInStr = dateStr.filter { it.isDigit() }.takeLast(4).toIntOrNull()
                yearInStr != null && yearInStr >= currentYear
            }
        } catch (_: Exception) {
            false
        }
    }
}

fun Long.toDateString(): String {
    return SimpleDateFormat("d M yyyy", Locale.getDefault()).format(Date(this))
}

fun Long.toDisplayDateString(): String {
    return SimpleDateFormat("d MMMM yyyy", Locale.forLanguageTag("id-ID")).format(Date(this))
}
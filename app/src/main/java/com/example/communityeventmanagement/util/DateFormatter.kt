package com.example.communityeventmanagement.util

import java.text.SimpleDateFormat
import java.util.*

object DateFormatter {
    fun formatEventDate(dateStr: String): String {
        if (dateStr.isBlank()) return ""
        return try {
            val parts = dateStr.trim().split(" ")
            if (parts.size == 3) {
                val day = parts[0].toIntOrNull() ?: 1
                val month = parts[1].toIntOrNull() ?: 1
                val year = parts[2].toIntOrNull() ?: 2025
                
                val calendar = Calendar.getInstance()
                calendar.set(year, month - 1, day)
                
                val sdf = SimpleDateFormat("d MMMM yyyy", Locale.forLanguageTag("id-ID"))
                sdf.format(calendar.time)
            } else {
                dateStr
            }
        } catch (_: Exception) {
            dateStr
        }
    }
}

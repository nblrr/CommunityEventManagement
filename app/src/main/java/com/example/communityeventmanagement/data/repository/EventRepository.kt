package com.example.communityeventmanagement.data.repository

import java.util.Calendar

class EventRepository {
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

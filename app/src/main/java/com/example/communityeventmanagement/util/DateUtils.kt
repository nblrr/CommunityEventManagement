package com.example.communityeventmanagement.util

import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

object DateUtils {
    private const val ID_LOCALE_TAG = "id-ID"
    private const val DB_DATE_FORMAT = "d M yyyy"
    private const val DISPLAY_DATE_FORMAT = "dd MMMM yyyy"

    private fun getLocale() = Locale.forLanguageTag(ID_LOCALE_TAG)

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
                SimpleDateFormat(DISPLAY_DATE_FORMAT, getLocale()).format(calendar.time)
            } else {
                dateStr
            }
        } catch (_: Exception) {
            dateStr
        }
    }

    fun isUpcoming(dateStr: String, timeStr: String = ""): Boolean {
        return try {
            val parts = dateStr.trim().split(" ")
            if (parts.size == 3) {
                val day = parts[0].toIntOrNull() ?: 1
                val month = parts[1].toIntOrNull() ?: 1
                val year = parts[2].toIntOrNull() ?: 0
                
                val eventCal = Calendar.getInstance().apply {
                    set(year, month - 1, day)
                    
                    if (timeStr.isNotBlank()) {
                        val timeParts = timeStr.trim().split(".")
                        if (timeParts.size == 2) {
                            val hour = timeParts[0].toIntOrNull() ?: 0
                            val minute = timeParts[1].toIntOrNull() ?: 0
                            set(Calendar.HOUR_OF_DAY, hour)
                            set(Calendar.MINUTE, minute)
                            set(Calendar.SECOND, 0)
                            set(Calendar.MILLISECOND, 0)
                        } else {
                            set(Calendar.HOUR_OF_DAY, 23)
                            set(Calendar.MINUTE, 59)
                            set(Calendar.SECOND, 59)
                        }
                    } else {
                        set(Calendar.HOUR_OF_DAY, 23)
                        set(Calendar.MINUTE, 59)
                        set(Calendar.SECOND, 59)
                    }
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

    fun isToday(dateStr: String): Boolean {
        val todayStr = SimpleDateFormat(DB_DATE_FORMAT, getLocale()).format(Date())
        return dateStr.trim() == todayStr
    }

    fun isThisWeek(dateStr: String): Boolean {
        val cal = Calendar.getInstance()
        val currentWeek = cal[Calendar.WEEK_OF_YEAR]
        val currentYear = cal[Calendar.YEAR]
        return try {
            val parts = dateStr.trim().split(" ")
            val eventCal = Calendar.getInstance().apply {
                set(parts[2].toInt(), parts[1].toInt() - 1, parts[0].toInt())
            }
            (eventCal[Calendar.WEEK_OF_YEAR] == currentWeek) && (eventCal[Calendar.YEAR] == currentYear)
        } catch (_: Exception) {
            false
        }
    }

    fun isThisMonth(dateStr: String): Boolean {
        val cal = Calendar.getInstance()
        val currentMonth = cal[Calendar.MONTH] + 1
        val currentYear = cal[Calendar.YEAR]
        return try {
            val parts = dateStr.trim().split(" ")
            (parts[1].toInt() == currentMonth) && (parts[2].toInt() == currentYear)
        } catch (_: Exception) {
            false
        }
    }
}

fun Long.toDateString(): String {
    return SimpleDateFormat("d M yyyy", Locale.forLanguageTag("id-ID")).format(Date(this))
}

fun Long.toDisplayDateString(): String {
    return SimpleDateFormat("d MMMM yyyy", Locale.forLanguageTag("id-ID")).format(Date(this))
}

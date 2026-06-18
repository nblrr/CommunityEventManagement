package com.example.communityeventmanagementsystem.core.common

import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import java.util.Locale

object DateTimeUtils {
    private val INDONESIAN_LOCALE = Locale("id", "ID")

    // Formatter to output: 29 Jul 2026
    private val DATE_DISPLAY_FORMATTER = DateTimeFormatter.ofPattern("d MMM yyyy", INDONESIAN_LOCALE)
    
    // Formatter to output: 15 Mei 2000
    private val BIRTH_DATE_DISPLAY_FORMATTER = DateTimeFormatter.ofPattern("d MMMM yyyy", INDONESIAN_LOCALE)

    // Formatter to parse ISO 8601 (e.g. 2026-06-18T17:15:30.000000Z or with offset/timezone)
    private val ISO_FORMATTER = DateTimeFormatter.ISO_DATE_TIME

    // Parse date safely from string
    private fun parseLocalDate(dateStr: String?): LocalDate? {
        if (dateStr.isNullOrBlank()) return null
        return try {
            if (dateStr.contains("T")) {
                parseLocalDateTime(dateStr)?.toLocalDate()
            } else {
                LocalDate.parse(dateStr)
            }
        } catch (e: Exception) {
            try {
                LocalDate.parse(dateStr, DateTimeFormatter.ofPattern("yyyy-MM-dd", INDONESIAN_LOCALE))
            } catch (ex: Exception) {
                null
            }
        }
    }

    // Parse local date time safely
    private fun parseLocalDateTime(dateTimeStr: String?): LocalDateTime? {
        if (dateTimeStr.isNullOrBlank()) return null
        return try {
            if (dateTimeStr.endsWith("Z")) {
                Instant.parse(dateTimeStr).atZone(ZoneId.systemDefault()).toLocalDateTime()
            } else {
                try {
                    LocalDateTime.parse(dateTimeStr, ISO_FORMATTER)
                } catch (e: Exception) {
                    try {
                        java.time.OffsetDateTime.parse(dateTimeStr).toLocalDateTime()
                    } catch (ex: Exception) {
                        try {
                            LocalDateTime.parse(dateTimeStr, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss", INDONESIAN_LOCALE))
                        } catch (ex2: Exception) {
                            LocalDateTime.parse(dateTimeStr, DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss", INDONESIAN_LOCALE))
                        }
                    }
                }
            }
        } catch (e: Exception) {
            null
        }
    }

    // Parse local time safely
    private fun parseLocalTime(timeStr: String?): LocalTime? {
        if (timeStr.isNullOrBlank()) return null
        return try {
            LocalTime.parse(timeStr)
        } catch (e: Exception) {
            try {
                LocalTime.parse(timeStr, DateTimeFormatter.ofPattern("HH:mm:ss"))
            } catch (ex: Exception) {
                try {
                    LocalTime.parse(timeStr, DateTimeFormatter.ofPattern("HH:mm"))
                } catch (ex2: Exception) {
                    null
                }
            }
        }
    }

    fun formatBirthDate(birthDateStr: String?): String {
        val date = parseLocalDate(birthDateStr) ?: return birthDateStr ?: "-"
        return date.format(BIRTH_DATE_DISPLAY_FORMATTER)
    }

    fun formatEventDate(eventDateStr: String?): String {
        val date = parseLocalDate(eventDateStr) ?: return eventDateStr ?: ""
        return date.format(DATE_DISPLAY_FORMATTER)
    }

    fun formatEventTime(timeStr: String?, endTimeStr: String? = null): String {
        val start = parseLocalTime(timeStr) ?: return timeStr ?: ""
        val formattedStart = start.format(DateTimeFormatter.ofPattern("HH.mm"))
        
        if (!endTimeStr.isNullOrBlank()) {
            val end = parseLocalTime(endTimeStr)
            if (end != null) {
                val formattedEnd = end.format(DateTimeFormatter.ofPattern("HH.mm"))
                return "$formattedStart - $formattedEnd WIB"
            }
        }
        return "$formattedStart WIB"
    }

    fun formatEventDateTime(eventDateStr: String?, timeStr: String?, endTimeStr: String? = null): String {
        val dateFormatted = formatEventDate(eventDateStr)
        val timeFormatted = formatEventTime(timeStr, endTimeStr)
        if (dateFormatted.isBlank()) return timeFormatted
        if (timeFormatted.isBlank()) return dateFormatted
        return "$dateFormatted • $timeFormatted"
    }

    fun formatLocation(isOnline: Boolean, location: String): String {
        val type = if (isOnline) "Online" else "Offline"
        if (location.isBlank()) return type
        if (location.startsWith(type, ignoreCase = true)) return location
        return "$type • $location"
    }

    // Relative format:
    // Baru saja
    // 5 menit lalu
    // 2 jam lalu
    // Kemarin
    // 29 Jul 2026
    fun formatRelativeTime(dateTimeStr: String?): String {
        if (dateTimeStr.isNullOrBlank()) return ""
        val parsedDateTime = parseLocalDateTime(dateTimeStr) ?: return dateTimeStr
        val messageInstant = parsedDateTime.atZone(ZoneId.systemDefault()).toInstant()
        val nowInstant = Instant.now()
        
        val diffSeconds = ChronoUnit.SECONDS.between(messageInstant, nowInstant)
        if (diffSeconds < 0) {
            return formatEventDate(dateTimeStr)
        }
        if (diffSeconds < 60) {
            return "Baru saja"
        }
        val diffMinutes = ChronoUnit.MINUTES.between(messageInstant, nowInstant)
        if (diffMinutes < 60) {
            return "$diffMinutes menit lalu"
        }
        val diffHours = ChronoUnit.HOURS.between(messageInstant, nowInstant)
        if (diffHours < 24) {
            return "$diffHours jam lalu"
        }
        
        val messageDate = parsedDateTime.toLocalDate()
        val today = LocalDate.now()
        if (messageDate.isEqual(today.minusDays(1))) {
            return "Kemarin"
        }
        
        return messageDate.format(DATE_DISPLAY_FORMATTER)
    }

    /**
     * Compute the event status based on eventDate, eventTime, and endTime.
     * Returns "UPCOMING", "ONGOING", or "COMPLETED".
     * Falls back to [fallbackStatus] if date/time parsing fails.
     */
    fun computeEventStatus(
        eventDateStr: String?,
        eventTimeStr: String?,
        endTimeStr: String?,
        fallbackStatus: String = "UPCOMING"
    ): String {
        val date = parseLocalDate(eventDateStr) ?: return fallbackStatus
        val startTime = parseLocalTime(eventTimeStr) ?: return fallbackStatus
        val startDateTime = LocalDateTime.of(date, startTime)

        val endDateTime = if (!endTimeStr.isNullOrBlank()) {
            val endTimeParsed = parseLocalTime(endTimeStr)
            if (endTimeParsed != null) {
                val endDt = LocalDateTime.of(date, endTimeParsed)
                // If end_time is before start_time, it means it crosses midnight
                if (endDt.isBefore(startDateTime)) endDt.plusDays(1) else endDt
            } else {
                startDateTime.plusHours(2)
            }
        } else {
            startDateTime.plusHours(2)
        }

        val now = LocalDateTime.now()
        return when {
            now.isBefore(startDateTime) -> "UPCOMING"
            now.isAfter(endDateTime) -> "COMPLETED"
            else -> "ONGOING"
        }
    }
}

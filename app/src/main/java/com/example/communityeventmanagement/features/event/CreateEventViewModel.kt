package com.example.communityeventmanagement.features.event

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.example.communityeventmanagement.data.model.Event
import com.example.communityeventmanagement.data.repository.CommunityRepository
import com.example.communityeventmanagement.util.toDateString
import kotlinx.coroutines.delay
import java.util.Calendar
import java.util.TimeZone

class CreateEventViewModel(
    private val communityRepository: CommunityRepository
) : ViewModel() {
    var title by mutableStateOf("")
    var description by mutableStateOf("")
    var selectedDateMillis by mutableStateOf<Long?>(null)
    var time by mutableStateOf("")
    var location by mutableStateOf("")
    var category by mutableStateOf("")
    var coverImageUri by mutableStateOf<String?>(null)
    var isSubmitting by mutableStateOf(false)
    var showSuccessSheet by mutableStateOf(false)

    val timeSlots = listOf(
        "07.00", "08.00", "09.00", "10.00", "11.00", "12.00",
        "13.00", "14.00", "15.00", "16.00", "17.00", "18.00", "19.00", "20.00"
    )

    val categoryOptions = listOf(
        "Technology", "Design", "Business", "Education",
        "Health", "Art", "Music", "Sports", "Social"
    )

    val isTimeValid: Boolean
        get() {
            if (time.isBlank()) return false
            val parts = time.split(".")
            if (parts.size != 2) return false
            val hour = parts[0].toIntOrNull() ?: return false
            val minute = parts[1].toIntOrNull() ?: return false
            return hour in 0..23 && minute in 0..59
        }

    val isDateTimeValid: Boolean
        get() {
            val millis = selectedDateMillis ?: return true
            val now = Calendar.getInstance()
            val eventCal = Calendar.getInstance().apply {
                val dateCal = Calendar.getInstance(TimeZone.getTimeZone("UTC")).apply {
                    timeInMillis = millis
                }
                set(Calendar.YEAR, dateCal.get(Calendar.YEAR))
                set(Calendar.MONTH, dateCal.get(Calendar.MONTH))
                set(Calendar.DAY_OF_MONTH, dateCal.get(Calendar.DAY_OF_MONTH))
                
                if (isTimeValid) {
                    val parts = this@CreateEventViewModel.time.split(".")
                    set(Calendar.HOUR_OF_DAY, parts[0].toInt())
                    set(Calendar.MINUTE, parts[1].toInt())
                    set(Calendar.SECOND, 0)
                    set(Calendar.MILLISECOND, 0)
                } else {
                    set(Calendar.HOUR_OF_DAY, 23)
                    set(Calendar.MINUTE, 59)
                }
            }
            return eventCal.after(now)
        }

    val isFormValid: Boolean
        get() = title.isNotBlank() && description.isNotBlank() && selectedDateMillis != null && location.isNotBlank() && isDateTimeValid && isTimeValid

    suspend fun submit(communityId: Int) {
        if (!isFormValid) return
        
        isSubmitting = true
        delay(800)
        
        val dateString = selectedDateMillis!!.toDateString()
        val newEventId = (communityRepository.communities.flatMap { it.events }.maxOfOrNull { it.id } ?: 0) + 1
        val newEvent = Event(
            id = newEventId,
            title = title.trim(),
            description = description.trim(),
            date = dateString,
            time = time,
            location = location.trim(),
            category = category,
            coverImageUri = coverImageUri,
            communityId = communityId,
            registeredUserIds = emptyList()
        )
        val index = communityRepository.communities.indexOfFirst { it.id == communityId }
        if (index != -1) {
            communityRepository.communities[index] = communityRepository.communities[index].copy(
                events = communityRepository.communities[index].events + newEvent
            )
            communityRepository.saveCommunityData()
            showSuccessSheet = true
        }
        
        isSubmitting = false
    }
}

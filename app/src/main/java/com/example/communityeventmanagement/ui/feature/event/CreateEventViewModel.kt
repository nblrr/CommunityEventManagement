package com.example.communityeventmanagement.ui.feature.event

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.communityeventmanagement.domain.entities.AppCategories
import com.example.communityeventmanagement.domain.entities.Event
import com.example.communityeventmanagement.domain.usecase.CreateEvent
import com.example.communityeventmanagement.domain.usecase.GetCommunities
import com.example.communityeventmanagement.domain.usecase.GetEventDetail
import com.example.communityeventmanagement.domain.usecase.UpdateEvent
import com.example.communityeventmanagement.util.toDateString
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.util.Calendar
import java.util.TimeZone

class CreateEventViewModel(
    private val getCommunities: GetCommunities,
    private val getEventDetail: GetEventDetail,
    private val createEvent: CreateEvent,
    private val updateEvent: UpdateEvent
) : ViewModel() {
    var title by mutableStateOf("")
    var description by mutableStateOf("")
    var selectedDateMillis by mutableStateOf<Long?>(null)
    var time by mutableStateOf("")
    var location by mutableStateOf("")
    var category by mutableStateOf("")
    var maxAttendees by mutableStateOf("")
    var coverImageUri by mutableStateOf<String?>(null)
    var isSubmitting by mutableStateOf(false)
    var showSuccessSheet by mutableStateOf(false)
    var isEditMode by mutableStateOf(false)
    var existingEvent: Event? = null
    var errorMessageResId by mutableStateOf<Int?>(null)

    val timeSlots = listOf(
        "07.00", "08.00", "09.00", "10.00", "11.00", "12.00",
        "13.00", "14.00", "15.00", "16.00", "17.00", "18.00", "19.00", "20.00"
    )

    val categoryOptions = AppCategories

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
        get() = title.isNotBlank() && description.isNotBlank() && selectedDateMillis != null && location.isNotBlank() && isDateTimeValid && isTimeValid && (maxAttendees.isEmpty() || maxAttendees.toIntOrNull() != null)

    fun loadEvent(eventId: Int, communityId: Int) {
        viewModelScope.launch {
            val event = getEventDetail(eventId, communityId).first()
            if (event != null) {
                existingEvent = event
                isEditMode = true
                title = event.title
                description = event.description
                location = event.location
                category = event.category
                maxAttendees = if (event.maxAttendees > 0) event.maxAttendees.toString() else ""
                time = event.time
                coverImageUri = event.coverImageUri
                // Date parsing would be needed here to set selectedDateMillis
                // For simplicity, we might just skip setting it or implement parsing
            }
        }
    }

    suspend fun submit(communityId: Int) {
        if (!isFormValid) return
        
        val dateMillis = selectedDateMillis ?: return
        isSubmitting = true
        delay(800)
        
        val dateString = dateMillis.toDateString()
        
        if (isEditMode) {
            existingEvent?.let { current ->
                val updated = current.copy(
                    title = title.trim(),
                    description = description.trim(),
                    date = dateString,
                    time = time,
                    location = location.trim(),
                    category = category,
                    maxAttendees = maxAttendees.toIntOrNull() ?: 0,
                    coverImageUri = coverImageUri
                )
                val result = updateEvent(communityId, updated)
                if (result.isSuccess) {
                    showSuccessSheet = true
                } else {
                    errorMessageResId = com.example.communityeventmanagement.R.string.msg_no_data_found
                }
            }
        } else {
            val newEventId = (System.currentTimeMillis() % 100000).toInt()
            val newEvent = Event(
                id = newEventId,
                title = title.trim(),
                description = description.trim(),
                date = dateString,
                time = time,
                location = location.trim(),
                category = category,
                maxAttendees = maxAttendees.toIntOrNull() ?: 0,
                coverImageUri = coverImageUri,
                communityId = communityId,
                registeredUserIds = emptyList()
            )
            
            val result = createEvent(communityId, newEvent)
            if (result.isSuccess) {
                showSuccessSheet = true
            } else {
                errorMessageResId = com.example.communityeventmanagement.R.string.msg_no_data_found
            }
        }
        
        isSubmitting = false
    }

    fun clearErrors() {
        errorMessageResId = null
    }
}

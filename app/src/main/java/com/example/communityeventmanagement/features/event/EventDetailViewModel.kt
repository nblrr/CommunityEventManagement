package com.example.communityeventmanagement.features.event

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.example.communityeventmanagement.data.model.Event
import com.example.communityeventmanagement.data.repository.CommunityRepository
import com.example.communityeventmanagement.util.DateFormatter

class EventDetailViewModel(
    private val communityRepository: CommunityRepository,
) : ViewModel() {
    var event by mutableStateOf<Event?>(null)
    var communityName by mutableStateOf("")

    val isRegistered: Boolean
        get() = event?.id?.let { communityRepository.registeredEventIds.contains(it) } ?: false

    fun loadEvent(eventId: Int, communityId: Int) {
        val community = communityRepository.communities.find { it.id == communityId }
        communityName = community?.name ?: ""
        event = community?.events?.find { it.id == eventId }
    }

    fun isOrganizer(userId: String?): Boolean {
        if (userId == null) return false
        val community = communityRepository.communities.find { it.events.any { ev -> ev.id == event?.id } }
        return community?.organizerId == userId
    }

    fun isUpcoming(): Boolean = event?.let { DateFormatter.isUpcoming(it.date) } ?: false

    fun addGalleryImage(communityId: Int, eventId: Int, uri: String) {
        communityRepository.addGalleryImage(communityId, eventId, uri)
        loadEvent(eventId, communityId) // reload
    }

    fun addRating(communityId: Int, eventId: Int, userId: String, userName: String, score: Int, comment: String) {
        communityRepository.addEventRating(communityId, eventId, userId, userName, score, comment)
        loadEvent(eventId, communityId) // reload
    }
    
    fun toggleRegistration(communityId: Int, eventId: Int, userId: String) {
        communityRepository.toggleEventRegistration(communityId, eventId, userId)
        loadEvent(eventId, communityId)
    }
}

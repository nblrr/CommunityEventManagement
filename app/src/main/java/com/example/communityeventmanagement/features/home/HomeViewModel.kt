package com.example.communityeventmanagement.features.home

import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.communityeventmanagement.data.repository.CommunityRepository
import com.example.communityeventmanagement.data.repository.UserRepository
import com.example.communityeventmanagement.util.DateFormatter
import kotlinx.coroutines.launch

class HomeViewModel(
    private val userRepository: UserRepository,
    private val communityRepository: CommunityRepository,
) : ViewModel() {
    
    companion object {
        const val CATEGORY_ALL = "Semua"
        const val DATE_FILTER_ANYTIME = "Kapan Saja"
        const val DATE_FILTER_TODAY = "Hari Ini"
        const val DATE_FILTER_THIS_WEEK = "Minggu Ini"
        const val DATE_FILTER_THIS_MONTH = "Bulan Ini"
    }

    var searchQuery by mutableStateOf("")
    var selectedCategory by mutableStateOf(CATEGORY_ALL)
    var selectedDateFilter by mutableStateOf(DATE_FILTER_ANYTIME)
    var isRefreshing by mutableStateOf(false)

    val categories by derivedStateOf {
        listOf(CATEGORY_ALL) + communityRepository.communities.asSequence()
            .map { it.category }
            .distinct()
            .toList()
    }

    val recommendedCommunities by derivedStateOf {
        communityRepository.getRecommendedCommunities()
    }

    val recommendedEvents by derivedStateOf {
        communityRepository.getRecommendedEvents { DateFormatter.isUpcoming(it) }
    }

    val filteredEvents by derivedStateOf {
        val allEvents = communityRepository.communities.flatMap { community ->
            community.events.map { it to community.id }
        }
        allEvents.filter { (event, _) ->
            val matchesQuery = event.title.contains(searchQuery, ignoreCase = true) || 
                               event.description.contains(searchQuery, ignoreCase = true)
            val matchesCategory = (selectedCategory == CATEGORY_ALL) || (event.category == selectedCategory)
            val matchesDate = when (selectedDateFilter) {
                DATE_FILTER_TODAY -> DateFormatter.isToday(event.date)
                DATE_FILTER_THIS_WEEK -> DateFormatter.isThisWeek(event.date)
                DATE_FILTER_THIS_MONTH -> DateFormatter.isThisMonth(event.date)
                else -> true
            }
            matchesQuery && matchesCategory && matchesDate
        }
    }

    fun isEventRegistered(eventId: Int): Boolean {
        return communityRepository.registeredEventIds.contains(eventId)
    }

    fun refresh() {
        viewModelScope.launch {
            isRefreshing = true
            communityRepository.loadCommunities()
            userRepository.loadUsers()
            userRepository.currentUser?.let { communityRepository.refreshUserParticipation(it) }
            isRefreshing = false
        }
    }
}

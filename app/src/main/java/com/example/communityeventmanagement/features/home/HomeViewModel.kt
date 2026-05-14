package com.example.communityeventmanagement.features.home

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.example.communityeventmanagement.data.repository.CommunityRepository
import com.example.communityeventmanagement.data.repository.UserRepository
import com.example.communityeventmanagement.util.DateFormatter
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class HomeViewModel(
    private val userRepository: UserRepository,
    private val communityRepository: CommunityRepository,
) : ViewModel() {
    var searchQuery by mutableStateOf("")
    var selectedCategory by mutableStateOf("Semua")
    var selectedDateFilter by mutableStateOf("Kapan Saja")
    var isRefreshing by mutableStateOf(value = false)

    val categories: List<String>
        get() = listOf("Semua") + communityRepository.communities.asSequence().map { it.category }.distinct().toList()

    val recommendedCommunities
        get() = communityRepository.getRecommendedCommunities()

    val recommendedEvents
        get() = communityRepository.getRecommendedEvents { DateFormatter.isUpcoming(it) }

    val filteredEvents: List<Pair<com.example.communityeventmanagement.data.model.Event, Int>>
        get() {
            val allEvents = communityRepository.communities.flatMap { community ->
                community.events.map { it to community.id }
            }
            return allEvents.filter { (event, _) ->
                val matchesQuery = event.title.contains(searchQuery, ignoreCase = true) || 
                                   event.description.contains(searchQuery, ignoreCase = true)
                val matchesCategory = (selectedCategory == "Semua") || (event.category == selectedCategory)
                val matchesDate = when (selectedDateFilter) {
                    "Hari Ini" -> {
                        val todayStr = SimpleDateFormat("d M yyyy", Locale.getDefault()).format(Date())
                        event.date.trim() == todayStr
                    }
                    "Minggu Ini" -> {
                        val cal = Calendar.getInstance()
                        val currentWeek = cal[Calendar.WEEK_OF_YEAR]
                        val currentYear = cal[Calendar.YEAR]
                        try {
                            val parts = event.date.trim().split(" ")
                            val eventCal = Calendar.getInstance().apply {
                                set(parts[2].toInt(), parts[1].toInt() - 1, parts[0].toInt())
                            }
                            (eventCal[Calendar.WEEK_OF_YEAR] == currentWeek) && (eventCal[Calendar.YEAR] == currentYear)
                        } catch (_: Exception) { false }
                    }
                    "Bulan Ini" -> {
                        val cal = Calendar.getInstance()
                        val currentMonth = cal[Calendar.MONTH] + 1
                        val currentYear = cal[Calendar.YEAR]
                        try {
                            val parts = event.date.trim().split(" ")
                            (parts[1].toInt() == currentMonth) && (parts[2].toInt() == currentYear)
                        } catch (_: Exception) { false }
                    }
                    else -> true
                }
                matchesQuery && matchesCategory && matchesDate
            }
        }

    fun isEventRegistered(eventId: Int): Boolean {
        return communityRepository.registeredEventIds.contains(eventId)
    }

    fun refresh() {
        isRefreshing = true
        communityRepository.loadCommunities()
        userRepository.loadUsers()
        userRepository.currentUser?.let { communityRepository.refreshUserParticipation(it) }
        isRefreshing = false
    }
}

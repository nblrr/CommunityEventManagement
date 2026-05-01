package com.example.communityeventmanagement.ui.screens.home

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.example.communityeventmanagement.data.repository.AppState
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class HomeViewModel : ViewModel() {
    var searchQuery by mutableStateOf("")
    var selectedCategory by mutableStateOf("Semua")
    var selectedDateFilter by mutableStateOf("Kapan Saja")
    var isRefreshing by mutableStateOf(false)

    val categories: List<String>
        get() = listOf("Semua") + AppState.communities.map { it.category }.distinct()

    val recommendedCommunities
        get() = AppState.getRecommendedCommunities()

    val recommendedEvents
        get() = AppState.getRecommendedEvents()

    val filteredEvents: List<Pair<com.example.communityeventmanagement.data.model.Event, Int>>
        get() {
            val allEvents = AppState.communities.flatMap { community ->
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
                        val currentWeek = cal.get(Calendar.WEEK_OF_YEAR)
                        val currentYear = cal.get(Calendar.YEAR)
                        try {
                            val parts = event.date.trim().split(" ")
                            val eventCal = Calendar.getInstance().apply {
                                set(parts[2].toInt(), parts[1].toInt() - 1, parts[0].toInt())
                            }
                            eventCal.get(Calendar.WEEK_OF_YEAR) == currentWeek && eventCal.get(Calendar.YEAR) == currentYear
                        } catch (_: Exception) { false }
                    }
                    "Bulan Ini" -> {
                        val cal = Calendar.getInstance()
                        val currentMonth = cal.get(Calendar.MONTH) + 1
                        val currentYear = cal.get(Calendar.YEAR)
                        try {
                            val parts = event.date.trim().split(" ")
                            parts[1].toInt() == currentMonth && parts[2].toInt() == currentYear
                        } catch (_: Exception) { false }
                    }
                    else -> true
                }
                matchesQuery && matchesCategory && matchesDate
            }
        }

    fun refresh() {
        // Simulasi refresh
        isRefreshing = true
        // AppState.initialize(context) - idealnya panggil reload data
        isRefreshing = false
    }
}

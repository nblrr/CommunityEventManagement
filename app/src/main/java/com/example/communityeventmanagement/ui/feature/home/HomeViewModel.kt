package com.example.communityeventmanagement.ui.feature.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.communityeventmanagement.R
import com.example.communityeventmanagement.domain.entities.AppCategories
import com.example.communityeventmanagement.domain.entities.Category
import com.example.communityeventmanagement.domain.entities.Community
import com.example.communityeventmanagement.domain.entities.Event
import com.example.communityeventmanagement.domain.entities.User
import com.example.communityeventmanagement.domain.usecase.GetCommunities
import com.example.communityeventmanagement.domain.usecase.GetCurrentUser
import com.example.communityeventmanagement.domain.usecase.GetRecommendedCommunities
import com.example.communityeventmanagement.domain.usecase.GetRecommendedEvents
import com.example.communityeventmanagement.domain.usecase.GetRegisteredEventIds
import com.example.communityeventmanagement.domain.usecase.RefreshData
import com.example.communityeventmanagement.util.DateUtils
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

data class HomeUiState(
    val currentUser: User? = null,
    val recommendedCommunities: List<Community> = emptyList(),
    val recommendedEvents: List<Event> = emptyList(),
    val filteredEvents: List<Event> = emptyList(),
    val registeredEventIds: Set<Int> = emptySet(),
    val categories: List<Category> = emptyList(),
    val searchQuery: String = "",
    val selectedCategory: String = CATEGORY_ALL,
    val selectedDateFilter: Int = R.string.time_any,
    val isRefreshing: Boolean = false,
    val isLoading: Boolean = false
) {
    companion object {
        const val CATEGORY_ALL = com.example.communityeventmanagement.domain.entities.CATEGORY_ALL
    }
}

@HiltViewModel
class HomeViewModel @Inject constructor(
    getCurrentUser: GetCurrentUser,
    getCommunities: GetCommunities,
    private val getRecommendedCommunities: GetRecommendedCommunities,
    private val getRecommendedEvents: GetRecommendedEvents,
    private val getRegisteredEventIds: GetRegisteredEventIds,
    private val refreshData: RefreshData
) : ViewModel() {

    private val _searchQuery = MutableStateFlow("")
    private val _selectedCategory = MutableStateFlow(HomeUiState.CATEGORY_ALL)
    private val _selectedDateFilter = MutableStateFlow(R.string.time_any)
    private val _isRefreshing = MutableStateFlow(false)
    private val _isLoading = MutableStateFlow(false)

    val uiState: StateFlow<HomeUiState> = combine(
        getCurrentUser(),
        getCommunities(),
        getRegisteredEventIds(),
        _searchQuery,
        _selectedCategory,
        _selectedDateFilter,
        _isRefreshing,
        _isLoading
    ) { args: Array<Any?> ->
        val user = args[0] as User?
        @Suppress("UNCHECKED_CAST")
        val communities = args[1] as List<Community>
        @Suppress("UNCHECKED_CAST")
        val registeredIds = args[2] as Set<Int>
        val query = args[3] as String
        val category = args[4] as String
        val dateFilter = args[5] as Int
        val refreshing = args[6] as Boolean
        val loading = args[7] as Boolean

        val recommendedCommunities = getRecommendedCommunities()
        val recommendedEvents = getRecommendedEvents { DateUtils.isUpcoming(it) }
        
        val allEvents = communities.flatMap { it.events }
        val filteredEvents = allEvents.filter { event ->
            val matchesSearch = event.title.contains(query, ignoreCase = true) ||
                    event.description.contains(query, ignoreCase = true)
            val matchesCategory = category == HomeUiState.CATEGORY_ALL || event.category.equals(category, ignoreCase = true)
            val matchesDate = when (dateFilter) {
                R.string.time_today -> DateUtils.isToday(event.date)
                R.string.time_this_week -> DateUtils.isThisWeek(event.date)
                R.string.time_this_month -> DateUtils.isThisMonth(event.date)
                else -> true
            }
            matchesSearch && matchesCategory && matchesDate
        }

        HomeUiState(
            currentUser = user,
            recommendedCommunities = recommendedCommunities,
            recommendedEvents = recommendedEvents,
            filteredEvents = filteredEvents,
            registeredEventIds = registeredIds,
            categories = listOf(Category(HomeUiState.CATEGORY_ALL, R.string.category_all)) + AppCategories,
            searchQuery = query,
            selectedCategory = category,
            selectedDateFilter = dateFilter,
            isRefreshing = refreshing,
            isLoading = loading
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = HomeUiState(isLoading = true)
    )

    fun onSearchQueryChange(query: String) {
        _searchQuery.value = query
    }

    fun onCategoryChange(category: String) {
        _selectedCategory.value = category
    }

    fun onDateFilterChange(filter: Int) {
        _selectedDateFilter.value = filter
    }

    fun refresh() {
        viewModelScope.launch {
            _isRefreshing.value = true
            refreshData(uiState.value.currentUser)
            _isRefreshing.value = false
        }
    }
}

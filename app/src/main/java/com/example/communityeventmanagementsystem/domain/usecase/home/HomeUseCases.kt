package com.example.communityeventmanagementsystem.domain.usecase.home

import com.example.communityeventmanagementsystem.core.common.NetworkResult
import com.example.communityeventmanagementsystem.domain.model.Category
import com.example.communityeventmanagementsystem.domain.model.Community
import com.example.communityeventmanagementsystem.domain.model.Event
import com.example.communityeventmanagementsystem.domain.repository.HomeRepository
import javax.inject.Inject

class GetCategoriesUseCase @Inject constructor(private val repository: HomeRepository) {
    suspend operator fun invoke(): NetworkResult<List<Category>> = repository.getCategories()
}

class GetUpcomingEventsUseCase @Inject constructor(private val repository: HomeRepository) {
    suspend operator fun invoke(): NetworkResult<List<Event>> = repository.getUpcomingEvents()
}

class GetRecommendedEventsUseCase @Inject constructor(private val repository: HomeRepository) {
    suspend operator fun invoke(): NetworkResult<List<Event>> = repository.getRecommendedEvents()
}

class GetMyCommunitiesUseCase @Inject constructor(private val repository: HomeRepository) {
    suspend operator fun invoke(): NetworkResult<List<Community>> = repository.getMyCommunities()
}

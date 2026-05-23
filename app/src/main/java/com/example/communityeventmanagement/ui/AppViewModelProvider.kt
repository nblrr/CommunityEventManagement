package com.example.communityeventmanagement.ui

import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.communityeventmanagement.CommunityApplication
import com.example.communityeventmanagement.ui.feature.admin.AdminPanelViewModel
import com.example.communityeventmanagement.ui.feature.auth.AuthViewModel
import com.example.communityeventmanagement.ui.feature.community.CommunityDetailViewModel
import com.example.communityeventmanagement.ui.feature.community.CommunityListViewModel
import com.example.communityeventmanagement.ui.feature.community.CreateCommunityViewModel
import com.example.communityeventmanagement.ui.feature.event.CreateEventViewModel
import com.example.communityeventmanagement.ui.feature.event.EventDetailViewModel
import com.example.communityeventmanagement.ui.feature.forum.ForumViewModel
import com.example.communityeventmanagement.ui.feature.home.HomeViewModel
import com.example.communityeventmanagement.ui.feature.organizer.OrganizerRegisterViewModel
import com.example.communityeventmanagement.ui.feature.profile.ProfileViewModel

object AppViewModelProvider {
    val Factory = viewModelFactory {
        initializer {
            val container = communityApplication().container
            HomeViewModel(
                getCurrentUser = container.getCurrentUser,
                getCommunities = container.getCommunities,
                getRecommendedCommunities = container.getRecommendedCommunities,
                getRecommendedEvents = container.getRecommendedEvents,
                getRegisteredEventIds = container.getRegisteredEventIds,
                refreshData = container.refreshData
            )
        }
        initializer {
            val container = communityApplication().container
            AuthViewModel(
                loginUseCase = container.login,
                registerUseCase = container.register
            )
        }
        initializer {
            val container = communityApplication().container
            ProfileViewModel(
                getCurrentUser = container.getCurrentUser,
                getCommunities = container.getCommunities,
                updateAvatar = container.updateAvatar,
                updateProfile = container.updateProfile,
                submitTrustedApplication = container.submitTrustedApplication,
                saveTheme = container.saveTheme,
                logout = container.logout,
                themeModeFlow = container.userRepository.themeMode
            )
        }
        initializer {
            val container = communityApplication().container
            AdminPanelViewModel(
                getUsers = container.getUsers,
                getPendingApplications = container.getPendingApplications,
                approveApplication = container.approveApplication,
                rejectApplication = container.rejectApplication,
                toggleUserBlock = container.toggleUserBlock
            )
        }
        initializer {
            val container = communityApplication().container
            CommunityListViewModel(
                getCommunities = container.getCommunities,
                getJoinedCommunityIds = container.getJoinedCommunityIds
            )
        }
        initializer {
            val container = communityApplication().container
            CommunityDetailViewModel(
                getCurrentUser = container.getCurrentUser,
                getCommunityDetail = container.getCommunityDetail,
                joinCommunity = container.joinCommunity,
                getJoinedCommunityIds = container.getJoinedCommunityIds,
                getRegisteredEventIds = container.getRegisteredEventIds,
                deleteCommunity = container.deleteCommunity
            )
        }
        initializer {
            val container = communityApplication().container
            CreateCommunityViewModel(
                getCurrentUser = container.getCurrentUser,
                getCommunityDetail = container.getCommunityDetail,
                createCommunity = container.createCommunity,
                updateCommunity = container.updateCommunity,
                refreshData = container.refreshData
            )
        }
        initializer {
            val container = communityApplication().container
            CreateEventViewModel(
                getCommunities = container.getCommunities,
                getEventDetail = container.getEventDetail,
                createEvent = container.createEvent,
                updateEvent = container.updateEvent
            )
        }
        initializer {
            val container = communityApplication().container
            EventDetailViewModel(
                getEventDetail = container.getEventDetail,
                getCommunityDetailUseCase = container.getCommunityDetail,
                getCurrentUser = container.getCurrentUser,
                joinEvent = container.joinEvent,
                cancelEvent = container.cancelEvent,
                getRegisteredEventIds = container.getRegisteredEventIds,
                deleteEvent = container.deleteEvent,
                addEventRating = container.addEventRating
            )
        }
        initializer {
            val container = communityApplication().container
            ForumViewModel(
                getCurrentUser = container.getCurrentUser,
                getCommunityDetail = container.getCommunityDetail,
                getForumMessages = container.getForumMessages,
                sendMessage = container.sendMessage
            )
        }
        initializer {
            val container = communityApplication().container
            OrganizerRegisterViewModel(
                getCurrentUser = container.getCurrentUser,
                registerOrganizer = container.registerOrganizer
            )
        }
    }
}

fun CreationExtras.communityApplication(): CommunityApplication =
    (this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as CommunityApplication)

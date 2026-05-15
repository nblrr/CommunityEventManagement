package com.example.communityeventmanagement.ui

import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.communityeventmanagement.CommunityApplication
import com.example.communityeventmanagement.features.admin.AdminPanelViewModel
import com.example.communityeventmanagement.features.auth.AuthViewModel
import com.example.communityeventmanagement.features.community.CommunityDetailViewModel
import com.example.communityeventmanagement.features.community.CommunityListViewModel
import com.example.communityeventmanagement.features.community.CreateCommunityViewModel
import com.example.communityeventmanagement.features.event.CreateEventViewModel
import com.example.communityeventmanagement.features.event.EventDetailViewModel
import com.example.communityeventmanagement.features.forum.ForumViewModel
import com.example.communityeventmanagement.features.home.HomeViewModel
import com.example.communityeventmanagement.features.organizer.OrganizerRegisterViewModel
import com.example.communityeventmanagement.features.profile.ProfileViewModel

object AppViewModelProvider {
    val Factory = viewModelFactory {
        initializer {
            HomeViewModel(
                userRepository = communityApplication().container.userRepository,
                communityRepository = communityApplication().container.communityRepository,
            )
        }
        initializer {
            AuthViewModel(
                userRepository = communityApplication().container.userRepository,
                communityRepository = communityApplication().container.communityRepository
            )
        }
        initializer {
            ProfileViewModel(
                userRepository = communityApplication().container.userRepository,
                communityRepository = communityApplication().container.communityRepository
            )
        }
        initializer {
            AdminPanelViewModel(
                userRepository = communityApplication().container.userRepository
            )
        }
        initializer {
            CommunityListViewModel(
                communityRepository = communityApplication().container.communityRepository
            )
        }
        initializer {
            CommunityDetailViewModel(
                userRepository = communityApplication().container.userRepository,
                communityRepository = communityApplication().container.communityRepository
            )
        }
        initializer {
            CreateCommunityViewModel(
                userRepository = communityApplication().container.userRepository,
                communityRepository = communityApplication().container.communityRepository
            )
        }
        initializer {
            CreateEventViewModel(
                communityRepository = communityApplication().container.communityRepository
            )
        }
        initializer {
            EventDetailViewModel(
                communityRepository = communityApplication().container.communityRepository,
            )
        }
        initializer {
            ForumViewModel(
                userRepository = communityApplication().container.userRepository,
                communityRepository = communityApplication().container.communityRepository
            )
        }
        initializer {
            OrganizerRegisterViewModel(
                userRepository = communityApplication().container.userRepository
            )
        }
    }
}

fun CreationExtras.communityApplication(): CommunityApplication =
    (this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as CommunityApplication)

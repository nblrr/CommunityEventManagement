package com.example.communityeventmanagement.data.repository

import android.content.Context
import androidx.compose.runtime.mutableStateListOf
import com.example.communityeventmanagement.data.model.*
import com.example.communityeventmanagement.data.local.JsonStorage
import java.text.SimpleDateFormat
import java.util.*

object AppState {
    private var storage: JsonStorage? = null
    private lateinit var userRepository: UserRepository
    private lateinit var communityRepository: CommunityRepository
    private val eventRepository = EventRepository()

    val joinedCommunityIds = mutableStateListOf<Int>()
    val registeredEventIds = mutableStateListOf<Int>()

    var currentUser: UserProfile?
        get() = userRepository.currentUser
        set(value) { userRepository.currentUser = value }
    val allUsers get() = userRepository.allUsers
    val communities get() = communityRepository.communities
    val trustedApplications get() = userRepository.trustedApplications

    // Inisialisasi data
    fun initialize(context: Context) {
        if (storage == null) {
            storage = JsonStorage(context.applicationContext)
            userRepository = UserRepository(storage)
            communityRepository = CommunityRepository(storage)
            userRepository.loadUsers()
            communityRepository.loadCommunities()
            userRepository.loadTrustedApplications()
            val savedUserId = userRepository.loadSession()
            if (savedUserId != null) {
                val user = allUsers.find { it.id == savedUserId }
                if (user != null && !user.isBlocked) {
                    userRepository.currentUser = user
                    refreshUserParticipation()
                } else if (user?.isBlocked == true) {
                    userRepository.saveSession(null)
                }
            }
            userRepository.ensureAdminExists()
        }
    }

    // Login user
    fun loginWithCredentials(email: String, password: String): LoginResult {
        val user = allUsers.find { it.email.equals(email.trim(), ignoreCase = true) && it.password == password }
            ?: return LoginResult.Error("Email atau password salah.")
        if (user.isBlocked) return LoginResult.Error("Akun ini telah diblokir oleh admin.")
        login(user)
        return LoginResult.Success(user)
    }

    // Gabung komunitas
    fun toggleCommunityJoin(communityId: Int) {
        val userId = currentUser?.id ?: return
        val index = communities.indexOfFirst { it.id == communityId }
        if (index != -1) {
            val community = communities[index]
            val isJoined = community.memberIds.contains(userId)
            val newMemberIds = if (isJoined) {
                joinedCommunityIds.remove(communityId)
                community.memberIds - userId
            } else {
                joinedCommunityIds.add(communityId)
                community.memberIds + userId
            }
            communities[index] = community.copy(memberIds = newMemberIds)
            communityRepository.saveCommunityData()
        }
    }

    // Daftar event
    fun toggleEventRegistration(communityId: Int, eventId: Int) {
        val userId = currentUser?.id ?: return
        val commIndex = communities.indexOfFirst { it.id == communityId }
        if (commIndex != -1) {
            val community = communities[commIndex]
            val eventIndex = community.events.indexOfFirst { it.id == eventId }
            if (eventIndex != -1) {
                val event = community.events[eventIndex]
                val isRegistered = event.registeredUserIds.contains(userId)
                val newRegisteredIds = if (isRegistered) {
                    registeredEventIds.remove(eventId)
                    event.registeredUserIds - userId
                } else {
                    registeredEventIds.add(eventId)
                    event.registeredUserIds + userId
                }
                val updatedEvents = community.events.toMutableList()
                updatedEvents[eventIndex] = event.copy(registeredUserIds = newRegisteredIds)
                var newMemberIds = community.memberIds
                if (!isRegistered && !community.memberIds.contains(userId)) {
                    newMemberIds = community.memberIds + userId
                    if (!joinedCommunityIds.contains(communityId)) joinedCommunityIds.add(communityId)
                }
                communities[commIndex] = community.copy(events = updatedEvents, memberIds = newMemberIds)
                communityRepository.saveCommunityData()
            }
        }
    }

    // Simpan data
    fun saveUserData() = userRepository.saveUserData()
    fun login(user: UserProfile) { userRepository.login(user); refreshUserParticipation() }
    fun logout() { userRepository.logout(); joinedCommunityIds.clear(); registeredEventIds.clear() }
    fun saveCommunityData() = communityRepository.saveCommunityData()
    fun saveForumData(communityId: Int) = communityRepository.saveForumData(communityId)

    // Refresh status partisipasi
    fun refreshUserParticipation() {
        joinedCommunityIds.clear(); registeredEventIds.clear()
        val userId = currentUser?.id ?: return
        communities.forEach { community ->
            if (community.organizerId == userId || community.memberIds.contains(userId)) joinedCommunityIds.add(community.id)
            community.events.forEach { event ->
                if (event.registeredUserIds.contains(userId)) {
                    registeredEventIds.add(event.id)
                    if (!joinedCommunityIds.contains(community.id)) joinedCommunityIds.add(community.id)
                }
            }
        }
    }

    // Ajukan trusted organizer
    fun submitTrustedApplication(reason: String, experience: String) {
        val user = currentUser ?: return
        if (trustedApplications.any { it.userId == user.id }) return
        val communityName = communities.find { it.organizerId == user.id }?.name ?: "Unknown"
        val application = TrustedApplication(user.id, user.name, communityName, reason, experience)
        trustedApplications.add(application)
        userRepository.saveTrustedApplications()
        val userIndex = allUsers.indexOfFirst { it.id == user.id }
        if (userIndex != -1) {
            allUsers[userIndex] = allUsers[userIndex].copy(trustedAppStatus = "PENDING")
            userRepository.currentUser = allUsers[userIndex]
            userRepository.saveUserData()
        }
    }

    // Proses pengajuan trusted
    fun handleTrustedApplication(userId: String, approve: Boolean) {
        val appIndex = trustedApplications.indexOfFirst { it.userId == userId }
        if (appIndex != -1) {
            trustedApplications.removeAt(appIndex); userRepository.saveTrustedApplications()
            val userIndex = allUsers.indexOfFirst { it.id == userId }
            if (userIndex != -1) {
                val status = if (approve) "APPROVED" else "REJECTED"
                allUsers[userIndex] = allUsers[userIndex].copy(trustedAppStatus = status, isTrusted = approve)
                if (currentUser?.id == userId) userRepository.currentUser = allUsers[userIndex]
                userRepository.saveUserData()
            }
        }
    }

    // Rating event
    fun addEventRating(communityId: Int, eventId: Int, score: Int, comment: String) {
        val user = currentUser ?: return
        val communityIndex = communities.indexOfFirst { it.id == communityId }
        if (communityIndex != -1) {
            val community = communities[communityIndex]
            val eventIndex = community.events.indexOfFirst { it.id == eventId }
            if (eventIndex != -1) {
                val event = community.events[eventIndex]
                if (event.ratings?.any { it.userId == user.id } == true) return
                val newRating = Rating(user.id, user.name, score.coerceIn(1, 5), comment.trim(), SimpleDateFormat("d M yyyy", Locale.getDefault()).format(Date()))
                val updatedEvents = community.events.toMutableList()
                updatedEvents[eventIndex] = event.copy(ratings = (event.ratings ?: emptyList()) + newRating)
                communities[communityIndex] = community.copy(events = updatedEvents)
                communityRepository.saveCommunityData()
            }
        }
    }

    // Galeri event
    fun addGalleryImage(communityId: Int, eventId: Int, imageUri: String) {
        val userId = currentUser?.id ?: return
        val communityIndex = communities.indexOfFirst { it.id == communityId }
        if (communityIndex != -1) {
            val community = communities[communityIndex]
            val isOwner = community.organizerId == userId || currentUser?.role == "Admin"
            if (!isOwner) return
            val eventIndex = community.events.indexOfFirst { it.id == eventId }
            if (eventIndex != -1) {
                val event = community.events[eventIndex]
                val updatedEvents = community.events.toMutableList()
                updatedEvents[eventIndex] = event.copy(galleryImages = (event.galleryImages ?: emptyList()) + imageUri)
                communities[communityIndex] = community.copy(events = updatedEvents)
                communityRepository.saveCommunityData()
            }
        }
    }

    // Rekomendasi
    fun getRecommendedCommunities(): List<Community> {
        if (joinedCommunityIds.isEmpty() && registeredEventIds.isEmpty()) return communities.sortedByDescending { it.memberCount }.take(10)
        val userCategories = mutableSetOf<String>()
        communities.filter { it.id in joinedCommunityIds }.forEach { userCategories.add(it.category) }
        return communities.filter { it.id !in joinedCommunityIds }.sortedByDescending { if (it.category in userCategories) 10 else 0 }.take(10)
    }

    fun getRecommendedEvents(): List<Event> {
        val allEvents = communities.flatMap { it.events }.filter { eventRepository.isUpcoming(it.date) }
        if (joinedCommunityIds.isEmpty() && registeredEventIds.isEmpty()) return allEvents.sortedByDescending { it.attendeeCount }.take(10)
        val userCategories = mutableSetOf<String>()
        communities.filter { it.id in joinedCommunityIds }.forEach { userCategories.add(it.category) }
        return allEvents.filter { it.id !in registeredEventIds }.sortedByDescending { if (it.category in userCategories) 10 else 0 }.take(10)
    }

    fun isUpcoming(dateStr: String): Boolean = eventRepository.isUpcoming(dateStr)
}

sealed class LoginResult {
    data class Success(val user: UserProfile) : LoginResult()
    data class Error(val message: String) : LoginResult()
}

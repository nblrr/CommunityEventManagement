package com.example.communityeventmanagement.data.repository

import android.content.Context
import androidx.compose.runtime.mutableStateListOf
import com.example.communityeventmanagement.data.model.*
import com.example.communityeventmanagement.data.local.JsonStorage
import java.text.SimpleDateFormat
import java.util.*

object AppState {
    var currentUser: UserProfile? = null
    val joinedCommunityIds = mutableStateListOf<Int>()
    val registeredEventIds  = mutableStateListOf<Int>()
    
    var allUsers = mutableListOf<UserProfile>()
    val communities = mutableStateListOf<Community>()
    val trustedApplications = mutableStateListOf<TrustedApplication>()
    
    private var storage: JsonStorage? = null

    fun initialize(context: Context) {
        if (storage == null) {
            storage = JsonStorage(context.applicationContext)
            
            // 1. Load Users
            allUsers = storage!!.loadUsers().map { user ->
                UserProfile(
                    id = (user.id as String?) ?: "",
                    name = (user.name as String?) ?: "",
                    email = (user.email as String?) ?: "",
                    password = (user.password as String?) ?: "",
                    avatarUri = user.avatarUri,
                    role = (user.role as String?) ?: "User",
                    isBlocked = user.isBlocked,
                    isTrusted = user.isTrusted,
                    trustedAppStatus = (user.trustedAppStatus as String?) ?: "NONE",
                    organizerProfile = user.organizerProfile
                )
            }.toMutableList()

            // 2. Load Communities
            val loadedCommunities = storage!!.loadCommunities().map { comm ->
                Community(
                    id = comm.id,
                    name = (comm.name as String?) ?: "",
                    description = (comm.description as String?) ?: "",
                    category = (comm.category as String?) ?: "",
                    coverImageUri = comm.coverImageUri,
                    organizerId = (comm.organizerId as String?) ?: "",
                    organizerName = (comm.organizerName as String?) ?: "",
                    memberIds = (comm.memberIds as List<String>?) ?: emptyList(),
                    events = (comm.events as List<Event>?)?.map { event ->
                        Event(
                            id = event.id,
                            title = (event.title as String?) ?: "",
                            description = (event.description as String?) ?: "",
                            date = (event.date as String?) ?: "",
                            time = (event.time as String?) ?: "",
                            location = (event.location as String?) ?: "",
                            category = (event.category as String?) ?: "",
                            maxAttendees = event.maxAttendees,
                            coverImageUri = event.coverImageUri,
                            communityId = event.communityId,
                            registeredUserIds = (event.registeredUserIds as List<String>?) ?: emptyList(),
                            galleryImages = event.galleryImages ?: emptyList(),
                            ratings = (event.ratings as List<Rating>?)?.map { r ->
                                Rating(
                                    userId = (r.userId as String?) ?: "",
                                    userName = (r.userName as String?) ?: "",
                                    score = r.score,
                                    comment = (r.comment as String?) ?: "",
                                    date = (r.date as String?) ?: ""
                                )
                            } ?: emptyList()
                        )
                    } ?: emptyList(),
                    forumMessages = (comm.forumMessages as List<ForumMessage>?) ?: emptyList()
                )
            }
            communities.clear()
            communities.addAll(loadedCommunities)
            
            // 3. Load Forum Messages for each community
            communities.forEachIndexed { index, community ->
                val messages = storage!!.loadForumMessages(community.id).map { msg ->
                    ForumMessage(
                        sender = (msg.sender as String?) ?: "Unknown",
                        message = (msg.message as String?) ?: "",
                        time = (msg.time as String?) ?: "",
                        avatarInitials = (msg.avatarInitials as String?) ?: ""
                    )
                }
                if (messages.isNotEmpty()) {
                    communities[index] = community.copy(forumMessages = messages)
                }
            }

            // 4. Persistence: Restore Session
            val savedUserId = storage!!.loadSession()
            if (savedUserId != null) {
                currentUser = allUsers.find { it.id == savedUserId }
                refreshUserParticipation()
            }

            // 5. Seed 100 Users if not already present and distribute them
            seed100Users()
        }
    }

    fun seed100Users() {
        // If we don't have 100 seeded users, create them
        val existingSeededCount = allUsers.count { it.id.startsWith("seeded_") }
        if (existingSeededCount < 100) {
            val usersToCreate = 100 - existingSeededCount
            val newUsers = (1..usersToCreate).map { i ->
                val idNum = existingSeededCount + i
                UserProfile(
                    id = "seeded_user_$idNum",
                    name = "User Simpul $idNum",
                    email = "user$idNum@community.com",
                    password = "password$idNum",
                    role = "User",
                    avatarUri = "https://i.pravatar.cc/150?u=seeded_user_$idNum"
                )
            }
            allUsers.addAll(newUsers)
            saveUserData()
        }

        // Distribute users to communities and events
        val allAvailableUsers = allUsers.filter { it.role == "User" }
        
        communities.forEachIndexed { index, community ->
            // Each community gets a random subset of users
            val maxCommMembers = allAvailableUsers.size
            val commMemberCount = if (maxCommMembers > 0) {
                (5.coerceAtMost(maxCommMembers)..30.coerceAtMost(maxCommMembers)).random()
            } else 0
            
            val communityMembers = allAvailableUsers.shuffled().take(commMemberCount)
            val memberIds = communityMembers.map { it.id }.toSet().toList()

            // Distribute these members to the community's events
            val updatedEvents = community.events.map { event ->
                val maxEventParticipants = memberIds.size
                val eventParticipantCount = if (maxEventParticipants > 0) {
                    (3.coerceAtMost(maxEventParticipants)..20.coerceAtMost(maxEventParticipants)).random()
                } else 0
                
                val eventParticipants = memberIds.shuffled().take(eventParticipantCount)
                event.copy(registeredUserIds = eventParticipants)
            }

            communities[index] = community.copy(
                memberIds = memberIds,
                events = updatedEvents
            )
        }
        saveCommunityData()
    }

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
            saveCommunityData()
        }
    }

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
                
                // Automatically join community if registering for an event
                var newMemberIds = community.memberIds
                if (!isRegistered && !community.memberIds.contains(userId)) {
                    newMemberIds = community.memberIds + userId
                    joinedCommunityIds.add(communityId)
                }
                
                communities[commIndex] = community.copy(
                    events = updatedEvents,
                    memberIds = newMemberIds
                )
                saveCommunityData()
            }
        }
    }

    fun saveUserData() {
        storage?.saveUsers(allUsers)
    }

    fun login(user: UserProfile) {
        currentUser = user
        storage?.saveSession(user.id)
        refreshUserParticipation()
    }

    fun logout() {
        currentUser = null
        joinedCommunityIds.clear()
        registeredEventIds.clear()
        storage?.saveSession(null)
    }

    fun saveCommunityData() {
        storage?.saveCommunities(communities.toList())
    }

    fun saveForumData(communityId: Int) {
        val community = communities.find { it.id == communityId }
        community?.let {
            storage?.saveForumMessages(communityId, it.forumMessages)
        }
    }

    fun refreshUserParticipation() {
        joinedCommunityIds.clear()
        registeredEventIds.clear()
        
        val userId = currentUser?.id ?: return
        
        communities.forEach { community ->
            if (community.organizerId == userId || community.memberIds.contains(userId)) {
                joinedCommunityIds.add(community.id)
            }
            
            community.events.forEach { event ->
                if (event.registeredUserIds.contains(userId)) {
                    registeredEventIds.add(event.id)
                    if (!joinedCommunityIds.contains(community.id)) {
                        joinedCommunityIds.add(community.id)
                    }
                }
            }
        }
    }

    // --- Trusted Organizer Features ---
    fun submitTrustedApplication(reason: String, experience: String) {
        val user = currentUser ?: return
        val commName = communities.find { it.organizerId == user.id }?.name ?: "Unknown"
        
        val app = TrustedApplication(
            userId = user.id,
            userName = user.name,
            communityName = commName,
            reason = reason,
            experience = experience
        )
        trustedApplications.add(app)
        
        // Update user status
        val userIndex = allUsers.indexOfFirst { it.id == user.id }
        if (userIndex != -1) {
            allUsers[userIndex] = allUsers[userIndex].copy(trustedAppStatus = "PENDING")
            currentUser = allUsers[userIndex]
            saveUserData()
        }
    }

    fun handleTrustedApplication(userId: String, approve: Boolean) {
        val appIndex = trustedApplications.indexOfFirst { it.userId == userId }
        if (appIndex != -1) {
            val status = if (approve) "APPROVED" else "REJECTED"
            trustedApplications[appIndex] = trustedApplications[appIndex].copy(status = status)
            
            val userIndex = allUsers.indexOfFirst { it.id == userId }
            if (userIndex != -1) {
                allUsers[userIndex] = allUsers[userIndex].copy(
                    trustedAppStatus = status,
                    isTrusted = approve
                )
                if (currentUser?.id == userId) currentUser = allUsers[userIndex]
                saveUserData()
            }
            
            // Remove from list after processing (or keep as history)
            trustedApplications.removeAt(appIndex)
        }
    }

    // --- Review & Rating Features ---
    fun addEventRating(communityId: Int, eventId: Int, score: Int, comment: String) {
        val user = currentUser ?: return
        val communityIndex = communities.indexOfFirst { it.id == communityId }
        if (communityIndex != -1) {
            val community = communities[communityIndex]
            val eventIndex = community.events.indexOfFirst { it.id == eventId }
            if (eventIndex != -1) {
                val event = community.events[eventIndex]
                val newRating = Rating(
                    userId = user.id,
                    userName = user.name,
                    score = score,
                    comment = comment,
                    date = SimpleDateFormat("d M 2025", Locale.getDefault()).format(Date())
                )
                val updatedRatings = (event.ratings ?: emptyList()) + newRating
                
                val updatedEvents = community.events.toMutableList()
                updatedEvents[eventIndex] = event.copy(ratings = updatedRatings)
                communities[communityIndex] = community.copy(events = updatedEvents)
                saveCommunityData()
            }
        }
    }

    fun addGalleryImage(communityId: Int, eventId: Int, imageUri: String) {
        val communityIndex = communities.indexOfFirst { it.id == communityId }
        if (communityIndex != -1) {
            val community = communities[communityIndex]
            val eventIndex = community.events.indexOfFirst { it.id == eventId }
            if (eventIndex != -1) {
                val event = community.events[eventIndex]
                val updatedImages = (event.galleryImages ?: emptyList()) + imageUri
                val updatedEvents = community.events.toMutableList()
                updatedEvents[eventIndex] = event.copy(galleryImages = updatedImages)
                communities[communityIndex] = community.copy(events = updatedEvents)
                saveCommunityData()
            }
        }
    }

    fun getRecommendedCommunities(): List<Community> {
        if (joinedCommunityIds.isEmpty() && registeredEventIds.isEmpty()) {
            return communities.sortedByDescending { it.memberCount }.take(10)
        }

        val userCategories = mutableSetOf<String>()
        communities.filter { it.id in joinedCommunityIds }.forEach { userCategories.add(it.category) }
        communities.flatMap { it.events }.filter { it.id in registeredEventIds }.forEach { userCategories.add(it.category) }

        return communities
            .filter { it.id !in joinedCommunityIds }
            .sortedByDescending { 
                var score = if (it.category in userCategories) 10 else 0
                score += (it.memberCount / 10)
                score
            }
            .take(10)
    }

    fun getRecommendedEvents(): List<Event> {
        val allEvents = communities.flatMap { it.events }.filter { isUpcoming(it.date) }
        
        if (joinedCommunityIds.isEmpty() && registeredEventIds.isEmpty()) {
            return allEvents.sortedByDescending { it.attendeeCount }.take(10)
        }

        val userCategories = mutableSetOf<String>()
        communities.filter { it.id in joinedCommunityIds }.forEach { userCategories.add(it.category) }
        allEvents.filter { it.id in registeredEventIds }.forEach { userCategories.add(it.category) }

        return allEvents
            .filter { it.id !in registeredEventIds }
            .sortedByDescending { 
                var score = if (it.category in userCategories) 10 else 0
                score += (it.attendeeCount / 50) 
                score
            }
            .take(10)
    }

    fun isUpcoming(dateStr: String): Boolean {
        return try {
            val parts = dateStr.trim().split(" ")
            if (parts.size == 3) {
                val day = parts[0].toIntOrNull() ?: 1
                val month = parts[1].toIntOrNull() ?: 1
                val year = parts[2].toIntOrNull() ?: 0

                val eventCal = Calendar.getInstance().apply {
                    set(year, month - 1, day, 23, 59, 59)
                }
                val now = Calendar.getInstance()
                
                eventCal.after(now)
            } else {
                val currentYear = Calendar.getInstance().get(Calendar.YEAR)
                val yearInStr = dateStr.filter { it.isDigit() }.takeLast(4).toIntOrNull()
                if (yearInStr != null) {
                    yearInStr >= currentYear
                } else {
                    dateStr.contains(currentYear.toString()) || dateStr.contains((currentYear + 1).toString())
                }
            }
        } catch (_: Exception) {
            false
        }
    }
}

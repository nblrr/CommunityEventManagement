package com.example.communityeventmanagement.data.model

data class UserProfile(
    val id: String,
    val name: String,
    val email: String,
    val password: String = "",
    val avatarUri: String? = null,
    val role: String = "User", // "User", "Organizer", "Admin"
    val isBlocked: Boolean = false,
    val isTrusted: Boolean = false,
    val trustedAppStatus: String = "NONE", // NONE, PENDING, APPROVED, REJECTED
    val organizerProfile: OrganizerProfile? = null
)

data class OrganizerProfile(
    val communityName: String,
    val picName: String,
    val description: String,
    val phone: String
)

data class Community(
    val id: Int,
    val name: String,
    val description: String,
    val category: String,
    val coverImageUri: String? = null,
    val organizerId: String,
    val organizerName: String,
    val memberIds: List<String> = emptyList(),
    val events: List<Event> = emptyList(),
    val forumMessages: List<ForumMessage> = emptyList()
) {
    val memberCount: Int get() = memberIds.size
}

data class Event(
    val id: Int,
    val title: String,
    val description: String,
    val date: String,
    val time: String = "",
    val location: String,
    val category: String,
    val maxAttendees: Int = 0,
    val coverImageUri: String? = null,
    val communityId: Int = 0,
    val registeredUserIds: List<String> = emptyList(),
    val galleryImages: List<String>? = emptyList(),
    val ratings: List<Rating>? = emptyList()
) {
    val attendeeCount: Int get() = registeredUserIds.size
}

data class Rating(
    val userId: String,
    val userName: String,
    val score: Int, // 1-5
    val comment: String,
    val date: String
)

data class ForumMessage(
    val sender: String,
    val message: String,
    val time: String,
    val avatarInitials: String
)

data class TrustedApplication(
    val userId: String,
    val userName: String,
    val communityName: String,
    val reason: String,
    val experience: String,
    val status: String = "PENDING" // PENDING, APPROVED, REJECTED
)

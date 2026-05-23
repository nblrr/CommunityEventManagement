package com.example.communityeventmanagement.domain.entities

data class Community(
    val id: Int,
    val name: String,
    val description: String,
    val category: String,
    val coverImageUri: String? = null,
    val organizerId: String,
    val organizerName: String,
    val memberCount: Int = 0,
    val memberIds: List<String> = emptyList(),
    val events: List<Event> = emptyList(),
    val forumMessages: List<ForumMessage> = emptyList(),
) {
}

data class ForumMessage(
    val sender: String,
    val message: String,
    val time: String,
    val avatarInitials: String,
)

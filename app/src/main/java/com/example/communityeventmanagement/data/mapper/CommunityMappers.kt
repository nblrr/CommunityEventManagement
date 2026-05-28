package com.example.communityeventmanagement.data.mapper

import com.example.communityeventmanagement.data.dto.CommunityDto
import com.example.communityeventmanagement.data.dto.ForumMessageDto
import com.example.communityeventmanagement.domain.entities.Community
import com.example.communityeventmanagement.domain.entities.ForumMessage

import android.util.Log

fun CommunityDto.toDomain(): Community {
    val commId = this.id ?: 0
    Log.d("MAPPER_DEBUG", "Mapping CommunityDto: id=$commId, name=$name")
    return Community(
        id = commId,
        name = this.name ?: "Unnamed Community",
        description = this.description ?: "",
        category = this.category ?: "General",
        coverImageUri = this.coverImageUri,
        organizerId = this.organizerId ?: "",
        organizerName = this.organizerName ?: "Unknown Organizer",
        memberCount = this.memberCount ?: (this.memberIds?.size ?: 0),
        memberIds = this.memberIds ?: emptyList(),
        events = (this.events ?: emptyList()).map { 
            it.toDomain().copy(communityId = commId) 
        },
        forumMessages = emptyList() // Loaded separately
    )
}

fun ForumMessageDto.toDomain(): ForumMessage {
    return ForumMessage(
        id = this.id ?: "msg_${System.currentTimeMillis()}",
        communityId = this.communityId ?: 0,
        sender = this.sender ?: "Unknown",
        message = this.message ?: "",
        time = this.time ?: "",
        avatarInitials = this.avatarInitials ?: ""
    )
}

fun Community.toDto(): CommunityDto {
    return CommunityDto(
        id = this.id,
        name = this.name,
        description = this.description,
        category = this.category,
        coverImageUri = this.coverImageUri,
        organizerId = this.organizerId,
        organizerName = this.organizerName,
        memberCount = this.memberCount,
        memberIds = this.memberIds,
        events = this.events.map { it.toDto() }
    )
}

fun ForumMessage.toDto(): ForumMessageDto {
    return ForumMessageDto(
        id = this.id,
        communityId = this.communityId,
        sender = this.sender,
        message = this.message,
        time = this.time,
        avatarInitials = this.avatarInitials
    )
}

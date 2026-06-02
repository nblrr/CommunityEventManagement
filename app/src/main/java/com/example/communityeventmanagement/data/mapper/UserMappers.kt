package com.example.communityeventmanagement.data.mapper

import com.example.communityeventmanagement.data.dto.OrganizerDto
import com.example.communityeventmanagement.data.dto.UserDto
import com.example.communityeventmanagement.domain.model.ApplicationStatus
import com.example.communityeventmanagement.domain.model.Organizer
import com.example.communityeventmanagement.domain.model.User
import com.example.communityeventmanagement.domain.model.UserRole

fun UserDto.toDomain(): User {
    return User(
        id = this.id ?: "",
        name = this.name ?: "Unknown User",
        email = this.email ?: "",
        password = this.password ?: "",
        bio = this.bio ?: "",
        avatarUri = this.avatarUri,
        role = try { 
            UserRole.valueOf((this.role ?: "USER").uppercase()) 
        } catch (_: Exception) { 
            UserRole.USER 
        },
        isBlocked = this.isBlocked ?: false,
        isTrusted = this.isTrusted ?: false,
        trustedApplicationStatus = try { 
            ApplicationStatus.valueOf((this.trustedApplicationStatus ?: "NONE").uppercase()) 
        } catch (_: Exception) { 
            ApplicationStatus.NONE 
        },
        organizer = this.organizer?.toDomain()
    )
}

fun OrganizerDto.toDomain(): Organizer {
    return Organizer(
        communityName = this.communityName ?: "",
        personInCharge = this.personInCharge ?: "",
        description = this.description ?: "",
        phone = this.phone ?: ""
    )
}

fun User.toDto(): UserDto {
    return UserDto(
        id = this.id,
        name = this.name,
        email = this.email,
        password = this.password,
        bio = this.bio,
        avatarUri = this.avatarUri,
        role = this.role.name,
        isBlocked = this.isBlocked,
        isTrusted = this.isTrusted,
        trustedApplicationStatus = this.trustedApplicationStatus.name,
        organizer = this.organizer?.toDto()
    )
}

fun Organizer.toDto(): OrganizerDto {
    return OrganizerDto(
        communityName = this.communityName,
        personInCharge = this.personInCharge,
        description = this.description,
        phone = this.phone
    )
}


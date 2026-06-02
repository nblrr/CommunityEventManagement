package com.example.communityeventmanagement.data.mapper

import com.example.communityeventmanagement.data.dto.TrustedApplicationDto
import com.example.communityeventmanagement.domain.model.ApplicationStatus
import com.example.communityeventmanagement.domain.model.TrustedApplication

fun TrustedApplicationDto.toDomain(): TrustedApplication {
    return TrustedApplication(
        userId = this.userId ?: "",
        userName = this.userName ?: "Unknown",
        communityName = this.communityName ?: "",
        reason = this.reason ?: "",
        experience = this.experience ?: "",
        status = try { 
            ApplicationStatus.valueOf((this.status ?: "PENDING").uppercase()) 
        } catch (_: Exception) { 
            ApplicationStatus.PENDING 
        }
    )
}

fun TrustedApplication.toDto(): TrustedApplicationDto {
    return TrustedApplicationDto(
        userId = this.userId,
        userName = this.userName,
        communityName = this.communityName,
        reason = this.reason,
        experience = this.experience,
        status = this.status.name
    )
}


package com.example.communityeventmanagement.data.dto

import com.google.gson.annotations.SerializedName

data class UserDto(
    @SerializedName("id") val id: String?,
    @SerializedName("name") val name: String?,
    @SerializedName("email") val email: String?,
    @SerializedName("password") val password: String? = "",
    @SerializedName("bio") val bio: String? = "",
    @SerializedName("avatar_uri") val avatarUri: String? = null,
    @SerializedName("role") val role: String?,
    @SerializedName("is_blocked") val isBlocked: Boolean? = false,
    @SerializedName("is_trusted") val isTrusted: Boolean? = false,
    @SerializedName("trusted_application_status") val trustedApplicationStatus: String?,
    @SerializedName("organizer") val organizer: OrganizerDto? = null
)

data class OrganizerDto(
    @SerializedName("community_name") val communityName: String?,
    @SerializedName("person_in_charge") val personInCharge: String?,
    @SerializedName("description") val description: String?,
    @SerializedName("phone") val phone: String?
)

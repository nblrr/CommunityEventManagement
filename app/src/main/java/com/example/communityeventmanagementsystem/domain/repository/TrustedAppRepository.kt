package com.example.communityeventmanagementsystem.domain.repository

import com.example.communityeventmanagementsystem.core.common.NetworkResult
import com.example.communityeventmanagementsystem.domain.model.TrustedApplication

interface TrustedAppRepository {
    suspend fun getMyApplication(): NetworkResult<TrustedApplication?>
    suspend fun submitApplication(communityName: String, reason: String, experience: String): NetworkResult<TrustedApplication>
}

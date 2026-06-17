package com.example.communityeventmanagementsystem.data.repository

import com.example.communityeventmanagementsystem.core.common.NetworkResult
import com.example.communityeventmanagementsystem.data.mapper.toDomain
import com.example.communityeventmanagementsystem.data.remote.api.TrustedAppApi
import com.example.communityeventmanagementsystem.data.remote.dto.SubmitTrustedAppRequest
import com.example.communityeventmanagementsystem.domain.model.TrustedApplication
import com.example.communityeventmanagementsystem.domain.repository.TrustedAppRepository
import javax.inject.Inject

class TrustedAppRepositoryImpl @Inject constructor(
    private val api: TrustedAppApi
) : TrustedAppRepository {

    override suspend fun getMyApplication(): NetworkResult<TrustedApplication?> {
        return try {
            val response = api.getMyApplication()
            NetworkResult.Success(response?.toDomain())
        } catch (e: retrofit2.HttpException) {
            if (e.code() == 404) {
                NetworkResult.Success(null)
            } else {
                NetworkResult.Error(e.message ?: "An HTTP error occurred")
            }
        } catch (e: Exception) {
            NetworkResult.Error(e.message ?: "An unknown error occurred")
        }
    }

    override suspend fun submitApplication(communityName: String, reason: String, experience: String): NetworkResult<TrustedApplication> {
        return try {
            val response = api.submitApplication(SubmitTrustedAppRequest(communityName, reason, experience))
            NetworkResult.Success(response.toDomain())
        } catch (e: Exception) {
            NetworkResult.Error(e.message ?: "An unknown error occurred")
        }
    }
}

package com.example.communityeventmanagementsystem.data.repository

import com.example.communityeventmanagementsystem.core.common.NetworkResult
import com.example.communityeventmanagementsystem.core.network.ErrorHandler
import com.example.communityeventmanagementsystem.data.mapper.toDomain
import com.example.communityeventmanagementsystem.data.remote.api.TrustedAppApi
import com.example.communityeventmanagementsystem.data.remote.dto.SubmitTrustedAppRequest
import com.example.communityeventmanagementsystem.domain.model.TrustedApplication
import com.example.communityeventmanagementsystem.domain.repository.TrustedAppRepository
import retrofit2.HttpException
import javax.inject.Inject

class TrustedAppRepositoryImpl @Inject constructor(
    private val api: TrustedAppApi
) : TrustedAppRepository {

    override suspend fun getMyApplication(): NetworkResult<TrustedApplication?> {
        return try {
            val response = api.getMyApplication()
            NetworkResult.Success(response?.toDomain())
        } catch (e: Exception) {
            if (e is HttpException && e.code() == 404) {
                NetworkResult.Success(null)
            } else {
                ErrorHandler.handleException(e)
            }
        }
    }

    override suspend fun submitApplication(communityName: String, reason: String, experience: String): NetworkResult<TrustedApplication> {
        return try {
            val response = api.submitApplication(SubmitTrustedAppRequest(communityName, reason, experience))
            NetworkResult.Success(response.toDomain())
        } catch (e: Exception) {
            ErrorHandler.handleException(e)
        }
    }
}

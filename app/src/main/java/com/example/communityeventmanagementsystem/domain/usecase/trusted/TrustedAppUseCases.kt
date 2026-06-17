package com.example.communityeventmanagementsystem.domain.usecase.trusted

import com.example.communityeventmanagementsystem.core.common.NetworkResult
import com.example.communityeventmanagementsystem.domain.model.TrustedApplication
import com.example.communityeventmanagementsystem.domain.repository.TrustedAppRepository
import javax.inject.Inject

class GetMyTrustedAppUseCase @Inject constructor(private val repository: TrustedAppRepository) {
    suspend operator fun invoke(): NetworkResult<TrustedApplication?> = repository.getMyApplication()
}

class SubmitTrustedAppUseCase @Inject constructor(private val repository: TrustedAppRepository) {
    suspend operator fun invoke(communityName: String, reason: String, experience: String): NetworkResult<TrustedApplication> =
        repository.submitApplication(communityName, reason, experience)
}

package com.example.communityeventmanagementsystem.domain.usecase.auth

import com.example.communityeventmanagementsystem.core.common.NetworkResult
import com.example.communityeventmanagementsystem.data.remote.dto.RegisterRequest
import com.example.communityeventmanagementsystem.domain.model.User
import com.example.communityeventmanagementsystem.domain.repository.AuthRepository
import javax.inject.Inject

class RegisterUseCase @Inject constructor(
    private val repository: AuthRepository
) {
    suspend operator fun invoke(request: RegisterRequest): NetworkResult<User> {
        return repository.register(request)
    }
}

package com.example.communityeventmanagementsystem.domain.usecase.auth

import com.example.communityeventmanagementsystem.core.common.NetworkResult
import com.example.communityeventmanagementsystem.data.remote.dto.LoginRequest
import com.example.communityeventmanagementsystem.domain.model.User
import com.example.communityeventmanagementsystem.domain.repository.AuthRepository
import javax.inject.Inject

class LoginUseCase @Inject constructor(
    private val repository: AuthRepository
) {
    suspend operator fun invoke(request: LoginRequest): NetworkResult<User> {
        return repository.login(request)
    }
}

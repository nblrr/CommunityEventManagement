package com.example.communityeventmanagementsystem.domain.usecase.admin

import com.example.communityeventmanagementsystem.core.common.NetworkResult
import com.example.communityeventmanagementsystem.domain.model.DashboardStats
import com.example.communityeventmanagementsystem.domain.model.TrustedApplication
import com.example.communityeventmanagementsystem.domain.model.User
import com.example.communityeventmanagementsystem.domain.repository.AdminRepository
import javax.inject.Inject

class GetDashboardStatsUseCase @Inject constructor(private val repository: AdminRepository) {
    suspend operator fun invoke(): NetworkResult<DashboardStats> = repository.getDashboardStats()
}

class GetUsersUseCase @Inject constructor(private val repository: AdminRepository) {
    suspend operator fun invoke(): NetworkResult<List<User>> = repository.getUsers()
}

class GetAdminTrustedAppsUseCase @Inject constructor(private val repository: AdminRepository) {
    suspend operator fun invoke(): NetworkResult<List<TrustedApplication>> = repository.getTrustedApplications()
}

class ApproveTrustedAppUseCase @Inject constructor(private val repository: AdminRepository) {
    suspend operator fun invoke(id: Long): NetworkResult<Unit> = repository.approveApplication(id)
}

class RejectTrustedAppUseCase @Inject constructor(private val repository: AdminRepository) {
    suspend operator fun invoke(id: Long, notes: String): NetworkResult<Unit> = repository.rejectApplication(id, notes)
}

class BlockUserUseCase @Inject constructor(private val repository: AdminRepository) {
    suspend operator fun invoke(id: Long): NetworkResult<Unit> = repository.blockUser(id)
}

class UnblockUserUseCase @Inject constructor(private val repository: AdminRepository) {
    suspend operator fun invoke(id: Long): NetworkResult<Unit> = repository.unblockUser(id)
}

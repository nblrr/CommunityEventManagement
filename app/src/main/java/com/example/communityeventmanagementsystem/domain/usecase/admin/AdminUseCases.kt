package com.example.communityeventmanagementsystem.domain.usecase.admin

import com.example.communityeventmanagementsystem.core.common.NetworkResult
import com.example.communityeventmanagementsystem.domain.model.DashboardStats
import com.example.communityeventmanagementsystem.domain.model.TrustedApplication
import com.example.communityeventmanagementsystem.domain.model.User
import com.example.communityeventmanagementsystem.domain.model.Community
import com.example.communityeventmanagementsystem.domain.model.Event
import com.example.communityeventmanagementsystem.domain.repository.AdminRepository
import javax.inject.Inject

class GetDashboardStatsUseCase @Inject constructor(private val repository: AdminRepository) {
    suspend operator fun invoke(): NetworkResult<DashboardStats> = repository.getDashboardStats()
}

class GetUsersUseCase @Inject constructor(private val repository: AdminRepository) {
    suspend operator fun invoke(search: String? = null, role: String? = null, status: String? = null): NetworkResult<List<User>> =
        repository.getUsers(search, role, status)
}

class CreateUserUseCase @Inject constructor(private val repository: AdminRepository) {
    suspend operator fun invoke(name: String, email: String, role: String, password: String): NetworkResult<User> =
        repository.createUser(name, email, role, password)
}

class DeleteUserUseCase @Inject constructor(private val repository: AdminRepository) {
    suspend operator fun invoke(id: Long): NetworkResult<Unit> = repository.deleteUser(id)
}

class UpdateUserRoleUseCase @Inject constructor(private val repository: AdminRepository) {
    suspend operator fun invoke(id: Long, role: String): NetworkResult<User> = repository.updateRole(id, role)
}

class RevokeTrustedUseCase @Inject constructor(private val repository: AdminRepository) {
    suspend operator fun invoke(id: Long): NetworkResult<Unit> = repository.revokeTrusted(id)
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

class GetAdminCommunitiesUseCase @Inject constructor(private val repository: AdminRepository) {
    suspend operator fun invoke(search: String? = null): NetworkResult<List<Community>> = repository.getCommunities(search)
}

class GetAdminEventsUseCase @Inject constructor(private val repository: AdminRepository) {
    suspend operator fun invoke(search: String? = null): NetworkResult<List<Event>> = repository.getEvents(search)
}

class DeleteCommunityUseCase @Inject constructor(private val repository: AdminRepository) {
    suspend operator fun invoke(id: Long): NetworkResult<Unit> = repository.deleteCommunity(id)
}

class DeleteEventUseCase @Inject constructor(private val repository: AdminRepository) {
    suspend operator fun invoke(id: Long): NetworkResult<Unit> = repository.deleteEvent(id)
}

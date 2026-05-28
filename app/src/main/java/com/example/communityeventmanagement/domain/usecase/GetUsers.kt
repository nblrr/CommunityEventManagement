package com.example.communityeventmanagement.domain.usecase

import com.example.communityeventmanagement.domain.entities.User
import com.example.communityeventmanagement.domain.repository.UserRepository
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

class GetUsers @Inject constructor(private val userRepository: UserRepository) {
    operator fun invoke(): StateFlow<List<User>> {
        return userRepository.users
    }
}

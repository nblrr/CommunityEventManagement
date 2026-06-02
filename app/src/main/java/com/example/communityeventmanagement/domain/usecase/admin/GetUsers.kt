package com.example.communityeventmanagement.domain.usecase.admin

import com.example.communityeventmanagement.domain.model.User
import com.example.communityeventmanagement.domain.repository.UserRepository
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

class GetUsers @Inject constructor(private val userRepository: UserRepository) {
    operator fun invoke(): StateFlow<List<User>> {
        return userRepository.users
    }
}


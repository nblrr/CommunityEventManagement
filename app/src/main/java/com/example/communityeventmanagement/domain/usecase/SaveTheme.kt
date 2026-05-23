package com.example.communityeventmanagement.domain.usecase

import com.example.communityeventmanagement.domain.entities.ThemeMode
import com.example.communityeventmanagement.domain.repository.UserRepository

/**
 * UseCase to save user's theme preference.
 */
class SaveTheme(private val userRepository: UserRepository) {
    suspend operator fun invoke(mode: ThemeMode) {
        userRepository.saveTheme(mode)
    }
}

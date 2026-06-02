package com.example.communityeventmanagement.domain.model

import com.example.communityeventmanagement.R

enum class UserRole(val resId: Int) {
    USER(R.string.role_member),
    ORGANIZER(R.string.label_organizer),
    ADMIN(R.string.role_admin)
}

enum class ApplicationStatus(val resId: Int) {
    NONE(R.string.status_inactive),
    PENDING(R.string.menu_verification_in_progress),
    APPROVED(R.string.status_active),
    REJECTED(R.string.btn_reject)
}

enum class ThemeMode {
    AUTO,
    LIGHT,
    DARK
}


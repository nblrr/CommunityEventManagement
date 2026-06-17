package com.example.communityeventmanagementsystem.presentation.navigation

sealed class Screen(val route: String) {
    object Login : Screen("login")
    object Register : Screen("register")
    object Home : Screen("home")

    object CommunityList : Screen("community_list?categoryId={categoryId}") {
        const val ROUTE = "community_list?categoryId={categoryId}"
        fun createRoute(categoryId: Long? = null) =
            if (categoryId != null) "community_list?categoryId=$categoryId" else "community_list"
    }

    data class CommunityDetail(val id: Long) : Screen("community_detail/$id") {
        companion object {
            const val ROUTE = "community_detail/{id}"
        }
    }

    data class CommunityForum(val id: Long) : Screen("community_forum/$id") {
        companion object {
            const val ROUTE = "community_forum/{id}"
        }
    }

    object EventList : Screen("event_list?categoryId={categoryId}") {
        const val ROUTE = "event_list?categoryId={categoryId}"
        fun createRoute(categoryId: Long? = null) =
            if (categoryId != null) "event_list?categoryId=$categoryId" else "event_list"
    }

    data class EventDetail(val id: Long) : Screen("event_detail/$id") {
        companion object {
            const val ROUTE = "event_detail/{id}"
        }
    }

    object Notifications : Screen("notifications")
    object EditProfile : Screen("edit_profile")
    object TrustedApplication : Screen("trusted_application")
    object OrganizerDashboard : Screen("organizer_dashboard")
    object AdminDashboard : Screen("admin_dashboard")
    object SavedEvents : Screen("saved_events")
    object CreateCommunity : Screen("create_community")
    object CreateEvent : Screen("create_event")
    object Profile : Screen("profile")
    object TrustedOrganizerApplication : Screen("trusted_organizer_application")
    object OrganizerRegistration : Screen("organizer_registration")
}

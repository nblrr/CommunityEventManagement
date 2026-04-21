package com.example.communityeventmanagement.navigation

sealed class Route {

    data object Login : Route()

    data object Register : Route()

    data object Home : Route()

    data object Profile : Route()

    data object OrganizerRegister : Route()

    data object CommunityList : Route()

    data class CommunityDetail(val communityId: Int) : Route()

    data object CreateCommunity : Route()

    data class CreateEvent(val communityId: Int) : Route()

    data class Forum(val communityId: Int) : Route()
}
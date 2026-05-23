package com.example.communityeventmanagement.data.repository

import android.content.Context
import com.example.communityeventmanagement.data.source.local.DataStoreManager
import com.example.communityeventmanagement.data.source.local.JsonDataSource
import com.example.communityeventmanagement.domain.repository.CommunityRepository
import com.example.communityeventmanagement.domain.repository.UserRepository
import com.example.communityeventmanagement.domain.usecase.AddEventRating
import com.example.communityeventmanagement.domain.usecase.ApproveApplication
import com.example.communityeventmanagement.domain.usecase.CancelEvent
import com.example.communityeventmanagement.domain.usecase.CreateCommunity
import com.example.communityeventmanagement.domain.usecase.CreateEvent
import com.example.communityeventmanagement.domain.usecase.DeleteCommunity
import com.example.communityeventmanagement.domain.usecase.DeleteEvent
import com.example.communityeventmanagement.domain.usecase.GetCommunities
import com.example.communityeventmanagement.domain.usecase.GetCommunityDetail
import com.example.communityeventmanagement.domain.usecase.GetCurrentUser
import com.example.communityeventmanagement.domain.usecase.GetEventDetail
import com.example.communityeventmanagement.domain.usecase.GetForumMessages
import com.example.communityeventmanagement.domain.usecase.GetJoinedCommunityIds
import com.example.communityeventmanagement.domain.usecase.GetPendingApplications
import com.example.communityeventmanagement.domain.usecase.GetRecommendedCommunities
import com.example.communityeventmanagement.domain.usecase.GetRecommendedEvents
import com.example.communityeventmanagement.domain.usecase.GetRegisteredEventIds
import com.example.communityeventmanagement.domain.usecase.GetUsers
import com.example.communityeventmanagement.domain.usecase.JoinCommunity
import com.example.communityeventmanagement.domain.usecase.JoinEvent
import com.example.communityeventmanagement.domain.usecase.Login
import com.example.communityeventmanagement.domain.usecase.Logout
import com.example.communityeventmanagement.domain.usecase.RefreshData
import com.example.communityeventmanagement.domain.usecase.Register
import com.example.communityeventmanagement.domain.usecase.RegisterOrganizer
import com.example.communityeventmanagement.domain.usecase.RejectApplication
import com.example.communityeventmanagement.domain.usecase.SaveTheme
import com.example.communityeventmanagement.domain.usecase.SendMessage
import com.example.communityeventmanagement.domain.usecase.SubmitTrustedApplication
import com.example.communityeventmanagement.domain.usecase.ToggleUserBlock
import com.example.communityeventmanagement.domain.usecase.UpdateAvatar
import com.example.communityeventmanagement.domain.usecase.UpdateCommunity
import com.example.communityeventmanagement.domain.usecase.UpdateEvent
import com.example.communityeventmanagement.domain.usecase.UpdateProfile

interface AppContainer {
    val userRepository: UserRepository
    val communityRepository: CommunityRepository
    
    val login: Login
    val logout: Logout
    val register: Register
    val registerOrganizer: RegisterOrganizer
    val getCurrentUser: GetCurrentUser
    val refreshData: RefreshData
    val getCommunities: GetCommunities
    val getRecommendedCommunities: GetRecommendedCommunities
    val getRecommendedEvents: GetRecommendedEvents
    val getRegisteredEventIds: GetRegisteredEventIds
    val joinCommunity: JoinCommunity
    val joinEvent: JoinEvent
    val cancelEvent: CancelEvent
    val addEventRating: AddEventRating
    val updateAvatar: UpdateAvatar
    val submitTrustedApplication: SubmitTrustedApplication
    val updateProfile: UpdateProfile
    val saveTheme: SaveTheme
    
    val getUsers: GetUsers
    val getPendingApplications: GetPendingApplications
    val approveApplication: ApproveApplication
    val rejectApplication: RejectApplication
    val toggleUserBlock: ToggleUserBlock

    // New UseCases
    val getJoinedCommunityIds: GetJoinedCommunityIds
    val getCommunityDetail: GetCommunityDetail
    val createCommunity: CreateCommunity
    val updateCommunity: UpdateCommunity
    val deleteCommunity: DeleteCommunity
    val createEvent: CreateEvent
    val updateEvent: UpdateEvent
    val deleteEvent: DeleteEvent
    val getEventDetail: GetEventDetail
    val getForumMessages: GetForumMessages
    val sendMessage: SendMessage
    
    suspend fun initialize()
}

class DefaultAppContainer(private val context: Context) : AppContainer {
    private val dataSource: JsonDataSource by lazy {
        JsonDataSource(context.applicationContext)
    }

    private val dataStoreManager: DataStoreManager by lazy {
        DataStoreManager(context.applicationContext)
    }

    override val userRepository: UserRepository by lazy {
        UserRepository(dataSource, dataStoreManager)
    }

    override val communityRepository: CommunityRepository by lazy {
        CommunityRepository(dataSource)
    }

    override val login: Login by lazy { Login(userRepository) }
    override val logout: Logout by lazy { Logout(userRepository) }
    override val register: Register by lazy { Register(userRepository) }
    override val registerOrganizer: RegisterOrganizer by lazy { RegisterOrganizer(userRepository) }
    override val getCurrentUser: GetCurrentUser by lazy { GetCurrentUser(userRepository) }
    override val refreshData: RefreshData by lazy { RefreshData(communityRepository) }
    override val getCommunities: GetCommunities by lazy { GetCommunities(communityRepository) }
    override val getRecommendedCommunities: GetRecommendedCommunities by lazy { GetRecommendedCommunities(communityRepository) }
    override val getRecommendedEvents: GetRecommendedEvents by lazy { GetRecommendedEvents(communityRepository) }
    override val getRegisteredEventIds: GetRegisteredEventIds by lazy { GetRegisteredEventIds(communityRepository) }
    override val joinCommunity: JoinCommunity by lazy { JoinCommunity(communityRepository) }
    override val joinEvent: JoinEvent by lazy { JoinEvent(communityRepository) }
    override val cancelEvent: CancelEvent by lazy { CancelEvent(communityRepository) }
    override val addEventRating: AddEventRating by lazy { AddEventRating(communityRepository) }
    override val updateAvatar: UpdateAvatar by lazy { UpdateAvatar(userRepository) }
    override val submitTrustedApplication: SubmitTrustedApplication by lazy { SubmitTrustedApplication(userRepository) }
    override val updateProfile: UpdateProfile by lazy { UpdateProfile(userRepository) }
    override val saveTheme: SaveTheme by lazy { SaveTheme(userRepository) }
    override val getUsers: GetUsers by lazy { GetUsers(userRepository) }
    override val getPendingApplications: GetPendingApplications by lazy { GetPendingApplications(userRepository) }
    override val approveApplication: ApproveApplication by lazy { ApproveApplication(userRepository) }
    override val rejectApplication: RejectApplication by lazy { RejectApplication(userRepository) }
    override val toggleUserBlock: ToggleUserBlock by lazy { ToggleUserBlock(userRepository) }

    // New UseCases implementation
    override val getJoinedCommunityIds: GetJoinedCommunityIds by lazy { GetJoinedCommunityIds(communityRepository) }
    override val getCommunityDetail: GetCommunityDetail by lazy { GetCommunityDetail(communityRepository) }
    override val createCommunity: CreateCommunity by lazy { CreateCommunity(communityRepository) }
    override val updateCommunity: UpdateCommunity by lazy { UpdateCommunity(communityRepository) }
    override val deleteCommunity: DeleteCommunity by lazy { DeleteCommunity(communityRepository) }
    override val createEvent: CreateEvent by lazy { CreateEvent(communityRepository) }
    override val updateEvent: UpdateEvent by lazy { UpdateEvent(communityRepository) }
    override val deleteEvent: DeleteEvent by lazy { DeleteEvent(communityRepository) }
    override val getEventDetail: GetEventDetail by lazy { GetEventDetail(communityRepository) }
    override val getForumMessages: GetForumMessages by lazy { GetForumMessages(communityRepository) }
    override val sendMessage: SendMessage by lazy { SendMessage(communityRepository) }

    override suspend fun initialize() {
        userRepository.initialize()
        val allUsers = userRepository.users.value
        communityRepository.loadCommunities(allUsers)
        
        val user = userRepository.currentUser.value
        if (user != null) {
            communityRepository.refreshUserParticipation(user)
        }
    }
}

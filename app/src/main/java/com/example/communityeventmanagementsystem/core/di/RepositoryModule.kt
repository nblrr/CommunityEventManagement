package com.example.communityeventmanagementsystem.core.di

import com.example.communityeventmanagementsystem.data.repository.AdminRepositoryImpl
import com.example.communityeventmanagementsystem.data.repository.AuthRepositoryImpl
import com.example.communityeventmanagementsystem.data.repository.CommunityRepositoryImpl
import com.example.communityeventmanagementsystem.data.repository.EventRepositoryImpl
import com.example.communityeventmanagementsystem.data.repository.ForumRepositoryImpl
import com.example.communityeventmanagementsystem.data.repository.HomeRepositoryImpl
import com.example.communityeventmanagementsystem.data.repository.NotificationRepositoryImpl
import com.example.communityeventmanagementsystem.data.repository.OrganizerRepositoryImpl
import com.example.communityeventmanagementsystem.data.repository.ProfileRepositoryImpl
import com.example.communityeventmanagementsystem.data.repository.MediaRepositoryImpl
import com.example.communityeventmanagementsystem.data.repository.TrustedAppRepositoryImpl
import com.example.communityeventmanagementsystem.domain.repository.AdminRepository
import com.example.communityeventmanagementsystem.domain.repository.AuthRepository
import com.example.communityeventmanagementsystem.domain.repository.CommunityRepository
import com.example.communityeventmanagementsystem.domain.repository.EventRepository
import com.example.communityeventmanagementsystem.domain.repository.ForumRepository
import com.example.communityeventmanagementsystem.domain.repository.HomeRepository
import com.example.communityeventmanagementsystem.domain.repository.NotificationRepository
import com.example.communityeventmanagementsystem.domain.repository.OrganizerRepository
import com.example.communityeventmanagementsystem.domain.repository.ProfileRepository
import com.example.communityeventmanagementsystem.domain.repository.MediaRepository
import com.example.communityeventmanagementsystem.domain.repository.TrustedAppRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindAuthRepository(
        authRepositoryImpl: AuthRepositoryImpl
    ): AuthRepository

    @Binds
    @Singleton
    abstract fun bindProfileRepository(
        profileRepositoryImpl: ProfileRepositoryImpl
    ): ProfileRepository

    @Binds
    @Singleton
    abstract fun bindMediaRepository(
        mediaRepositoryImpl: MediaRepositoryImpl
    ): MediaRepository

    @Binds
    @Singleton
    abstract fun bindHomeRepository(
        homeRepositoryImpl: HomeRepositoryImpl
    ): HomeRepository

    @Binds
    @Singleton
    abstract fun bindCommunityRepository(
        communityRepositoryImpl: CommunityRepositoryImpl
    ): CommunityRepository

    @Binds
    @Singleton
    abstract fun bindEventRepository(
        eventRepositoryImpl: EventRepositoryImpl
    ): EventRepository

    @Binds
    @Singleton
    abstract fun bindForumRepository(
        forumRepositoryImpl: ForumRepositoryImpl
    ): ForumRepository

    @Binds
    @Singleton
    abstract fun bindNotificationRepository(
        notificationRepositoryImpl: NotificationRepositoryImpl
    ): NotificationRepository

    @Binds
    @Singleton
    abstract fun bindTrustedAppRepository(
        trustedAppRepositoryImpl: TrustedAppRepositoryImpl
    ): TrustedAppRepository

    @Binds
    @Singleton
    abstract fun bindOrganizerRepository(
        organizerRepositoryImpl: OrganizerRepositoryImpl
    ): OrganizerRepository

    @Binds
    @Singleton
    abstract fun bindAdminRepository(
        adminRepositoryImpl: AdminRepositoryImpl
    ): AdminRepository
}

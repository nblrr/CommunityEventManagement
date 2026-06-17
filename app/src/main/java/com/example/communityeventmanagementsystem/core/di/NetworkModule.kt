package com.example.communityeventmanagementsystem.core.di

import com.example.communityeventmanagementsystem.BuildConfig
import com.example.communityeventmanagementsystem.core.network.AuthInterceptor
import com.example.communityeventmanagementsystem.data.remote.api.AdminApi
import com.example.communityeventmanagementsystem.data.remote.api.AuthApi
import com.example.communityeventmanagementsystem.data.remote.api.CommunityApi
import com.example.communityeventmanagementsystem.data.remote.api.EventApi
import com.example.communityeventmanagementsystem.data.remote.api.ForumApi
import com.example.communityeventmanagementsystem.data.remote.api.HomeApi
import com.example.communityeventmanagementsystem.data.remote.api.NotificationApi
import com.example.communityeventmanagementsystem.data.remote.api.OrganizerApi
import com.example.communityeventmanagementsystem.data.remote.api.ProfileApi
import com.example.communityeventmanagementsystem.data.remote.api.TrustedAppApi
import com.google.gson.Gson
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    @Singleton
    fun provideGson(): Gson = Gson()

    @Provides
    @Singleton
    fun provideLoggingInterceptor(): HttpLoggingInterceptor {
        return HttpLoggingInterceptor().apply {
            level = if (BuildConfig.DEBUG_FLAG) {
                HttpLoggingInterceptor.Level.BODY
            } else {
                HttpLoggingInterceptor.Level.NONE
            }
        }
    }

    @Provides
    @Singleton
    fun provideOkHttpClient(
        loggingInterceptor: HttpLoggingInterceptor,
        authInterceptor: AuthInterceptor
    ): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .addInterceptor(authInterceptor)
            .build()
    }

    @Provides
    @Singleton
    fun provideRetrofit(okHttpClient: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BuildConfig.BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    @Provides
    @Singleton
    fun provideAuthApi(retrofit: Retrofit): AuthApi {
        return retrofit.create(AuthApi::class.java)
    }

    @Provides
    @Singleton
    fun provideProfileApi(retrofit: Retrofit): ProfileApi {
        return retrofit.create(ProfileApi::class.java)
    }

    @Provides
    @Singleton
    fun provideHomeApi(retrofit: Retrofit): HomeApi {
        return retrofit.create(HomeApi::class.java)
    }

    @Provides
    @Singleton
    fun provideCommunityApi(retrofit: Retrofit): CommunityApi {
        return retrofit.create(CommunityApi::class.java)
    }

    @Provides
    @Singleton
    fun provideEventApi(retrofit: Retrofit): EventApi {
        return retrofit.create(EventApi::class.java)
    }

    @Provides
    @Singleton
    fun provideForumApi(retrofit: Retrofit): ForumApi {
        return retrofit.create(ForumApi::class.java)
    }

    @Provides
    @Singleton
    fun provideNotificationApi(retrofit: Retrofit): NotificationApi {
        return retrofit.create(NotificationApi::class.java)
    }

    @Provides
    @Singleton
    fun provideTrustedAppApi(retrofit: Retrofit): TrustedAppApi {
        return retrofit.create(TrustedAppApi::class.java)
    }

    @Provides
    @Singleton
    fun provideOrganizerApi(retrofit: Retrofit): OrganizerApi {
        return retrofit.create(OrganizerApi::class.java)
    }

    @Provides
    @Singleton
    fun provideAdminApi(retrofit: Retrofit): AdminApi {
        return retrofit.create(AdminApi::class.java)
    }
}

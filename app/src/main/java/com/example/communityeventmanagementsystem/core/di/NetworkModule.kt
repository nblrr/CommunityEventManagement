package com.example.communityeventmanagementsystem.core.di

import android.content.Context
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
import com.example.communityeventmanagementsystem.data.remote.api.MediaApi
import com.example.communityeventmanagementsystem.data.remote.api.TrustedAppApi
import com.google.gson.Gson
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.Cache
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.File
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    @Singleton
    fun provideGson(): Gson = Gson()

    @Provides
    @Singleton
    fun provideCache(@ApplicationContext context: Context): Cache {
        val cacheSize = 50 * 1024 * 1024L // 50MB
        return Cache(File(context.cacheDir, "http_cache"), cacheSize)
    }

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
        authInterceptor: AuthInterceptor,
        cache: Cache
    ): OkHttpClient {
        return OkHttpClient.Builder()
            .cache(cache)
            .addInterceptor(loggingInterceptor)
            .addInterceptor(authInterceptor)
            .addInterceptor { chain ->
                var request = chain.request()
                // Force cache for certain public endpoints if needed, 
                // but usually we respect Cache-Control from server.
                chain.proceed(request)
            }
            .connectTimeout(60, TimeUnit.SECONDS)
            .readTimeout(60, TimeUnit.SECONDS)
            .writeTimeout(60, TimeUnit.SECONDS)
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
    fun provideMediaApi(retrofit: Retrofit): MediaApi {
        return retrofit.create(MediaApi::class.java)
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

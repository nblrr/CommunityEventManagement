package com.example.communityeventmanagementsystem.data.remote.api

import com.example.communityeventmanagementsystem.data.remote.dto.EventDto
import com.example.communityeventmanagementsystem.data.remote.dto.UserDto
import com.google.gson.annotations.SerializedName
import retrofit2.http.*

interface EventApi {
    @GET("events")
    suspend fun getEvents(
        @Query("page") page: Int,
        @Query("category_id") categoryId: Long? = null,
        @Query("search") search: String? = null
    ): EventListResponse

    @GET("events/{id}")
    suspend fun getEventDetail(@Path("id") id: Long): com.example.communityeventmanagementsystem.data.remote.dto.EventDetailResponse

    @POST("events/{id}/register")
    suspend fun registerToEvent(@Path("id") id: Long)

    @POST("events/{id}/cancel")
    suspend fun unregisterFromEvent(@Path("id") id: Long)

    @GET("my-events")
    suspend fun getMyEvents(
        @Query("page") page: Int
    ): EventListResponse

    @GET("events/{id}/ratings")
    suspend fun getEventRatings(@Path("id") id: Long): List<EventRatingDto>

    @POST("events/{id}/ratings")
    suspend fun rateEvent(
        @Path("id") id: Long,
        @Body ratingRequest: RateEventRequest
    ): EventRatingDto
}

data class EventListResponse(
    val data: List<EventDto>,
    val current_page: Int,
    val last_page: Int
)

data class EventRatingDto(
    val id: Long,
    @SerializedName("user_id") val userId: Long,
    val rating: Int,
    val comment: String?,
    val user: UserDto?
)

data class RateEventRequest(
    val rating: Int,
    val comment: String
)

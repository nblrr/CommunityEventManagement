package com.example.communityeventmanagement.domain.model

data class Rating(
    val userId: String,
    val userName: String,
    val score: Int,
    val comment: String,
    val date: String,
)


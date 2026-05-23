package com.example.communityeventmanagement.domain.entities

data class Rating(
    val userId: String,
    val userName: String,
    val score: Int,
    val comment: String,
    val date: String,
)

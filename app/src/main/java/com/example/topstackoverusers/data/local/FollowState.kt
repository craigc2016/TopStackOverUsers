package com.example.topstackoverusers.data.local

import kotlinx.serialization.Serializable

@Serializable
data class FollowState(val userId: Int, val isFollowed: Boolean)

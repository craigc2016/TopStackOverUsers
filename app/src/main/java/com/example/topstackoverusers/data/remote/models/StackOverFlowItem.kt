package com.example.topstackoverusers.data.remote.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class StackOverFlowItem(
    @SerialName("display_name")
    val displayName: String,
    @SerialName("profile_image")
    val profileImage: String,
    val reputation: Int,
)
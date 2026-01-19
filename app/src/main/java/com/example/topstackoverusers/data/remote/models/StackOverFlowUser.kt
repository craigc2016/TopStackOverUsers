package com.example.topstackoverusers.data.remote.models

import androidx.compose.ui.graphics.ImageBitmap
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

@Serializable
data class StackOverFlowUser(
    @SerialName("user_id")
    val userId: Int,
    @SerialName("display_name")
    val displayName: String? = null,
    @SerialName("profile_image")
    val profileImageUrl: String,
    val reputation: Int,
    @Transient
    val profileImage: ImageBitmap? = null,
    val isFollowed: Boolean = false
)
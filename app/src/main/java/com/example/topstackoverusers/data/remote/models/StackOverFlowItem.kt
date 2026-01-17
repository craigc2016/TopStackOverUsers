package com.example.topstackoverusers.data.remote.models

import androidx.compose.ui.graphics.ImageBitmap
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

@Serializable
data class StackOverFlowItem(
    @SerialName("user_id")
    val userId: Int,
    @SerialName("display_name")
    val displayName: String = "",
    @SerialName("profile_image")
    val profileImageUrl: String,
    val reputation: Int,
    @Transient
    val profileImage: ImageBitmap? = null
)
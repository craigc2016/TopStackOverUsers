package com.example.topstackoverusers.data.remote.models

import com.example.topstackoverusers.viewmodel.UserUiModel
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class StackOverFlowUser(
    @SerialName("user_id")
    val userId: Int,
    @SerialName("display_name")
    val displayName: String? = null,
    @SerialName("profile_image")
    val profileImageUrl: String,
    val reputation: Int,
)

fun StackOverFlowUser.toUiModel(): UserUiModel =
    UserUiModel(
        userId = userId,
        displayName = displayName ?: "Unknown",
        reputation = reputation,
        profileImageUrl = profileImageUrl
    )
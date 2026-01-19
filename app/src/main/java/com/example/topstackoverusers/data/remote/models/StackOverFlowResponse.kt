package com.example.topstackoverusers.data.remote.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class StackOverFlowResponse(
    @SerialName("items")
    val stackOverItems: List<StackOverFlowUser>
)

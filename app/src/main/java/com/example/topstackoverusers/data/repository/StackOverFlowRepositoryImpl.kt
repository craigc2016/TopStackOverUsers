package com.example.topstackoverusers.data.repository

import com.example.topstackoverusers.data.local.FollowState
import com.example.topstackoverusers.data.local.UserPreferencesRepository
import com.example.topstackoverusers.data.remote.ApiService
import com.example.topstackoverusers.data.remote.ImageService
import com.example.topstackoverusers.data.remote.models.StackOverFlowResponse
import kotlinx.coroutines.flow.Flow

interface StackOverFlowRepository{
    suspend fun getTopUsers(): StackOverFlowResponse
    suspend fun loadImage(url: String) : ByteArray

    val followedState: Flow<Set<Int>>
    suspend fun toggleFollowedState(userId: Int, isFollowed: Boolean)
}

class StackOverFlowRepositoryImpl(
    private val apiService: ApiService,
    private val imageService: ImageService,
    private val dataStore: UserPreferencesRepository
) : StackOverFlowRepository {

    override suspend fun getTopUsers(): StackOverFlowResponse {
        return apiService.getTopUsers()
    }

    override suspend fun loadImage(url: String) : ByteArray {
        return imageService.loadImage(url)
    }

    override val followedState: Flow<Set<Int>>
        get() = dataStore.followState

    override suspend fun toggleFollowedState(userId: Int, isFollowed: Boolean) {
        dataStore.setFollowed(FollowState(userId = userId, isFollowed = isFollowed))
    }
}
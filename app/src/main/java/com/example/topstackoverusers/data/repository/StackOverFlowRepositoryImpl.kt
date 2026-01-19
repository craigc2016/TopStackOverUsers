package com.example.topstackoverusers.data.repository

import android.graphics.Bitmap
import com.example.topstackoverusers.data.remote.ApiService
import com.example.topstackoverusers.data.remote.ImageService
import com.example.topstackoverusers.data.remote.models.StackOverFlowResponse
import kotlinx.coroutines.flow.Flow

interface StackOverFlowRepository{
    suspend fun getTopUsers(): StackOverFlowResponse
    suspend fun loadImage(url: String) : Bitmap?

    val followedState: Flow<Map<Int, Boolean>>
    suspend fun toggleFollowedState(id: Int, isFollowed: Boolean)
}

class StackOverFlowRepositoryImpl(
    private val apiService: ApiService,
    private val imageService: ImageService,
    private val dataStore: UserPreferencesRepository
) : StackOverFlowRepository {

    override suspend fun getTopUsers(): StackOverFlowResponse {
        return apiService.getTopUsers()
    }

    override suspend fun loadImage(url: String) : Bitmap? {
        return imageService.loadImage(url)
    }

    override val followedState: Flow<Map<Int, Boolean>>
        get() = dataStore.followState

    override suspend fun toggleFollowedState(id: Int, isFollowed: Boolean) {
        dataStore.setFollowed(id, isFollowed)
    }
}
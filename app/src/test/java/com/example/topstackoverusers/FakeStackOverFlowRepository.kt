package com.example.topstackoverusers

import androidx.compose.ui.graphics.ImageBitmap
import com.example.topstackoverusers.data.remote.models.StackOverFlowResponse
import com.example.topstackoverusers.data.remote.models.StackOverFlowUser
import com.example.topstackoverusers.data.repository.StackOverFlowRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

class FakeImageBitmap

class FakeStackOverFlowRepository: StackOverFlowRepository {

    var fakeImage: FakeImageBitmap? = null

    override suspend fun getTopUsers(): StackOverFlowResponse {
        return StackOverFlowResponse(
            stackOverItems = listOf(
                StackOverFlowUser(userId = 1, displayName = "John Doe", profileImageUrl = "url1", reputation = 100),
                StackOverFlowUser(userId = 2, displayName = "Jane Doe", profileImageUrl = "url2", reputation = 200),
                StackOverFlowUser(userId = 3, displayName = "John Test", profileImageUrl = "url3", reputation = 100)
        ))
    }

    override suspend fun loadImage(url: String): ImageBitmap? = null

    override val followedState: Flow<Set<Int>>
        get() = flowOf(setOf(1,3))

    override suspend fun toggleFollowedState(userId: Int, isFollowed: Boolean) {

    }
}
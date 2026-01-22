package com.example.topstackoverusers.mocks

import com.example.topstackoverusers.data.remote.models.StackOverFlowResponse
import com.example.topstackoverusers.data.remote.models.StackOverFlowUser
import com.example.topstackoverusers.data.repository.StackOverFlowRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update

open class FakeStackOverFlowRepository(
    var throwOnGetUsers: Boolean = false,
    initialFollowed: Set<Int> = setOf(1, 3)
): StackOverFlowRepository {

    private val _followedState = MutableStateFlow(initialFollowed)

    override suspend fun getTopUsers(): StackOverFlowResponse {
        return if (throwOnGetUsers) {
            throw Exception("Failed to load users")
        } else {
            StackOverFlowResponse(
                stackOverItems = listOf(
                    StackOverFlowUser(
                        userId = 1,
                        displayName = "John Doe",
                        profileImageUrl = "url1",
                        reputation = 100
                    ),
                    StackOverFlowUser(
                        userId = 2,
                        displayName = "Jane Doe",
                        profileImageUrl = "url2",
                        reputation = 200
                    ),
                    StackOverFlowUser(
                        userId = 3,
                        displayName = "John Test",
                        profileImageUrl = "url3",
                        reputation = 100
                    )
                )
            )
        }
    }

    override suspend fun loadImage(url: String): ByteArray = ByteArray(0)


    override val followedState: Flow<Set<Int>> = _followedState

    override suspend fun toggleFollowedState(userId: Int, isFollowed: Boolean) {
        _followedState.update { current ->
            if (isFollowed) current + userId else current - userId
        }

    }
}
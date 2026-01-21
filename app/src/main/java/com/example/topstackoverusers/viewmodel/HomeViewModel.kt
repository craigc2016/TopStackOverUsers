package com.example.topstackoverusers.viewmodel

import android.util.Log
import androidx.compose.ui.graphics.ImageBitmap
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.topstackoverusers.data.remote.models.toUiModel
import com.example.topstackoverusers.data.repository.StackOverFlowRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.supervisorScope

class HomeViewModel(
    private val repository: StackOverFlowRepository,
    private val ioDispatcher: CoroutineDispatcher
) : ViewModel() {

    private val _isLoading = MutableStateFlow(false)
    private val _errorState = MutableStateFlow<Throwable?>(null)
    private val _userCache: MutableStateFlow<Map<Int, UserUiModel>> = MutableStateFlow(emptyMap())

    val uiState: StateFlow<UiState> = combine(
        _userCache,
        repository.followedState,
        _errorState,
        _isLoading
    ) { cache, followedIds, error, isLoading ->

        // update the follow state
        val list = cache.values.map { item ->
            item.copy(
                isFollowed = item.userId in followedIds
            )
        }

        // set the Ui state
        when {
            isLoading -> UiState.Loading
            error != null -> UiState.Error
            else -> UiState.Success(data = list)
        }
    }
        .stateIn(
            viewModelScope,
            SharingStarted.Lazily,
            UiState.Loading
        )

    fun loadUsers() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val usersResponse = repository.getTopUsers()
                // Map the response network model to the UI version one.
                _userCache.value = usersResponse.stackOverItems.associateBy(
                    keySelector = { it.userId },
                    valueTransform = { it.toUiModel() }
                )

                supervisorScope {
                    usersResponse.stackOverItems.map { item ->
                        launch(ioDispatcher) {
                            loadImage(item.userId)
                        }
                    }
                }
            } catch (e: Exception) {
                _errorState.value = e
                Log.e(TAG, "Failed to load users", e)
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun onRetry() {
        _errorState.value = null
        loadUsers()
    }


    fun followUser(userId: Int, isFollowed: Boolean) = viewModelScope.launch(ioDispatcher) {
        try {
            repository.toggleFollowedState(userId, isFollowed)
        } catch (e : Exception) {
            _errorState.value = e
        }
    }

    private suspend fun loadImage(userId: Int) {
        val cachedItem = _userCache.value[userId] ?: return

        // If image is already loaded, skip
        if (cachedItem.profileImage != null) return

        val bitmap = repository.loadImage(cachedItem.profileImageUrl)

        // Update the cache with the loaded image bitmap
        val updatedItem = cachedItem.copy(profileImage = bitmap)

        _userCache.update { cache ->
            cache + (userId to updatedItem)
        }
    }

    companion object {
        private val TAG = "HomeViewModel"
    }
}

sealed class UiState() {
    data class Success(val data: List<UserUiModel>) : UiState()
    object Error : UiState()
    object Loading : UiState()
}

data class UserUiModel(
    val userId: Int,
    val displayName: String,
    val reputation: Int,
    val profileImageUrl: String,
    val profileImage: ImageBitmap? = null,
    val isFollowed: Boolean = false
)


package com.example.topstackoverusers.viewmodel

import android.util.Log
import androidx.compose.ui.graphics.asImageBitmap
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.topstackoverusers.data.remote.models.StackOverFlowUser
import com.example.topstackoverusers.data.repository.StackOverFlowRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.supervisorScope
import kotlinx.coroutines.withContext

class HomeViewModel(
    private val repository: StackOverFlowRepository
) : ViewModel() {

    private val _errorState: MutableStateFlow<Boolean> = MutableStateFlow(false)

    private val _userCache: MutableStateFlow<Map<Int, StackOverFlowUser>> = MutableStateFlow(emptyMap())

    val uiState: StateFlow<UiState> = combine(
        _userCache,
        repository.followedState,
        _errorState
    ) { cache, followedState, errorState ->
        val list = cache.values.map { item ->
            item.copy(
                isFollowed = followedState[item.userId] == true
            )
        }
        when {
            errorState -> UiState.Error
            list.isEmpty() -> UiState.Loading
            else -> UiState.Success(list)
        }
    }
        .catch {
            emit(UiState.Error)
        }
        .stateIn(
            viewModelScope,
            SharingStarted.Lazily,
            UiState.Loading
        )

    fun loadUsers() {
        viewModelScope.launch {
            try {
                val usersResponse = repository.getTopUsers()
                // Initialize the cache with API users
                val initialMap = usersResponse.stackOverItems.associateBy { it.userId }
                _userCache.value = initialMap


                // Load images in parallel
                supervisorScope {
                    usersResponse.stackOverItems.map { item ->
                        async {
                            try {
                                loadImage(userId = item.userId)
                            } catch (e: Exception) {
                                _errorState.value = true
                                Log.e(TAG, "Image load failed", e)
                                item
                            }
                        }
                    }.awaitAll()
                }

            } catch (e: Exception) {
                _errorState.value = true
                Log.e(TAG, "API call failed", e)
            }
        }
    }

    fun followUser(userId: Int, isFollowed: Boolean) = viewModelScope.launch(Dispatchers.IO) {
        try {
            repository.toggleFollowedState(userId, isFollowed)
        } catch (_: Exception) {
            _errorState.value = true
        }
    }

    private suspend fun loadImage(userId: Int) {
        val cachedItem = _userCache.value[userId] ?: return

        // If image is already loaded, skip
        if (cachedItem.profileImage != null) return

        val bitmap = withContext(Dispatchers.IO) {
            repository.loadImage(cachedItem.profileImageUrl)?.asImageBitmap()
        }

        // Create a new object with the bitmap
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
    data class Success(val data: List<StackOverFlowUser>) : UiState()
    object Error : UiState()
    object Loading : UiState()
}
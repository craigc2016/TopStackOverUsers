package com.example.topstackoverusers.viewmodel

import android.util.Log
import androidx.compose.ui.graphics.asImageBitmap
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.topstackoverusers.data.remote.models.StackOverFlowItem
import com.example.topstackoverusers.data.repository.StackOverFlowRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.supervisorScope
import kotlinx.coroutines.withContext

class HomeViewModel(
    private val repository: StackOverFlowRepository
) : ViewModel() {

    private val userCache: MutableMap<Int, StackOverFlowItem> = mutableMapOf()

    private val _uiState = MutableStateFlow<UiState>(UiState.Loading)
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    fun loadUsers() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val usersResponse = repository.getTopUsers()

                // Load images in parallel
                supervisorScope {
                    usersResponse.stackOverItems.map { item ->
                        async {
                            val updatedItem = try {
                                loadImage(item)
                            } catch (e: Exception) {
                                Log.e("HomeViewModel", "Image load failed", e)
                                item
                            }

                            val initialItems = usersResponse.stackOverItems.map { item ->
                                userCache[item.userId] ?: item.copy(profileImage = null)
                            }
                            _uiState.value = UiState.Success(initialItems)

                            // Cache updated user
                            userCache[updatedItem.userId] = updatedItem

                            _uiState.update { state ->
                                if (state is UiState.Success) {
                                    val newData = state.data.map { existing ->
                                        if (existing.userId == updatedItem.userId) updatedItem
                                        else existing
                                    }
                                    state.copy(data = newData)
                                } else state
                            }
                        }
                    }.awaitAll()
                }

            } catch (e: Exception) {
                Log.e("HomeViewModel", "API call failed", e)
                _uiState.value = UiState.Error
            }
        }
    }

    private suspend fun loadImage(item: StackOverFlowItem) : StackOverFlowItem {
        // If cached item and has image return
        userCache[item.userId]?.profileImage?.let { cachedBitmap ->
            return item.copy(profileImage = cachedBitmap)
        }

        return withContext(Dispatchers.Default) {
            try {
                val bitmap = repository.loadImage(item.profileImageUrl)
                item.copy(profileImage = bitmap?.asImageBitmap())
            } catch (e: Exception) {
                Log.e(TAG, e.message.toString())
                item
            }
        }
    }

    companion object {
        private val TAG = "HomeViewModel"
    }
}

sealed class UiState() {
    data class Success(val data: List<StackOverFlowItem>) : UiState()
    object Error : UiState()
    object Loading : UiState()
}
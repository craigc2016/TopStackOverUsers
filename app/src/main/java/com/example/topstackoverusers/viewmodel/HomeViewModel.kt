package com.example.topstackoverusers.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.topstackoverusers.data.remote.models.StackOverFlowItem
import com.example.topstackoverusers.data.repository.StackOverFlowRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class HomeViewModel(
    private val repository: StackOverFlowRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow<UiState>(UiState.Loading)
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    fun loadUsers() {
        viewModelScope.launch {
            try {
                val result = repository.getTopUsers()
                _uiState.value = UiState.Success(result.stackOverItems)
            } catch (e: Exception) {
                Log.e("HomeViewModel", "API call failed", e)
                _uiState.value = UiState.Error
            }
        }
    }
}

sealed class UiState() {
    data class Success(val data: List<StackOverFlowItem>) : UiState()
    object Error : UiState()
    object Loading : UiState()
}
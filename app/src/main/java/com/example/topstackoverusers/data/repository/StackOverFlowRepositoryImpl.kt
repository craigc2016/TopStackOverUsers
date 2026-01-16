package com.example.topstackoverusers.data.repository

import com.example.topstackoverusers.data.remote.ApiService
import com.example.topstackoverusers.data.remote.models.StackOverFlowResponse

interface StackOverFlowRepository{
    suspend fun getTopUsers(): StackOverFlowResponse
    suspend fun loadImage()
}

class StackOverFlowRepositoryImpl(private val apiService: ApiService) : StackOverFlowRepository {

    override suspend fun getTopUsers(): StackOverFlowResponse {
        return apiService.getTopUsers()
    }

    override suspend fun loadImage() {
        TODO("Not yet implemented")
    }
}
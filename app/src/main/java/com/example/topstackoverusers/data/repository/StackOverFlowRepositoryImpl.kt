package com.example.topstackoverusers.data.repository

import android.graphics.Bitmap
import com.example.topstackoverusers.data.remote.ApiService
import com.example.topstackoverusers.data.remote.ImageService
import com.example.topstackoverusers.data.remote.models.StackOverFlowResponse

interface StackOverFlowRepository{
    suspend fun getTopUsers(): StackOverFlowResponse
    suspend fun loadImage(url: String) : Bitmap?
}

class StackOverFlowRepositoryImpl(
    private val apiService: ApiService,
    private val imageService: ImageService
) : StackOverFlowRepository {

    override suspend fun getTopUsers(): StackOverFlowResponse {
        return apiService.getTopUsers()
    }

    override suspend fun loadImage(url: String) : Bitmap? {
        return imageService.loadImage(url)
    }
}
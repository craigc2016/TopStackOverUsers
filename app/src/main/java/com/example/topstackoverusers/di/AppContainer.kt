package com.example.topstackoverusers.di

import com.example.topstackoverusers.data.remote.ApiService
import com.example.topstackoverusers.data.remote.AppRoutes
import com.example.topstackoverusers.data.repository.StackOverFlowRepository
import com.example.topstackoverusers.data.repository.StackOverFlowRepositoryImpl
import com.example.topstackoverusers.viewmodel.HomeViewModel
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import retrofit2.Retrofit



class AppContainer {

    private val json = Json {
        ignoreUnknownKeys = true // important for APIs that return extra fields
    }

    @OptIn(ExperimentalSerializationApi::class)
    private val retrofit: Retrofit by lazy {
        val contentType = "application/json".toMediaType()
        Retrofit.Builder()
            .baseUrl(AppRoutes.BASE_URL)
            .addConverterFactory(json.asConverterFactory(contentType))
            .build()
    }

    val apiService: ApiService by lazy {
        retrofit.create(ApiService::class.java)
    }

    val repository: StackOverFlowRepository by lazy {
        StackOverFlowRepositoryImpl(apiService)
    }

    val homeViewModel: HomeViewModel by lazy {
        HomeViewModel(repository)
    }
}
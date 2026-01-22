package com.example.topstackoverusers.di

import android.app.Application
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStoreFile
import com.example.topstackoverusers.data.local.UserPreferencesDataStore
import com.example.topstackoverusers.data.remote.ApiService
import com.example.topstackoverusers.data.remote.AppRoutes
import com.example.topstackoverusers.data.remote.ImageService
import com.example.topstackoverusers.data.remote.ImageServiceImpl
import com.example.topstackoverusers.data.repository.StackOverFlowRepository
import com.example.topstackoverusers.data.repository.StackOverFlowRepositoryImpl
import com.example.topstackoverusers.domain.ImageDecoder
import com.example.topstackoverusers.domain.LoadImageUseCase
import com.example.topstackoverusers.viewmodel.HomeViewModel
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import retrofit2.Retrofit


class AppContainer(application: Application) {

    private val json = Json {
        ignoreUnknownKeys = true
    }

    private val ioDispatcher = Dispatchers.IO

    val dataStore: DataStore<Preferences> =
        PreferenceDataStoreFactory.create(
            produceFile = {
                application.applicationContext.preferencesDataStoreFile("user_prefs")
            }
        )

    val userPreferencesDataStore = UserPreferencesDataStore(dataStore)

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

    val imageService: ImageService by lazy {
        ImageServiceImpl()
    }

    val repository: StackOverFlowRepository by lazy {
        StackOverFlowRepositoryImpl(apiService, imageService, userPreferencesDataStore)
    }

    val imageDecoder by lazy {
        ImageDecoder()
    }

    val loadImageUseCase by lazy {
        LoadImageUseCase(repository, imageDecoder)
    }

    val homeViewModel: HomeViewModel by lazy {
        HomeViewModel(
            repository = repository,
            loadImageUseCase = loadImageUseCase,
            ioDispatcher = ioDispatcher
        )
    }
}
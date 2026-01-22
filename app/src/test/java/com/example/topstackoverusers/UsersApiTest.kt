package com.example.topstackoverusers

import com.example.topstackoverusers.data.remote.ApiService
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import retrofit2.Retrofit

@RunWith(JUnit4::class)
class UsersApiTest {
    private lateinit var mockWebServer: MockWebServer
    private lateinit var userApiService: ApiService

    @Before
    fun setup() {
        mockWebServer = MockWebServer()
        mockWebServer.start()

        // set the json serializer to ignore unknown keys model only supports 4 values
        val json = Json {
            ignoreUnknownKeys = true
        }

        // Get the base URL of the mock server
        val baseUrl = mockWebServer.url("/")
        val contentType = "application/json".toMediaType()

        val retrofit = Retrofit.Builder()
            .baseUrl(baseUrl)
            .addConverterFactory(json.asConverterFactory(contentType))
            .build()

        userApiService = retrofit.create(ApiService::class.java)
    }

    @After
    fun tearDown() {
        mockWebServer.shutdown()
    }

    @Test
    fun getUsers_returnsSuccessResponse() = runTest {
        val jsonResponse = getUserJson()
        mockWebServer.enqueue(MockResponse().setBody(jsonResponse).setResponseCode(200))

        val response = userApiService.getTopUsers()
        assert(response.stackOverItems.isNotEmpty())

        // User values
        val user = response.stackOverItems.first()
        assert(user.reputation == 1454978)
        assert(user.userId == 22656)
        assert(user.profileImageUrl == "https://www.gravatar.com/avatar/6d8ebb117e8d83d74ea95fbdd0f87e13?s=256&d=identicon&r=PG")
        assert(user.displayName == "Jon Skeet")
    }

    @Test
    fun getUsers_returnsEmptyResponse() = runTest {
        val jsonResponse = """
            {
              "items": []
            }
        """.trimIndent()
        mockWebServer.enqueue(MockResponse().setBody(jsonResponse).setResponseCode(200))

        val response = userApiService.getTopUsers()
        assert(response.stackOverItems.isEmpty())
    }

    private fun getUserJson() : String {
        return """
            {
              "items": [
                {
                  "reputation": 1454978,
                  "user_id": 22656,
                  "link": "https://stackoverflow.com/users/22656/jon-skeet",
                  "profile_image": "https://www.gravatar.com/avatar/6d8ebb117e8d83d74ea95fbdd0f87e13?s=256&d=identicon&r=PG",
                  "display_name": "Jon Skeet"
                }
              ]
            }
        """.trimIndent()
    }
}
package com.example.topstackoverusers.data.remote

import com.example.topstackoverusers.data.remote.models.StackOverFlowResponse
import retrofit2.http.GET

interface ApiService {

    @GET("/2.2/users?page=1&pagesize=20&order=desc&sort=reputation&site=stackoverflow")
    suspend fun getTopUsers(): StackOverFlowResponse

}

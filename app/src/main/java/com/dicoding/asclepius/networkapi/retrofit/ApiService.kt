package com.dicoding.asclepius.networkapi.retrofit

import com.dicoding.asclepius.networkapi.response.ArticlesResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiService {
    @GET("v2/top-headlines")
    suspend fun getTopHeadlines(
        @Query("q") query: String,
        @Query("category") category: String,
        @Query("language") language: String,
    ): Response<ArticlesResponse>
}
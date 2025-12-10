package com.example.playlistmaker.network

import com.example.playlistmaker.SearchResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface ITunesApi {
    @GET("/search?entity=song")
    suspend fun searchTracks(
        @Query("term") text: String
    ): Response<SearchResponse>
}

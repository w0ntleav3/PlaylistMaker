package com.example.playlistmaker

// модель для трека
data class Track(
    val trackName: String,
    val artistName: String,
    val trackTimeMillis: Long,
    val artworkUrl100: String
)

// модель для ответа сервера
data class SearchResponse(
    val resultCount: Int,
    val results: List<Track>
)

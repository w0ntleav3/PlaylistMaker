package com.example.playlistmaker

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName
import java.io.Serializable

@Entity(tableName = "favorite_tracks_table")
data class Track(
    @PrimaryKey
    val trackId: Long,
    val trackName: String,
    val artistName: String,
    val trackTimeMillis: Long?,
    val artworkUrl100: String,
    val collectionName: String?,
    val releaseDate: String?,
    val primaryGenreName: String?,
    val country: String?,

    @SerializedName("previewUrl")
    val previewUrl: String?,


    val addTime: Long = System.currentTimeMillis()
) : Serializable {
    fun getCoverArtwork(): String = artworkUrl100.replaceAfterLast('/', "512x512bb.jpg")
}

data class SearchResponse(
    val resultCount: Int,
    val results: List<Track>
)
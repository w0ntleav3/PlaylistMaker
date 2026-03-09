package com.example.playlistmaker.db

import androidx.room.Entity

@Entity(
    tableName = "playlist_track_table",
    primaryKeys = ["playlistId", "trackId"]
)
data class PlaylistTrackEntity(
    val playlistId: Int,
    val trackId: Int
)
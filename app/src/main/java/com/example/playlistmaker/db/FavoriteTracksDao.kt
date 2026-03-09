package com.example.playlistmaker.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.playlistmaker.Track

@Dao
interface FavoriteTracksDao {

    // Добавляем трек. Если он уже был, заменяем (на всякий случай)
    @Insert(onConflict = OnConflictStrategy.Companion.REPLACE)
    suspend fun insertTrack(track: Track)

    // Удаляем конкретный трек
    @Delete
    suspend fun deleteTrack(track: Track)

    // Получаем все треки, сортируя их по времени добавления (новые сверху)
    @Query("SELECT * FROM favorite_tracks_table ORDER BY addTime DESC")
    suspend fun getFavoriteTracks(): List<Track>

    // Проверяем, есть ли трек в избранном по его ID
    @Query("SELECT EXISTS(SELECT 1 FROM favorite_tracks_table WHERE trackId = :id)")
    suspend fun isFavorite(id: Long): Boolean
}
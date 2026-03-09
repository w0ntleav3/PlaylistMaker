package com.example.playlistmaker.db
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.playlistmaker.db.PlaylistEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface PlaylistDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPlaylist(playlist: PlaylistEntity): Long // ДОБАВЬ ЭТО

    @Query("SELECT * FROM playlist_table ORDER BY id DESC")
    fun getAllPlaylists(): Flow<List<PlaylistEntity>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun addTrackToPlaylist(link: PlaylistTrackEntity)


    @Query("SELECT EXISTS(SELECT 1 FROM playlist_track_table WHERE playlistId = :playlistId AND trackId = :trackId)")
    suspend fun isTrackInPlaylist(
        playlistId: Int,
        trackId: Int
    ): Boolean

    @Update
    suspend fun updatePlaylist(playlist: PlaylistEntity)
}
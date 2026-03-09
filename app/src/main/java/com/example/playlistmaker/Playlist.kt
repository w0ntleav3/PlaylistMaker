import com.example.playlistmaker.db.PlaylistEntity

data class Playlist(
    val id: Int,
    val name: String,
    val description: String?,
    val imagePath: String?,
    val tracksCount: Int,


)
private fun mapEntityToPlaylist(entity: PlaylistEntity): Playlist {
    return Playlist(
        id = entity.id,
        name = entity.name,
        description = entity.description,
        imagePath = entity.imagePath,
        tracksCount = entity.tracksCount
    )
}

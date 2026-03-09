package com.example.playlistmaker

import Playlist
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.playlistmaker.databinding.ItemPlaylistBinding
import java.io.File

class PlaylistsAdapter(
    private val playlists: MutableList<Playlist>,
    private val isGridView: Boolean = true,
    private val onClick: (Playlist) -> Unit
) : RecyclerView.Adapter<PlaylistsAdapter.PlaylistViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlaylistViewHolder {
        val layout = if (isGridView) R.layout.item_playlist else R.layout.item_playlist_small
        val view = LayoutInflater.from(parent.context).inflate(layout, parent, false)
        return PlaylistViewHolder(view)
    }

    inner class PlaylistViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        private val playlistImage: ImageView = view.findViewById(R.id.playlist_image)
        private val playlistName: TextView = view.findViewById(R.id.playlist_name)
        private val tracksCount: TextView = view.findViewById(R.id.tracks_count)

        fun bind(playlist: Playlist) {
            playlistName.text = playlist.name
            tracksCount.text = formatTracksCount(playlist.tracksCount)

            itemView.setOnClickListener {
                onClick(playlist)
            }

            if (playlist.imagePath.isNullOrEmpty()) {
                playlistImage.setImageResource(R.drawable.placeholder_player)
            } else {

                playlistImage.setImageURI(Uri.fromFile(File(playlist.imagePath)))
            }
        }

        private fun formatTracksCount(count: Int): String {
            val lastDigit = count % 10
            val lastTwoDigits = count % 100

            return when {
                lastTwoDigits in 11..14 -> "$count треков"
                lastDigit == 1 -> "$count трек"
                lastDigit in 2..4 -> "$count трека"
                else -> "$count треков"
            }
        }
    }

    fun updateData(newList: List<Playlist>) {
        playlists.clear()
        playlists.addAll(newList)
        notifyDataSetChanged()
    }


    override fun onBindViewHolder(holder: PlaylistViewHolder, position: Int) {
        holder.bind(playlists[position])
    }

    override fun getItemCount() = playlists.size
}

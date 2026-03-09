package com.example.playlistmaker

import Playlist
import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.playlistmaker.databinding.ItemPlaylistBinding
import java.io.File

class PlaylistsAdapter(
    private val playlists: MutableList<Playlist>,
    private val onClick: (Playlist) -> Unit
) : RecyclerView.Adapter<PlaylistsAdapter.PlaylistViewHolder>() {

    inner class PlaylistViewHolder(
        private val binding: ItemPlaylistBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(playlist: Playlist) {
            binding.playlistName.text = playlist.name
            binding.tracksCount.text = formatTracksCount(playlist.tracksCount)

            binding.root.setOnClickListener {
                onClick(playlist)
            }

            if (playlist.imagePath.isNullOrEmpty()) {
                binding.playlistImage.setImageResource(R.drawable.placeholder_player)
            } else {
                binding.playlistImage.setImageURI(Uri.fromFile(File(playlist.imagePath)))
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

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlaylistViewHolder {
        val binding = ItemPlaylistBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PlaylistViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PlaylistViewHolder, position: Int) {
        holder.bind(playlists[position])
    }

    override fun getItemCount() = playlists.size
}

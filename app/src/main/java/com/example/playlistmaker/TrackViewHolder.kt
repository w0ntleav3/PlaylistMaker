package com.example.playlistmaker

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.playlistmaker.utils.formatTrackTime

class TrackViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    private val trackName = itemView.findViewById<TextView>(R.id.trackName)
    private val artistName = itemView.findViewById<TextView>(R.id.artistName)
    private val trackTime = itemView.findViewById<TextView>(R.id.trackTime)
    private val artwork = itemView.findViewById<ImageView>(R.id.trackArtwork)

    fun bind(track: Track) {
        trackName.text = track.trackName
        artistName.text = track.artistName
        trackTime.text = formatTrackTime(track.trackTimeMillis)

        Glide.with(itemView)
            .load(track.artworkUrl100)
            .placeholder(R.drawable.placeholder)
            .error(R.drawable.placeholder)
            .centerCrop()
            .transform(RoundedCorners(12))
            .into(artwork)
    }
}

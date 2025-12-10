package com.example.playlistmaker

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.bitmap.RoundedCorners

class HistoryAdapter(
    private var items: MutableList<Track>
) : RecyclerView.Adapter<HistoryAdapter.HistoryViewHolder>() {

    var onItemClick: ((Track) -> Unit)? = null
    inner class HistoryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val trackName: TextView = itemView.findViewById(R.id.trackName)
        private val artistName: TextView = itemView.findViewById(R.id.artistName)
        private val artwork: ImageView = itemView.findViewById(R.id.trackArtwork)
        private val trackTime: TextView = itemView.findViewById(R.id.trackTime)

        fun bind(track: Track) {
            trackName.text = track.trackName
            artistName.text = track.artistName
            trackTime.text = track.trackTime
            Glide.with(itemView).load(track.artworkUrl100)
                .placeholder(R.drawable.placeholder_image)
                .error(R.drawable.placeholder_image)
                .centerCrop()
                .transform(RoundedCorners(12))
                .into(artwork)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HistoryViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_history_track, parent, false)
        return HistoryViewHolder(view)
    }

    override fun getItemCount(): Int = items.size

    override fun onBindViewHolder(holder: HistoryViewHolder, position: Int) {
        holder.bind(items[position])

        // добавляем клик на элемент
        holder.itemView.setOnClickListener {
            onItemClick?.invoke(items[position])
        }
    }

    // твой метод update
    fun update(newList: List<Track>) {
        items.clear()
        items.addAll(newList)
        notifyDataSetChanged()
    }
}

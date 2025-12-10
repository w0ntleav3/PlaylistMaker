import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.playlistmaker.Track
import com.example.playlistmaker.R


class TrackAdapter(private val tracks: List<Track>) :
    RecyclerView.Adapter<TrackAdapter.TrackViewHolder>() {

    class TrackViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val artwork: ImageView = itemView.findViewById(R.id.trackArtwork)
        private val name: TextView = itemView.findViewById(R.id.trackName)
        private val artist: TextView = itemView.findViewById(R.id.artistName)
        private val time: TextView = itemView.findViewById(R.id.trackTime)

        fun bind(track: Track) {
            name.text = track.trackName
            artist.text = track.artistName
            time.text = track.trackTime

            Glide.with(itemView)
                .load(track.artworkUrl100)
                .placeholder(R.drawable.placeholder_image)
                .centerCrop()
                .into(artwork)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TrackViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_track, parent, false)
        return TrackViewHolder(view)
    }

    override fun onBindViewHolder(holder: TrackViewHolder, position: Int) {
        holder.bind(tracks[position])
    }

    override fun getItemCount(): Int = tracks.size
}

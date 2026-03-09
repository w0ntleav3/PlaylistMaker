package com.example.playlistmaker

import android.media.MediaPlayer
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.room.Room
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.playlistmaker.databinding.ActivityPlayerBinding
import com.example.playlistmaker.db.AppDatabase
import com.example.playlistmaker.utils.formatTrackTime
import com.google.gson.Gson
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Locale

class PlayerActivity : AppCompatActivity() {
    private var isFavorite: Boolean = false
    private lateinit var db: AppDatabase
    companion object {
        private const val STATE_DEFAULT = 0
        private const val STATE_PREPARED = 1
        private const val STATE_PLAYING = 2
        private const val STATE_PAUSED = 3
        private const val TIMER_DELAY = 300L
    }

    private lateinit var binding: ActivityPlayerBinding
    private var mediaPlayer = MediaPlayer()
    private var playerState = STATE_DEFAULT
    private val handler = Handler(Looper.getMainLooper())

    private val updateTimeRunnable = object : Runnable {
        override fun run() {
            if (playerState == STATE_PLAYING) {
                binding.progress.text = SimpleDateFormat("mm:ss", Locale.getDefault())
                    .format(mediaPlayer.currentPosition)
                handler.postDelayed(this, TIMER_DELAY)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPlayerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val json = intent.getStringExtra("track")
        val track = Gson().fromJson(json, Track::class.java)

        setupUI(track)
        preparePlayer(track.previewUrl)

        binding.backButton.setOnClickListener { finish() }

        binding.playButton.setOnClickListener {
            playbackControl()
        }

        db = AppDatabase.getInstance(this)

        // проверяем, в избранном ли трек
        lifecycleScope.launch {
            isFavorite = db.favoriteTracksDao().isFavorite(track.trackId)
            setFavoriteButtonIcon(isFavorite) // меняем иконку
        }

        binding.favoriteButton.setOnClickListener {
            onFavoriteClicked(track)
        }

        binding.addToPlaylistButton.setOnClickListener {
            val bottomSheet = PlaylistsBottomSheetFragment.newInstance(track)
            bottomSheet.show(supportFragmentManager, "playlists_sheet")
        }
    }

    private fun setupUI(track: Track) {
        binding.trackName.text = track.trackName
        binding.artistName.text = track.artistName
        binding.durationValue.text = formatTrackTime(track.trackTimeMillis ?: 0L)
        binding.genreValue.text = track.primaryGenreName
        binding.countryValue.text = track.country
        binding.yearValue.text = track.releaseDate?.take(4) ?: ""

        if (track.collectionName.isNullOrEmpty()) {
            binding.albumLabel.visibility = View.GONE
            binding.albumValue.visibility = View.GONE
        } else {
            binding.albumLabel.visibility = View.VISIBLE
            binding.albumValue.visibility = View.VISIBLE
            binding.albumValue.text = track.collectionName
        }

        binding.progress.text = getString(R.string.player_stub_time)

        val radius = (8 * resources.displayMetrics.density).toInt()
        Glide.with(this)
            .load(track.getCoverArtwork())
            .placeholder(R.drawable.placeholder_player)
            .centerCrop()
            .transform(RoundedCorners(radius))
            .into(binding.coverImage)
    }

    private fun preparePlayer(previewUrl: String?) {
        if (previewUrl.isNullOrEmpty()) return
        mediaPlayer.setDataSource(previewUrl)
        mediaPlayer.prepareAsync()
        mediaPlayer.setOnPreparedListener {
            playerState = STATE_PREPARED
        }
        mediaPlayer.setOnCompletionListener {
            playerState = STATE_PREPARED
            binding.playButton.setImageResource(R.drawable.ic_play)
            handler.removeCallbacks(updateTimeRunnable)
            binding.progress.text = getString(R.string.player_stub_time)
        }
    }

    private fun playbackControl() {
        when (playerState) {
            STATE_PLAYING -> pausePlayer()
            STATE_PREPARED, STATE_PAUSED -> startPlayer()
        }
    }

    private fun startPlayer() {
        mediaPlayer.start()
        binding.playButton.setImageResource(R.drawable.ic_pause)
        playerState = STATE_PLAYING
        handler.post(updateTimeRunnable)
    }

    private fun pausePlayer() {
        mediaPlayer.pause()
        binding.playButton.setImageResource(R.drawable.ic_play)
        playerState = STATE_PAUSED
        handler.removeCallbacks(updateTimeRunnable)
    }

    override fun onPause() {
        super.onPause()
        pausePlayer()
    }

    override fun onDestroy() {
        super.onDestroy()
        handler.removeCallbacks(updateTimeRunnable)
        mediaPlayer.release()
    }

    private fun onFavoriteClicked(track: Track) {
        lifecycleScope.launch {
            if (isFavorite) {
                db.favoriteTracksDao().deleteTrack(track)
                isFavorite = false
            } else {
                val favoriteTrack = track.copy(addTime = System.currentTimeMillis())
                db.favoriteTracksDao().insertTrack(favoriteTrack)
                isFavorite = true
            }
            setFavoriteButtonIcon(isFavorite)
        }
    }

    private fun setFavoriteButtonIcon(isFavorite: Boolean) {
        val imageResource = if (isFavorite) {
            R.drawable.ic_favourite_checked // подсвеченное сердечко
        } else {
            R.drawable.ic_favourite // контур
        }
        binding.favoriteButton.setImageResource(imageResource)
    }
}
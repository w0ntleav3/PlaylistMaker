package com.example.playlistmaker

import android.media.MediaPlayer
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.playlistmaker.databinding.ActivityPlayerBinding
import com.example.playlistmaker.utils.formatTrackTime
import com.google.gson.Gson
import java.text.SimpleDateFormat
import java.util.Locale

class PlayerActivity : AppCompatActivity() {

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

    // runnable для обновления текста с текущим временем проигрывания
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
        android.util.Log.d("PLAYER_DEBUG", "Preview URL: $previewUrl")
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
}
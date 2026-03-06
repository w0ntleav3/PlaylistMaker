package com.example.playlistmaker

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.playlistmaker.databinding.ActivityPlayerBinding
import com.example.playlistmaker.utils.formatTrackTime // проверь свой путь к этой функции!
import com.google.gson.Gson

class PlayerActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPlayerBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPlayerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val json = intent.getStringExtra("track")
        if (json == null) {
            finish()
            return
        }

        // теперь десериализуем в правильный класс Track
        val track = Gson().fromJson(json, Track::class.java)

        setupUI(track)
        setupListeners()
    }

    private fun setupUI(track: Track) {
        binding.trackName.text = track.trackName
        binding.artistName.text = track.artistName

        // форматируем время (используй свою функцию formatTrackTime)
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

        // безопасный радиус закругления
        val radius = (8 * resources.displayMetrics.density).toInt()

        Glide.with(this)
            .load(track.getCoverArtwork())
            .placeholder(R.drawable.placeholder_player)
            .centerCrop()
            .transform(RoundedCorners(radius))
            .into(binding.coverImage)
    }

    private fun setupListeners() {
        binding.backButton.setOnClickListener { finish() }
    }


}
package com.example.playlistmaker

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.playlistmaker.databinding.ActivityMediaBinding
import com.google.android.material.tabs.TabLayoutMediator

class MediaActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMediaBinding
    private lateinit var mediator: TabLayoutMediator

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMediaBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val adapter = MediaViewPagerAdapter(supportFragmentManager, lifecycle)
        binding.viewPager.adapter = adapter

        mediator = TabLayoutMediator(binding.tabLayout, binding.viewPager) { tab, position ->
            tab.text = if (position == 0) "Избранные треки" else "Плейлисты"
        }
        mediator.attach()
        binding.btnBackFromMedia.setOnClickListener { finish() }
    }

    override fun onDestroy() {
        super.onDestroy()
        mediator.detach()
    }
}
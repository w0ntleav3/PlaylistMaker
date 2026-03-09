package com.example.playlistmaker

import TrackAdapter
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.playlistmaker.databinding.FragmentFavoritesBinding
import com.example.playlistmaker.db.AppDatabase
import com.google.gson.Gson
import kotlinx.coroutines.launch

class FavoritesFragment : Fragment() {

    private var _binding: FragmentFavoritesBinding? = null
    private val binding get() = _binding!!

    private val adapter = TrackAdapter().apply {
        onItemClick = { track ->
            openPlayer(track)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFavoritesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.favoritesRecycler.layoutManager = LinearLayoutManager(requireContext())
        binding.favoritesRecycler.adapter = adapter
    }

    override fun onResume() {
        super.onResume()
        loadFavorites()
    }

    private fun loadFavorites() {
        viewLifecycleOwner.lifecycleScope.launch {
            // Используем синглтон вместо создания новой БД
            val db = AppDatabase.getInstance(requireContext())
            val tracks = db.favoriteTracksDao().getFavoriteTracks()

            if (tracks.isEmpty()) {
                binding.emptyPlaceholder.isVisible = true
                binding.favoritesRecycler.isVisible = false
            } else {
                binding.emptyPlaceholder.isVisible = false
                binding.favoritesRecycler.isVisible = true
                adapter.update(tracks)
            }
        }
    }

    private fun openPlayer(track: Track) {
        val intent = Intent(requireContext(), PlayerActivity::class.java)
        intent.putExtra("track", Gson().toJson(track))
        startActivity(intent)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        fun newInstance() = FavoritesFragment()
    }
}

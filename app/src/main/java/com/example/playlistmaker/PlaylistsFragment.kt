package com.example.playlistmaker

import Playlist
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.GridLayoutManager
import androidx.room.Room
import com.example.playlistmaker.databinding.FragmentPlaylistsBinding
import com.example.playlistmaker.db.AppDatabase
import com.example.playlistmaker.db.PlaylistEntity
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class PlaylistsFragment : Fragment() {

    private var _binding: FragmentPlaylistsBinding? = null
    private val binding get() = _binding!!


    private lateinit var db: AppDatabase

    private val playlistsList = mutableListOf<Playlist>()
    // добавляем лямбду в конце
    private val adapter = PlaylistsAdapter(playlistsList) { playlist ->
        // здесь будет логика открытия плейлиста, а пока просто лог
        Log.d("PlaylistsFragment", "нажали на: ${playlist.name}")
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentPlaylistsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        db = AppDatabase.getInstance(requireContext())

        binding.recyclerView.layoutManager = GridLayoutManager(requireContext(), 2)
        binding.recyclerView.adapter = adapter

        // ОСТАВЛЯЕМ ТОЛЬКО ОДНУ ПОДПИСКУ
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                db.playlistDao().getAllPlaylists().collectLatest { entities ->
                    Log.d("PlaylistsFragment", "Количество из базы: ${entities.size}")
                    val playlists = entities.map { mapEntityToPlaylist(it) }
                    render(playlists)
                }
            }
        }

        binding.btnNewPlaylist.setOnClickListener {
            // Используем commit() для перехода
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, NewPlaylistFragment.newInstance())
                .addToBackStack(null)
                .commit()
        }
    }

    private fun render(playlists: List<Playlist>) {
        if (playlists.isEmpty()) {
            binding.recyclerView.isVisible = false
            binding.emptyPlaceholder.isVisible = true
        } else {
            binding.emptyPlaceholder.isVisible = false
            binding.recyclerView.isVisible = true
            playlistsList.clear()
            playlistsList.addAll(playlists)
            adapter.notifyDataSetChanged()
        }
    }

    private fun mapEntityToPlaylist(entity: PlaylistEntity): Playlist {
        return Playlist(
            id = entity.id,
            name = entity.name,
            description = entity.description,
            imagePath = entity.imagePath,
            tracksCount = entity.tracksCount
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        fun newInstance() = PlaylistsFragment()
    }
}
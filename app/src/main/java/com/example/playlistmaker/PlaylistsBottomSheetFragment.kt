package com.example.playlistmaker

import Playlist
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.room.Room
import com.example.playlistmaker.databinding.FragmentPlaylistsBottomSheetBinding
import com.example.playlistmaker.db.AppDatabase
import com.example.playlistmaker.db.PlaylistTrackEntity
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.gson.Gson
import androidx.lifecycle.lifecycleScope
import com.example.playlistmaker.db.PlaylistEntity
import kotlinx.coroutines.launch


class PlaylistsBottomSheetFragment : BottomSheetDialogFragment() {

    private var _binding: FragmentPlaylistsBottomSheetBinding? = null
    private val binding get() = _binding!!
    private lateinit var track: Track
    private lateinit var db: AppDatabase


    override fun getTheme(): Int = R.style.AppBottomSheetDialogTheme
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val json = requireArguments().getString(TRACK_KEY)
        track = Gson().fromJson(json, Track::class.java)
    }
    private val adapter = PlaylistsAdapter(mutableListOf(), isGridView = false) { playlist ->
        onPlaylistClicked(playlist)
    }

    override fun onStart() {
        super.onStart()
        val dialog = dialog as? com.google.android.material.bottomsheet.BottomSheetDialog
        val bottomSheet = dialog?.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet)

        bottomSheet?.let {
            val behavior = com.google.android.material.bottomsheet.BottomSheetBehavior.from(it)


            val displayMetrics = requireContext().resources.displayMetrics
            val height = (displayMetrics.heightPixels * 0.5).toInt()
            bottomSheet.layoutParams.height = height


            it.layoutParams.height = height
            behavior.state = com.google.android.material.bottomsheet.BottomSheetBehavior.STATE_EXPANDED
            behavior.peekHeight = height
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentPlaylistsBottomSheetBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        db = AppDatabase.getInstance(requireContext())

        binding.playlistsRecyclerBottom.layoutManager = LinearLayoutManager(requireContext())

        binding.playlistsRecyclerBottom.adapter = adapter

        binding.btnNewPlaylistBottom.setOnClickListener {
            val containerId = if (requireActivity() is MainActivity) R.id.fragment_container else android.R.id.content

            parentFragmentManager.beginTransaction()
                .replace(containerId, NewPlaylistFragment.newInstance(track)) // ПЕРЕДАЕМ ТРЕК
                .addToBackStack(null)
                .commit()

            dismiss()
        }

        loadPlaylists()
    }

    private fun loadPlaylists() {
        lifecycleScope.launch {
            db.playlistDao().getAllPlaylists().collect { list ->

                adapter.updateData(list.map { mapEntityToPlaylist(it) })
            }
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

    private fun onPlaylistClicked(playlist: Playlist) {
        lifecycleScope.launch {
            val exists = db.playlistDao().isTrackInPlaylist(
                playlist.id,
                track.trackId.toInt()
            )

            if (exists) {
                Toast.makeText(context, "Трек уже добавлен в плейлист ${playlist.name}", Toast.LENGTH_SHORT).show()
            } else {
                db.playlistDao().addTrackToPlaylist(
                    PlaylistTrackEntity(playlistId = playlist.id, trackId = track.trackId.toInt())
                )

                val updatedEntity = PlaylistEntity(
                    id = playlist.id,
                    name = playlist.name,
                    description = playlist.description,
                    imagePath = playlist.imagePath,
                    tracksCount = playlist.tracksCount + 1
                )
                db.playlistDao().updatePlaylist(updatedEntity)

                Toast.makeText(context, "Добавлено в плейлист ${playlist.name}", Toast.LENGTH_SHORT).show()
                dismiss()
            }
        }
    }

    companion object {

        private const val TRACK_KEY = "track"

        fun newInstance(track: Track) = PlaylistsBottomSheetFragment().apply {
            arguments = Bundle().apply {
                putString(TRACK_KEY, Gson().toJson(track))
            }
        }
    }}
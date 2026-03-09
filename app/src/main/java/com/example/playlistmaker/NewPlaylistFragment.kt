package com.example.playlistmaker

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.room.Room
import com.example.playlistmaker.databinding.FragmentNewPlaylistBinding
import com.example.playlistmaker.db.AppDatabase
import com.example.playlistmaker.db.PlaylistEntity
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream

class NewPlaylistFragment : Fragment() {

    private var _binding: FragmentNewPlaylistBinding? = null
    private val binding get() = _binding!!

    private var imageUri: Uri? = null

    // База данных без Koin
    private lateinit var db: AppDatabase

    private val pickMedia = registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
        if (uri != null) {
            binding.playlistCover.setImageURI(uri)
            imageUri = uri
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentNewPlaylistBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        db = AppDatabase.getInstance(requireContext())
        binding.btnCreate.isEnabled = false

        binding.playlistCover.setOnClickListener {
            pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
        }

        binding.etPlaylistName.doOnTextChanged { text, _, _, _ ->
            binding.btnCreate.isEnabled = !text.isNullOrEmpty()
        }

        binding.btnCreate.setOnClickListener {
            val name = binding.etPlaylistName.text.toString()
            val description = binding.etPlaylistDescription.text.toString()

            lifecycleScope.launch {
                val savedImagePath = imageUri?.let { saveImageToInternalStorage(it) }

                val newPlaylist = PlaylistEntity(
                    name = name,
                    description = description,
                    imagePath = savedImagePath,
                    tracksCount = 0
                )

                db.playlistDao().insertPlaylist(newPlaylist)
                Log.d("NewPlaylistFragment", "Inserted playlist: $name")

                Toast.makeText(requireContext(), "плейлист $name создан", Toast.LENGTH_SHORT).show()
                parentFragmentManager.popBackStack()
            }
        }

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                confirmExit()
            }
        })

        binding.toolbar.setNavigationOnClickListener {
            confirmExit()
        }
    }

    private fun saveImageToInternalStorage(uri: Uri): String {
        val filePath = File(requireContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES), "my_photos")
        if (!filePath.exists()) filePath.mkdirs()

        val file = File(filePath, "cover_${System.currentTimeMillis()}.jpg")

        val inputStream = requireContext().contentResolver.openInputStream(uri)
        val outputStream = FileOutputStream(file)

        BitmapFactory
            .decodeStream(inputStream)
            .compress(Bitmap.CompressFormat.JPEG, 30, outputStream)

        return file.absolutePath
    }

    private fun confirmExit() {
        if (imageUri != null || binding.etPlaylistName.text?.isNotEmpty() == true || binding.etPlaylistDescription.text?.isNotEmpty() == true) {
            MaterialAlertDialogBuilder(requireContext(), R.style.AlertDialogTheme)
                .setTitle("Завершить создание плейлиста?")
                .setMessage("Все несохраненные данные будут потеряны")
                .setNegativeButton("Отмена") { _, _ -> }
                .setPositiveButton("Завершить") { _, _ -> parentFragmentManager.popBackStack() }
                .show()
        } else {
            parentFragmentManager.popBackStack()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        fun newInstance() = NewPlaylistFragment()
    }
}
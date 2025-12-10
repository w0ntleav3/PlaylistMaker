package com.example.playlistmaker

import TrackAdapter
import android.os.Bundle
import android.text.Editable
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.playlistmaker.network.ITunesApi
import com.example.playlistmaker.utils.formatTrackTime
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class SearchActivity : AppCompatActivity() {

    private lateinit var searchEdit: EditText
    private lateinit var clearBtn: ImageView
    private lateinit var backBtn: View
    private lateinit var historyContainer: View

    private lateinit var recyclerView: RecyclerView
    private lateinit var trackAdapter: TrackAdapter

    private lateinit var historyRecycler: RecyclerView
    private lateinit var historyTitle: TextView
    private lateinit var clearHistoryButton: Button
    private lateinit var historyAdapter: HistoryAdapter

    private lateinit var historyStorage: SearchHistoryStorage

    // плейсхолдеры
    private lateinit var placeholderContainer: View
    private lateinit var placeholderImage: ImageView
    private lateinit var placeholderText: TextView
    private lateinit var refreshButton: Button

    private var searchText = ""
    private var lastQuery: String? = null

    private val iTunesApi by lazy {
        Retrofit.Builder()
            .baseUrl("https://itunes.apple.com")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ITunesApi::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        historyStorage = SearchHistoryStorage(this)

        initViews()
        setupTrackRecycler()
        setupHistoryRecycler()
        setupSearch()
        setupBackButton()

        savedInstanceState?.getString("search_text_key")?.let {
            searchText = it
            searchEdit.setText(it)
            searchEdit.setSelection(it.length)
        }

        updateHistoryVisibility()
    }

    private fun initViews() {
        searchEdit = findViewById(R.id.searchEditText)
        clearBtn = findViewById(R.id.clearButton)
        backBtn = findViewById(R.id.btn_back)
        historyContainer = findViewById(R.id.historyContainer)
        recyclerView = findViewById(R.id.recyclerView)
        historyRecycler = findViewById(R.id.historyRecycler)
        historyTitle = findViewById(R.id.historyTitle)
        clearHistoryButton = findViewById(R.id.clearHistoryBtn)

        // плейсхолдеры
        placeholderContainer = findViewById(R.id.placeholderContainer)
        placeholderImage = findViewById(R.id.placeholderImage)
        placeholderText = findViewById(R.id.placeholderText)
        refreshButton = findViewById(R.id.refreshButton)

        clearBtn.visibility = View.GONE
        placeholderContainer.visibility = View.GONE
    }

    private fun setupTrackRecycler() {
        trackAdapter = TrackAdapter(mutableListOf())
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = trackAdapter

        trackAdapter.onItemClick = { track ->
            historyStorage.addTrack(track)
            updateHistoryVisibility()
        }
    }

    private fun setupHistoryRecycler() {
        historyAdapter = HistoryAdapter(historyStorage.getHistory().toMutableList())
        historyRecycler.layoutManager = LinearLayoutManager(this)
        historyRecycler.adapter = historyAdapter

        historyAdapter.onItemClick = { track ->
            historyStorage.addTrack(track)
            historyAdapter.update(historyStorage.getHistory())
        }

        clearHistoryButton.setOnClickListener {
            historyStorage.clearHistory()
            updateHistoryVisibility()
        }
    }

    private fun updateHistoryVisibility() {
        val history = historyStorage.getHistory()
        val show = searchEdit.text.isEmpty() && history.isNotEmpty()
        if (show) {
            historyContainer.visibility = View.VISIBLE
            recyclerView.visibility = View.GONE
            placeholderContainer.visibility = View.GONE
            historyAdapter.update(history)
        } else {
            historyContainer.visibility = View.GONE
        }
    }

    private fun hideHistory() {
        historyContainer.visibility = View.GONE
    }

    private fun setupSearch() {
        searchEdit.addTextChangedListener(object : android.text.TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                searchText = s.toString()
                clearBtn.visibility = if (searchText.isEmpty()) View.GONE else View.VISIBLE
                if (searchText.isEmpty()) updateHistoryVisibility()
            }
            override fun afterTextChanged(s: Editable?) {}
        })


        searchEdit.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                lastQuery = searchEdit.text.toString()
                performSearch(lastQuery!!)
                true
            } else false
        }

        clearBtn.setOnClickListener {
            searchEdit.setText("")
            recyclerView.visibility = View.GONE
            placeholderContainer.visibility = View.GONE
            updateHistoryVisibility()
        }
    }

    private fun setupBackButton() {
        backBtn.setOnClickListener { finish() }
    }

    private fun performSearch(query: String) {
        if (query.isBlank()) return

        // прячем все кроме плейсхолдера
        recyclerView.visibility = View.GONE
        historyContainer.visibility = View.GONE
        placeholderContainer.visibility = View.GONE

        lifecycleScope.launch {
            try {
                val response = iTunesApi.searchTracks(query)
                if (response.isSuccessful) {
                    val tracks = response.body()?.results ?: emptyList()
                    if (tracks.isEmpty()) {
                        showEmptyPlaceholder()
                    } else {
                        trackAdapter.update(tracks.map { track ->
                            track.copy(trackTimeMillis = track.trackTimeMillis)
                        })
                        recyclerView.visibility = View.VISIBLE
                        placeholderContainer.visibility = View.GONE
                    }
                } else {
                    showErrorPlaceholder()
                }
            } catch (e: Exception) {
                showErrorPlaceholder()
            }
        }
    }

    private fun showEmptyPlaceholder() {
        placeholderContainer.visibility = View.VISIBLE
        recyclerView.visibility = View.GONE
        historyContainer.visibility = View.GONE
        placeholderImage.setImageResource(R.drawable.placeholder_no_music)
        placeholderText.text = "Ничего не нашлось"
        refreshButton.visibility = View.GONE
    }

    private fun showErrorPlaceholder() {
        placeholderContainer.visibility = View.VISIBLE
        recyclerView.visibility = View.GONE
        historyContainer.visibility = View.GONE

        placeholderImage.setImageResource(R.drawable.placeholder_internet_error)
        placeholderText.text = "Загрузка не удалась.\nПроверьте подключение к интернету"
        placeholderText.setLineSpacing(16f, 1f) // добавляет расстояние между строками

        refreshButton.visibility = View.VISIBLE
        refreshButton.setOnClickListener {
            lastQuery?.let { performSearch(it) }
        }
    }



}

package com.example.playlistmaker

import TrackAdapter
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

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

    private var searchText = ""

    companion object {
        private const val SEARCH_TEXT_KEY = "search_text_key"
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

        // восстановление текста поиска
        savedInstanceState?.getString(SEARCH_TEXT_KEY)?.let {
            searchText = it
            searchEdit.setText(it)
            searchEdit.setSelection(it.length)
        }

        // сразу показать историю если она есть
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

        clearBtn.visibility = View.GONE
    }

    /* ----------- СПИСОК ТРЕКОВ ----------- */
    private fun setupTrackRecycler() {
        trackAdapter = TrackAdapter(mutableListOf())
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = trackAdapter

        // при клике на трек → добавляем в историю
        trackAdapter.onItemClick = { track ->
            historyStorage.addTrack(track)
            updateHistoryVisibility()
        }
    }

    /* ----------- ИСТОРИЯ ----------- */
    private fun setupHistoryRecycler() {
        historyAdapter = HistoryAdapter(historyStorage.getHistory().toMutableList())
        historyRecycler.layoutManager = LinearLayoutManager(this)
        historyRecycler.adapter = historyAdapter

        historyAdapter.onItemClick = { track ->
            historyStorage.addTrack(track) // поднимаем вверх
            historyAdapter.update(historyStorage.getHistory()) // обновляем список
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
            historyAdapter.update(history)
        } else {
            historyContainer.visibility = View.GONE
        }
    }


    private fun hideHistory() {
        historyContainer.visibility = View.GONE
    }


    /* ----------- ПОИСК ----------- */
    private fun setupSearch() {
        searchEdit.addTextChangedListener(object : TextWatcher {

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                searchText = s.toString()

                clearBtn.visibility = if (searchText.isEmpty()) View.GONE else View.VISIBLE

                if (searchText.isEmpty()) {
                    updateHistoryVisibility()
                    return
                }

                hideHistory()
                recyclerView.visibility = View.VISIBLE

                val filtered = tracksListExample.filter { track ->
                    track.trackName.startsWith(searchText, ignoreCase = true) ||
                            track.artistName.startsWith(searchText, ignoreCase = true)
                }

                trackAdapter.update(filtered)
            }

            override fun afterTextChanged(s: Editable?) {}
        })

        clearBtn.setOnClickListener {
            searchEdit.setText("")
        }
    }

    private fun setupBackButton() {
        backBtn.setOnClickListener { finish() }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putString(SEARCH_TEXT_KEY, searchText)
        super.onSaveInstanceState(outState)
    }


    /* ------------------ TEST DATA ------------------ */

    private val tracksListExample = arrayListOf(
        Track(1, "Smells Like Teen Spirit", "Nirvana", "5:01",
            "https://is5-ssl.mzstatic.com/image/thumb/Music115/v4/7b/58/c2/7b58c21a-2b51-2bb2-e59a-9bb9b96ad8c3/00602567924166.rgb.jpg/100x100bb.jpg"),

        Track(2, "Billie Jean", "Michael Jackson", "4:35",
            "https://is5-ssl.mzstatic.com/image/thumb/Music125/v4/3d/9d/38/3d9d3811-71f0-3a0e-1ada-3004e56ff852/827969428726.jpg/100x100bb.jpg"),

        Track(3, "Stayin' Alive", "Bee Gees", "4:10",
            "https://is4-ssl.mzstatic.com/image/thumb/Music115/v4/1f/80/1f/1f801fc1-8c0f-ea3e-d3e5-387c6619619e/16UMGIM86640.rgb.jpg/100x100bb.jpg"),

        Track(4, "Whole Lotta Love", "Led Zeppelin", "5:33",
            "https://is2-ssl.mzstatic.com/image/thumb/Music62/v4/7e/17/e3/7e17e33f-2efa-2a36-e916-7f808576cf6b/mzm.fyigqcbs.jpg/100x100bb.jpg"),

        Track(5, "Sweet Child O'Mine", "Guns N' Roses", "5:03",
            "https://is5-ssl.mzstatic.com/image/thumb/Music125/v4/a0/4d/c4/a04dc484-03cc-02aa-fa82-5334fcb4bc16/18UMGIM24878.rgb.jpg/100x100bb.jpg"))

}

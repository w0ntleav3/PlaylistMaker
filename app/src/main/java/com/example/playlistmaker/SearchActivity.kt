package com.example.playlistmaker

import TrackAdapter
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class SearchActivity : AppCompatActivity() {

    private lateinit var searchEdit: EditText
    private lateinit var clearBtn: ImageView
    private lateinit var backBtn: View

    // переменная для хранения текста
    private var searchText: String = ""

    companion object {
        private const val SEARCH_TEXT_KEY = "search_text_key"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        searchEdit = findViewById(R.id.searchEditText)
        clearBtn = findViewById(R.id.clearButton)
        backBtn = findViewById(R.id.btn_back)

        recyclerView = findViewById(R.id.recyclerView) // добавь в activity_search
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = TrackAdapter(tracks)

        // если есть сохранённые данные, восстанавливаем их
        savedInstanceState?.getString(SEARCH_TEXT_KEY)?.let { restoredText ->
            searchEdit.setText(restoredText)
            searchText = restoredText
            clearBtn.visibility = if (restoredText.isEmpty()) View.GONE else View.VISIBLE
        }

        setupBackButton()
        setupSearch()
    }

    private fun setupBackButton() {
        backBtn.setOnClickListener { finish() }
    }

    private fun setupSearch() {
        // TextWatcher для сохранения текста и управления кнопкой очистки
        searchEdit.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                searchText = s.toString()
                clearBtn.visibility = if (s.isNullOrEmpty()) View.GONE else View.VISIBLE
            }

            override fun afterTextChanged(s: Editable?) {}
        })

        // клик по EditText — ставим фокус и показываем клавиатуру
        searchEdit.setOnClickListener {
            searchEdit.requestFocus()
            showKeyboard(searchEdit)
        }

        // кнопка очистки
        clearBtn.setOnClickListener {
            searchEdit.text.clear()
            hideKeyboard()
            clearBtn.visibility = View.GONE
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        // сохраняем текст
        outState.putString(SEARCH_TEXT_KEY, searchText)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        // восстанавливаем текст
        savedInstanceState.getString(SEARCH_TEXT_KEY)?.let { restoredText ->
            searchEdit.setText(restoredText)
            searchText = restoredText
            clearBtn.visibility = if (restoredText.isEmpty()) View.GONE else View.VISIBLE
        }
    }

    private fun showKeyboard(view: View) {
        val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        imm.showSoftInput(view, InputMethodManager.SHOW_IMPLICIT)
    }

    private fun hideKeyboard() {
        val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(window.decorView.windowToken, 0)
    }

    private lateinit var recyclerView: RecyclerView
    val tracks = arrayListOf(
        Track(
            "Smells Like Teen Spirit",
            "Nirvana",
            "5:01",
            "https://is5-ssl.mzstatic.com/image/thumb/Music115/v4/7b/58/c2/7b58c21a-2b51-2bb2-e59a-9bb9b96ad8c3/00602567924166.rgb.jpg/100x100bb.jpg"
        ),
        Track(
            "Billie Jean",
            "Michael Jackson",
            "4:35",
            "https://is5-ssl.mzstatic.com/image/thumb/Music125/v4/3d/9d/38/3d9d3811-71f0-3a0e-1ada-3004e56ff852/827969428726.jpg/100x100bb.jpg"
        ),
        Track(
            "Stayin' Alive",
            "Bee Gees",
            "4:10",
            "https://is4-ssl.mzstatic.com/image/thumb/Music115/v4/1f/80/1f/1f801fc1-8c0f-ea3e-d3e5-387c6619619e/16UMGIM86640.rgb.jpg/100x100bb.jpg"
        ),
        Track(
            "Whole Lotta Love",
            "Led Zeppelin",
            "5:33",
            "https://is2-ssl.mzstatic.com/image/thumb/Music62/v4/7e/17/e3/7e17e33f-2efa-2a36-e916-7f808576cf6b/mzm.fyigqcbs.jpg/100x100bb.jpg"
        ),
        Track(
            "Sweet Child O'Mine",
            "Guns N' Roses",
            "5:03",
            "https://is5-ssl.mzstatic.com/image/thumb/Music125/v4/a0/4d/c4/a04dc484-03cc-02aa-fa82-5334fcb4bc16/18UMGIM24878.rgb.jpg/100x100bb.jpg"
        )
    )

}

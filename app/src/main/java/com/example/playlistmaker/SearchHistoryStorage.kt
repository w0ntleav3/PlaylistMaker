package com.example.playlistmaker

import android.content.Context
import com.google.firebase.crashlytics.buildtools.reloc.com.google.common.reflect.TypeToken
import com.google.gson.Gson

class SearchHistoryStorage(context: Context) {

    private val prefs = context.getSharedPreferences("search_history_prefs", Context.MODE_PRIVATE)
    private val gson = Gson()
    private val HISTORY_KEY = "history_list"

    fun getHistory(): ArrayList<Track> {
        val json = prefs.getString(HISTORY_KEY, null) ?: return arrayListOf()
        val type = object : TypeToken<ArrayList<Track>>() {}.type
        return gson.fromJson(json, type)
    }

    fun saveHistory(list: ArrayList<Track>) {
        val json = gson.toJson(list)
        prefs.edit().putString(HISTORY_KEY, json).apply()
    }

    fun clearHistory() {
        prefs.edit().remove(HISTORY_KEY).apply()
    }
    fun addTrack(track: Track) {
        val history = getHistory()

        // 1. удаляем, если трек уже есть
        history.removeAll { it.trackId == track.trackId }

        // 2. добавляем в начало
        history.add(0, track)

        // 3. обрезаем до 10
        if (history.size > 10) {
            history.removeAt(history.size - 1)
        }

        saveHistory(history)
    }

}

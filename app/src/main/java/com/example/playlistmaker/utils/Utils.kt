package com.example.playlistmaker.utils

import java.text.SimpleDateFormat
import java.util.*

fun formatTrackTime(ms: Long): String {
    return SimpleDateFormat("mm:ss", Locale.getDefault()).format(ms)
}

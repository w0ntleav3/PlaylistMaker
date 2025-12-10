package com.example.playlistmaker

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import android.widget.Switch
import android.content.Intent
import android.net.Uri

class SettingsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // выбираем тему из SharedPreferences
        val prefs = getSharedPreferences("settings", MODE_PRIVATE)
        val isNight = prefs.getBoolean("night_mode", false)
        if (isNight) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        }

        setContentView(R.layout.activity_settings)

        // кнопка назад
        findViewById<View>(R.id.btn_back).setOnClickListener {
            finish()
        }

        findViewById<View>(R.id.btn_share).setOnClickListener {
            val intent = Intent(Intent.ACTION_SEND)
            intent.type = "text/plain"
            intent.putExtra(Intent.EXTRA_TEXT, getString(R.string.share_text))
            startActivity(Intent.createChooser(intent, null))
        }

        findViewById<View>(R.id.btn_support).setOnClickListener {
            val intent = Intent(Intent.ACTION_SENDTO)
            intent.data = Uri.parse("mailto:")
            intent.putExtra(Intent.EXTRA_EMAIL, arrayOf(getString(R.string.support_email)))
            intent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.support_subject))
            intent.putExtra(Intent.EXTRA_TEXT, getString(R.string.support_body))
            startActivity(intent)
        }

        findViewById<View>(R.id.btn_agreement).setOnClickListener {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.agreement_url)))
            startActivity(intent)
        }



        val switchTheme = findViewById<Switch>(R.id.switch_dark_mode)
        switchTheme.isChecked = isNight

        switchTheme.setOnCheckedChangeListener { _, isChecked ->
            prefs.edit().putBoolean("night_mode", isChecked).apply()
            AppCompatDelegate.setDefaultNightMode(
                if (isChecked) AppCompatDelegate.MODE_NIGHT_YES
                else AppCompatDelegate.MODE_NIGHT_NO
            )
            // пересоздать текущую активити, чтобы тема сразу обновилась
            recreate()
        }

    }
}

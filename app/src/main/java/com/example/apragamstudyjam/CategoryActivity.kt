package com.example.apragamstudyjam

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import com.example.apragamstudyjam.databinding.ActivityCategoryBinding

/**
 * Activity for category selection and dark mode toggle.
 */
class CategoryActivity : AppCompatActivity() {

    companion object {
        const val EXTRA_CATEGORY = "EXTRA_CATEGORY"
        const val PREFS_NAME = "QuizPrefs"
        const val KEY_DARK_MODE = "KEY_DARK_MODE"
    }

    private lateinit var binding: ActivityCategoryBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Apply saved theme mode before layout inflation (defaults to Neon Night dark theme)
        val prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE)
        val isDarkMode = prefs.getBoolean(KEY_DARK_MODE, true)
        applyTheme(isDarkMode)

        binding = ActivityCategoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Initialize Dark Mode Switch state
        binding.darkModeSwitch.isChecked = isDarkMode
        binding.darkModeSwitch.setOnCheckedChangeListener { _, isChecked ->
            prefs.edit().putBoolean(KEY_DARK_MODE, isChecked).apply()
            applyTheme(isChecked)
        }

        // Set category card click listeners
        binding.cardAndroid.setOnClickListener { startQuizWithCategory("Android") }
        binding.cardProgramming.setOnClickListener { startQuizWithCategory("Programming") }
        binding.cardGeneral.setOnClickListener { startQuizWithCategory("General Knowledge") }
        binding.cardScience.setOnClickListener { startQuizWithCategory("Science") }
        binding.cardAll.setOnClickListener { startQuizWithCategory("All") }
    }

    private fun applyTheme(isDarkMode: Boolean) {
        val targetMode = if (isDarkMode) {
            AppCompatDelegate.MODE_NIGHT_YES
        } else {
            AppCompatDelegate.MODE_NIGHT_NO
        }
        if (AppCompatDelegate.getDefaultNightMode() != targetMode) {
            AppCompatDelegate.setDefaultNightMode(targetMode)
        }
    }

    private fun startQuizWithCategory(category: String) {
        val intent = Intent(this, QuizActivity::class.java).apply {
            putExtra(EXTRA_CATEGORY, category)
        }
        startActivity(intent)
    }
}
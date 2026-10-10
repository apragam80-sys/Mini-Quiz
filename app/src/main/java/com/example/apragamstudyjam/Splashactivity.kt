package com.example.apragamstudyjam

import android.R.anim.fade_in
import android.R.anim.fade_out
import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.animation.OvershootInterpolator
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import com.example.apragamstudyjam.databinding.ActivitySplashactivityBinding

/**
 * Splash screen displaying animated title, image, and credits before navigating to CategoryActivity.
 */
@SuppressLint("CustomSplashScreen")
class SplashActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySplashactivityBinding
    private val handler = Handler(Looper.getMainLooper())
    private var navigateRunnable: Runnable? = null
    private var mainAnimatorSet: AnimatorSet? = null
    private var pulseAnimatorSet: AnimatorSet? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Apply saved dark mode setting (defaults to Neon Night dark theme)
        val prefs = getSharedPreferences(CategoryActivity.PREFS_NAME, MODE_PRIVATE)
        val isDarkMode = prefs.getBoolean(CategoryActivity.KEY_DARK_MODE, true)
        val targetMode = if (isDarkMode) AppCompatDelegate.MODE_NIGHT_YES else AppCompatDelegate.MODE_NIGHT_NO
        AppCompatDelegate.setDefaultNightMode(targetMode)

        binding = ActivitySplashactivityBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Set initial invisible/offset states for animations
        prepareInitialViewStates()

        // Start splash animations
        startSplashAnimations()

        // Navigate to CategoryActivity after 3 seconds
        navigateRunnable = Runnable {
            if (!isFinishing && !isDestroyed) {
                navigateToCategoryScreen()
            }
        }
        handler.postDelayed(navigateRunnable!!, 3000)
    }

    private fun prepareInitialViewStates() {
        binding.titleText.alpha = 0f
        binding.titleText.translationY = -120f

        binding.subtitleText.alpha = 0f
        binding.subtitleText.translationY = 50f

        binding.quizImage.scaleX = 0f
        binding.quizImage.scaleY = 0f
        binding.quizImage.rotation = -20f

        binding.tvCredits.alpha = 0f
    }

    private fun startSplashAnimations() {
        // 1. Quiz Image scale up with bounce and slight rotation
        val imageScaleX = ObjectAnimator.ofFloat(binding.quizImage, "scaleX", 0f, 1f).apply {
            duration = 900
            interpolator = OvershootInterpolator(1.4f)
        }
        val imageScaleY = ObjectAnimator.ofFloat(binding.quizImage, "scaleY", 0f, 1f).apply {
            duration = 900
            interpolator = OvershootInterpolator(1.4f)
        }
        val imageRotate = ObjectAnimator.ofFloat(binding.quizImage, "rotation", -20f, 0f).apply {
            duration = 900
            interpolator = OvershootInterpolator(1.2f)
        }

        val imageBounceSet = AnimatorSet().apply {
            playTogether(imageScaleX, imageScaleY, imageRotate)
        }

        // 2. Subtle pulsing glow effect on quiz image
        val pulseX = ObjectAnimator.ofFloat(binding.quizImage, "scaleX", 1f, 1.06f, 1f).apply {
            duration = 1200
            repeatCount = ObjectAnimator.INFINITE
            repeatMode = ObjectAnimator.REVERSE
            interpolator = AccelerateDecelerateInterpolator()
        }
        val pulseY = ObjectAnimator.ofFloat(binding.quizImage, "scaleY", 1f, 1.06f, 1f).apply {
            duration = 1200
            repeatCount = ObjectAnimator.INFINITE
            repeatMode = ObjectAnimator.REVERSE
            interpolator = AccelerateDecelerateInterpolator()
        }
        val pulseSet = AnimatorSet().apply {
            playTogether(pulseX, pulseY)
        }
        pulseAnimatorSet = pulseSet

        imageBounceSet.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator) {
                if (!isFinishing && !isDestroyed) {
                    pulseSet.start()
                }
            }
        })

        // 3. Title text slide down and fade in
        val titleAlpha = ObjectAnimator.ofFloat(binding.titleText, "alpha", 0f, 1f).setDuration(600)
        val titleTranslate = ObjectAnimator.ofFloat(binding.titleText, "translationY", -120f, 0f).setDuration(600)
        val titleSet = AnimatorSet().apply {
            playTogether(titleAlpha, titleTranslate)
            interpolator = OvershootInterpolator(1.0f)
            startDelay = 300
        }

        // 4. Subtitle fade in
        val subtitleAlpha = ObjectAnimator.ofFloat(binding.subtitleText, "alpha", 0f, 1f).setDuration(500)
        val subtitleTranslate = ObjectAnimator.ofFloat(binding.subtitleText, "translationY", 50f, 0f).setDuration(500)
        val subtitleSet = AnimatorSet().apply {
            playTogether(subtitleAlpha, subtitleTranslate)
            startDelay = 600
        }

        // 5. Credits fade in last
        val creditsAlpha = ObjectAnimator.ofFloat(binding.tvCredits, "alpha", 0f, 1f).apply {
            duration = 500
            startDelay = 900
        }

        // Run overall sequence
        mainAnimatorSet = AnimatorSet().apply {
            playTogether(imageBounceSet, titleSet, subtitleSet, creditsAlpha)
            start()
        }
    }

    private fun navigateToCategoryScreen() {
        val intent = Intent(this, CategoryActivity::class.java)
        startActivity(intent)
        @Suppress("DEPRECATION")
        overridePendingTransition(fade_in, fade_out)
        finish()
    }

    override fun onDestroy() {
        super.onDestroy()
        navigateRunnable?.let { handler.removeCallbacks(it) }
        mainAnimatorSet?.cancel()
        pulseAnimatorSet?.cancel()
    }
}
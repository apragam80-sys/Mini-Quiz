package com.example.apragamstudyjam

import android.animation.ValueAnimator
import android.content.Intent
import android.os.Bundle
import android.view.animation.DecelerateInterpolator
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.apragamstudyjam.databinding.ActivityResultBinding

/**
 * ResultActivity presents final scores, performance stats, high scores, review button, and restart options.
 */
class ResultActivity : AppCompatActivity() {

    private lateinit var binding: ActivityResultBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityResultBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(binding.resultMain) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Retrieve quiz result statistics from intent
        val score = intent.getIntExtra("SCORE", 0)
        val total = intent.getIntExtra("TOTAL", 10)
        val correctCount = intent.getIntExtra("CORRECT_COUNT", 0)
        val wrongCount = intent.getIntExtra("WRONG_COUNT", 0)
        val skippedCount = intent.getIntExtra("SKIPPED_COUNT", 0)
        val questions = intent.getParcelableArrayListExtra<Question>("QUESTIONS")
        val userAnswers = intent.getIntArrayExtra("USER_ANSWERS")

        // Save and update high score in SharedPreferences
        val prefs = getSharedPreferences(CategoryActivity.PREFS_NAME, MODE_PRIVATE)
        val savedHighScore = prefs.getInt("KEY_HIGH_SCORE", 0)
        val currentHighScore = if (score > savedHighScore) {
            prefs.edit().putInt("KEY_HIGH_SCORE", score).apply()
            score
        } else {
            savedHighScore
        }

        binding.highScoreText.text = getString(R.string.high_score_format, currentHighScore)

        // Animate score count-up
        animateScoreCountUp(score)

        // Set detailed stat texts
        binding.correctCountText.text = getString(R.string.stat_correct, correctCount)
        binding.wrongCountText.text = getString(R.string.stat_wrong, wrongCount)
        binding.skippedCountText.text = getString(R.string.stat_skipped, skippedCount)

        val accuracy = if (total > 0) (correctCount.toFloat() / total.toFloat()) * 100f else 0f
        binding.accuracyText.text = getString(R.string.stat_accuracy, accuracy)

        // Determine performance message based on score percentage
        val maxScore = total * QuizActivity.CORRECT_MARKS
        binding.messageText.text = when {
            score >= maxScore && maxScore > 0 -> getString(R.string.feedback_perfect)
            score >= maxScore * 0.7 -> getString(R.string.feedback_excellent)
            score >= maxScore * 0.4 -> getString(R.string.feedback_good)
            else -> getString(R.string.feedback_needs_practice)
        }

        // Review answers button action
        binding.reviewButton.setOnClickListener {
            if (questions != null && userAnswers != null) {
                val intent = Intent(this, ReviewActivity::class.java).apply {
                    putParcelableArrayListExtra("QUESTIONS", questions)
                    putExtra("USER_ANSWERS", userAnswers)
                }
                startActivity(intent)
            }
        }

        // Restart quiz button action -> open CategoryActivity
        binding.restartButton.setOnClickListener {
            val intent = Intent(this, CategoryActivity::class.java)
            startActivity(intent)
            finish()
        }

        // Exit app button action
        binding.exitButton.setOnClickListener {
            finishAffinity()
        }
    }

    /**
     * Animates counting up the score text from 0 to final target score.
     */
    private fun animateScoreCountUp(targetScore: Int) {
        val animator = ValueAnimator.ofInt(0, targetScore).apply {
            duration = 1200
            interpolator = DecelerateInterpolator()
            addUpdateListener { animation ->
                binding.scoreText.text = animation.animatedValue.toString()
            }
        }
        animator.start()
    }
}
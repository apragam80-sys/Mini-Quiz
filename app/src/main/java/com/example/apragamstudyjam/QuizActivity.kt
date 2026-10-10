package com.example.apragamstudyjam

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import android.view.View
import android.view.animation.OvershootInterpolator
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.apragamstudyjam.databinding.ActivityQuizBinding

/**
 * QuizActivity handles displaying shuffled questions filtered by category, lifelines,
 * streak bonus system, countdown timer, options, and navigation.
 */
class QuizActivity : AppCompatActivity() {

    companion object {
        const val CORRECT_MARKS = 4
        const val WRONG_MARKS = -1
        const val SKIPPED_MARKS = 0
        const val STREAK_BONUS = 2
        const val TIMER_DURATION_MS = 15000L
        const val TIMER_INTERVAL_MS = 50L
    }

    private lateinit var binding: ActivityQuizBinding
    private lateinit var questions: List<Question>
    private lateinit var optionButtons: List<TextView>

    private var currentQuestionIndex = 0
    private var score = 0
    private var correctCount = 0
    private var wrongCount = 0
    private var skippedCount = 0
    private var currentStreak = 0

    private var isAnswered = false
    private var isFiftyFiftyUsed = false
    private var isSkipUsed = false

    private lateinit var userAnswers: IntArray
    private var countDownTimer: CountDownTimer? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityQuizBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Retrieve selected category extra or default to "All"
        val category = intent.getStringExtra(CategoryActivity.EXTRA_CATEGORY) ?: "All"
        questions = QuestionBank.getQuestionsByCategory(category)
        if (questions.isEmpty()) {
            questions = QuestionBank.getShuffledQuestions()
        }

        userAnswers = IntArray(questions.size) { -1 }

        optionButtons = listOf(
            binding.option1Button,
            binding.option2Button,
            binding.option3Button,
            binding.option4Button
        )

        // Set click listeners on options
        optionButtons.forEachIndexed { index, textView ->
            textView.setOnClickListener {
                if (!isAnswered) {
                    checkAnswer(index)
                }
            }
        }

        // Set lifeline click listeners
        binding.btnFiftyFifty.setOnClickListener {
            if (!isFiftyFiftyUsed && !isAnswered) {
                useFiftyFifty()
            }
        }

        binding.btnSkip.setOnClickListener {
            if (!isSkipUsed && !isAnswered) {
                useSkipLifeline()
            }
        }

        // Set next button click listener
        binding.nextButton.setOnClickListener {
            if (currentQuestionIndex < questions.size - 1) {
                currentQuestionIndex++
                showQuestion()
            } else {
                openResultScreen()
            }
        }

        // Display first question
        showQuestion()
    }

    /**
     * Displays current question, updates progress indicators, resets option views, and starts timer.
     */
    private fun showQuestion() {
        isAnswered = false
        val currentQuestion = questions[currentQuestionIndex]

        // Update progress and header details
        binding.categoryBadge.text = currentQuestion.category
        binding.scoreText.text = getString(R.string.score_format, score)
        binding.questionNumberText.text = getString(R.string.question_number_format, currentQuestionIndex + 1, questions.size)
        binding.quizProgressBar.max = questions.size
        binding.quizProgressBar.progress = currentQuestionIndex + 1

        // Set question and option texts
        binding.questionText.text = currentQuestion.text
        optionButtons.forEachIndexed { index, button ->
            button.text = currentQuestion.options[index]
            button.setBackgroundResource(R.drawable.bg_option_default)
            button.setTextColor(ContextCompat.getColor(this, R.color.text_primary))
            button.isEnabled = true
            button.visibility = View.VISIBLE
            button.alpha = 1f
        }

        // Hide next button initially for new question
        binding.nextButton.visibility = View.GONE

        // Update Lifeline Button UI state
        updateLifelinesUI()

        // Animate question card entry
        animateCardEntry()

        // Start 15s timer
        startTimer()
    }

    private fun updateLifelinesUI() {
        if (isFiftyFiftyUsed) {
            binding.btnFiftyFifty.isEnabled = false
            binding.btnFiftyFifty.alpha = 0.4f
        } else {
            binding.btnFiftyFifty.isEnabled = !isAnswered
            binding.btnFiftyFifty.alpha = 1f
        }

        if (isSkipUsed) {
            binding.btnSkip.isEnabled = false
            binding.btnSkip.alpha = 0.4f
        } else {
            binding.btnSkip.isEnabled = !isAnswered
            binding.btnSkip.alpha = 1f
        }
    }

    /**
     * Lifeline 1: 50-50 - Hides 2 incorrect options for the current question.
     */
    private fun useFiftyFifty() {
        isFiftyFiftyUsed = true
        updateLifelinesUI()

        val currentQuestion = questions[currentQuestionIndex]
        val wrongIndices = (0..3).filter { it != currentQuestion.correctIndex }.shuffled().take(2)

        wrongIndices.forEach { index ->
            optionButtons[index].visibility = View.INVISIBLE
            optionButtons[index].isEnabled = false
        }
    }

    /**
     * Lifeline 2: Skip - Skips the current question with no penalty and preserves streak.
     */
    private fun useSkipLifeline() {
        isSkipUsed = true
        countDownTimer?.cancel()
        skippedCount++
        userAnswers[currentQuestionIndex] = -1

        if (currentQuestionIndex < questions.size - 1) {
            currentQuestionIndex++
            showQuestion()
        } else {
            openResultScreen()
        }
    }

    /**
     * Animates question card sliding in from bottom with fade effect.
     */
    private fun animateCardEntry() {
        binding.questionCard.alpha = 0f
        binding.questionCard.translationY = 80f

        val fade = ObjectAnimator.ofFloat(binding.questionCard, "alpha", 0f, 1f)
        val translate = ObjectAnimator.ofFloat(binding.questionCard, "translationY", 80f, 0f)

        AnimatorSet().apply {
            playTogether(fade, translate)
            duration = 400
            interpolator = OvershootInterpolator(1.1f)
            start()
        }
    }

    /**
     * Starts or resets the 15-second question countdown timer.
     */
    private fun startTimer() {
        countDownTimer?.cancel()

        binding.timerProgress.max = (TIMER_DURATION_MS / TIMER_INTERVAL_MS).toInt()
        binding.timerProgress.progress = binding.timerProgress.max

        countDownTimer = object : CountDownTimer(TIMER_DURATION_MS, TIMER_INTERVAL_MS) {
            override fun onTick(millisUntilFinished: Long) {
                val progress = (millisUntilFinished / TIMER_INTERVAL_MS).toInt()
                binding.timerProgress.progress = progress
                val secondsLeft = (millisUntilFinished / 1000) + 1
                binding.timerText.text = getString(R.string.timer_format, secondsLeft)
            }

            override fun onFinish() {
                binding.timerProgress.progress = 0
                binding.timerText.text = getString(R.string.timer_format, 0)
                handleTimeout()
            }
        }.start()
    }

    /**
     * Handles timer expiration when question goes unanswered.
     */
    private fun handleTimeout() {
        if (isAnswered) return
        isAnswered = true
        skippedCount++
        currentStreak = 0 // Reset streak on timeout
        score += SKIPPED_MARKS
        userAnswers[currentQuestionIndex] = -1

        val currentQuestion = questions[currentQuestionIndex]

        // Disable all buttons and highlight correct answer
        optionButtons.forEach { it.isEnabled = false }
        optionButtons[currentQuestion.correctIndex].setBackgroundResource(R.drawable.bg_option_correct)
        optionButtons[currentQuestion.correctIndex].setTextColor(ContextCompat.getColor(this, R.color.option_correct_text))

        // Show timeout score popup
        showScorePopup(getString(R.string.score_popup_timeout), false)
        hideStreakBadge()
        showNextButton()
    }

    /**
     * Evaluates selected option index against correct answer index.
     */
    private fun checkAnswer(selectedIndex: Int) {
        isAnswered = true
        countDownTimer?.cancel()
        userAnswers[currentQuestionIndex] = selectedIndex

        val currentQuestion = questions[currentQuestionIndex]
        optionButtons.forEach { it.isEnabled = false }
        updateLifelinesUI()

        if (selectedIndex == currentQuestion.correctIndex) {
            score += CORRECT_MARKS
            correctCount++
            currentStreak++

            optionButtons[selectedIndex].setBackgroundResource(R.drawable.bg_option_correct)
            optionButtons[selectedIndex].setTextColor(ContextCompat.getColor(this, R.color.option_correct_text))

            var popupText = "+$CORRECT_MARKS"

            // Check for Streak Bonus (+2 points every 3 consecutive correct answers)
            if (currentStreak > 0 && currentStreak % 3 == 0) {
                score += STREAK_BONUS
                popupText = "+${CORRECT_MARKS + STREAK_BONUS}"
                showStreakBadgeAnimation(currentStreak)
            }

            showScorePopup(popupText, true)
        } else {
            score += WRONG_MARKS
            wrongCount++
            currentStreak = 0 // Reset streak on wrong answer

            optionButtons[selectedIndex].setBackgroundResource(R.drawable.bg_option_wrong)
            optionButtons[selectedIndex].setTextColor(ContextCompat.getColor(this, R.color.option_wrong_text))

            // Highlight the correct answer
            optionButtons[currentQuestion.correctIndex].setBackgroundResource(R.drawable.bg_option_correct)
            optionButtons[currentQuestion.correctIndex].setTextColor(ContextCompat.getColor(this, R.color.option_correct_text))

            showScorePopup("$WRONG_MARKS", false)
            hideStreakBadge()
        }

        binding.scoreText.text = getString(R.string.score_format, score)
        showNextButton()
    }

    /**
     * Animates the Streak Badge on streak milestones (3, 6, 9...).
     */
    private fun showStreakBadgeAnimation(streak: Int) {
        binding.streakBadge.text = getString(R.string.streak_bonus_format, streak)
        binding.streakBadge.visibility = View.VISIBLE
        binding.streakBadge.scaleX = 0f
        binding.streakBadge.scaleY = 0f

        val scaleX = ObjectAnimator.ofFloat(binding.streakBadge, "scaleX", 0f, 1.2f, 1f)
        val scaleY = ObjectAnimator.ofFloat(binding.streakBadge, "scaleY", 0f, 1.2f, 1f)

        AnimatorSet().apply {
            playTogether(scaleX, scaleY)
            duration = 500
            interpolator = OvershootInterpolator(1.4f)
            start()
        }
    }

    private fun hideStreakBadge() {
        binding.streakBadge.visibility = View.GONE
    }

    /**
     * Displays a floating animated popup for score change (+4, +6, -1, +0).
     */
    private fun showScorePopup(text: String, isPositive: Boolean) {
        binding.scorePopupText.text = text
        val textColorRes = if (isPositive) R.color.score_positive else R.color.score_negative
        binding.scorePopupText.setTextColor(ContextCompat.getColor(this, textColorRes))

        binding.scorePopupText.visibility = View.VISIBLE
        binding.scorePopupText.alpha = 1f
        binding.scorePopupText.translationY = 0f

        val translateY = ObjectAnimator.ofFloat(binding.scorePopupText, "translationY", 0f, -60f)
        val fadeOut = ObjectAnimator.ofFloat(binding.scorePopupText, "alpha", 1f, 0f)

        AnimatorSet().apply {
            playTogether(translateY, fadeOut)
            duration = 1000
            start()
        }
    }

    /**
     * Displays the Next button with a smooth fade-in animation.
     */
    private fun showNextButton() {
        binding.nextButton.visibility = View.VISIBLE
        binding.nextButton.alpha = 0f
        binding.nextButton.animate().alpha(1f).setDuration(300).start()
    }

    /**
     * Navigates to ResultActivity with performance statistics, questions list, and user answers.
     */
    private fun openResultScreen() {
        countDownTimer?.cancel()
        val intent = Intent(this, ResultActivity::class.java).apply {
            putExtra("SCORE", score)
            putExtra("TOTAL", questions.size)
            putExtra("CORRECT_COUNT", correctCount)
            putExtra("WRONG_COUNT", wrongCount)
            putExtra("SKIPPED_COUNT", skippedCount)
            putParcelableArrayListExtra("QUESTIONS", ArrayList(questions))
            putExtra("USER_ANSWERS", userAnswers)
        }
        startActivity(intent)
        finish()
    }

    override fun onDestroy() {
        super.onDestroy()
        countDownTimer?.cancel()
    }
}
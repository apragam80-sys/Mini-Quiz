package com.example.apragamstudyjam

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.apragamstudyjam.databinding.ActivityReviewBinding

/**
 * ReviewActivity displays a RecyclerView of all quiz questions, the user's answers, and correct answers.
 */
class ReviewActivity : AppCompatActivity() {

    private lateinit var binding: ActivityReviewBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityReviewBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val questions = intent.getParcelableArrayListExtra<Question>("QUESTIONS") ?: ArrayList()
        val userAnswers = intent.getIntArrayExtra("USER_ANSWERS") ?: IntArray(0)

        binding.reviewRecyclerView.layoutManager = LinearLayoutManager(this)
        binding.reviewRecyclerView.adapter = ReviewAdapter(questions, userAnswers)

        binding.backButton.setOnClickListener {
            finish()
        }
    }
}
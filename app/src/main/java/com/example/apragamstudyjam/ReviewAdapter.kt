package com.example.apragamstudyjam

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.apragamstudyjam.databinding.ItemReviewQuestionBinding

/**
 * RecyclerView Adapter for displaying detailed review of quiz questions and user answers.
 */
class ReviewAdapter(
    private val questions: List<Question>,
    private val userAnswers: IntArray
) : RecyclerView.Adapter<ReviewAdapter.ReviewViewHolder>() {

    class ReviewViewHolder(val binding: ItemReviewQuestionBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReviewViewHolder {
        val binding = ItemReviewQuestionBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ReviewViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ReviewViewHolder, position: Int) {
        val context = holder.itemView.context
        val question = questions[position]
        val userAnswerIndex = if (position < userAnswers.size) userAnswers[position] else -1

        holder.binding.questionIndexText.text = context.getString(
            R.string.question_number_format,
            position + 1,
            questions.size
        ) + " • " + question.category

        holder.binding.questionText.text = question.text

        val correctAnswerStr = question.options[question.correctIndex]
        holder.binding.correctAnswerText.text = context.getString(R.string.correct_answer_format, correctAnswerStr)

        if (userAnswerIndex == -1) {
            // Skipped or Timed out
            holder.binding.statusBadge.text = context.getString(R.string.status_skipped)
            holder.binding.statusBadge.backgroundTintList = ContextCompat.getColorStateList(context, R.color.option_skipped_text)
            holder.binding.userAnswerText.text = context.getString(R.string.user_answer_format, context.getString(R.string.status_skipped))
            holder.binding.userAnswerText.setTextColor(ContextCompat.getColor(context, R.color.option_skipped_text))
        } else if (userAnswerIndex == question.correctIndex) {
            // Correct answer
            holder.binding.statusBadge.text = context.getString(R.string.status_correct)
            holder.binding.statusBadge.backgroundTintList = ContextCompat.getColorStateList(context, R.color.option_correct_stroke)
            val userAnsStr = question.options[userAnswerIndex]
            holder.binding.userAnswerText.text = context.getString(R.string.user_answer_format, userAnsStr)
            holder.binding.userAnswerText.setTextColor(ContextCompat.getColor(context, R.color.option_correct_text))
        } else {
            // Wrong answer
            holder.binding.statusBadge.text = context.getString(R.string.status_wrong)
            holder.binding.statusBadge.backgroundTintList = ContextCompat.getColorStateList(context, R.color.option_wrong_stroke)
            val userAnsStr = if (userAnswerIndex in question.options.indices) question.options[userAnswerIndex] else "N/A"
            holder.binding.userAnswerText.text = context.getString(R.string.user_answer_format, userAnsStr)
            holder.binding.userAnswerText.setTextColor(ContextCompat.getColor(context, R.color.option_wrong_text))
        }
    }

    override fun getItemCount(): Int = questions.size
}
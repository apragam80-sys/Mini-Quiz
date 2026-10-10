package com.example.apragamstudyjam

import android.os.Parcel
import android.os.Parcelable

/**
 * Data class representing a single quiz question.
 * Implements Parcelable for Intent passing.
 */
data class Question(
    val text: String,
    val options: List<String>,
    val correctIndex: Int,
    val category: String
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString() ?: "",
        parcel.createStringArrayList() ?: emptyList(),
        parcel.readInt(),
        parcel.readString() ?: ""
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(text)
        parcel.writeStringList(options)
        parcel.writeInt(correctIndex)
        parcel.writeString(category)
    }

    override fun describeContents(): Int = 0

    companion object CREATOR : Parcelable.Creator<Question> {
        override fun createFromParcel(parcel: Parcel): Question {
            return Question(parcel)
        }

        override fun newArray(size: Int): Array<Question?> {
            return arrayOfNulls(size)
        }
    }
}
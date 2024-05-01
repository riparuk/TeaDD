package com.dicoding.asclepius.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class PredictionHistory(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val imagePath: String?,
    val predictionResult: String,
    val confidenceScore: Float,
    val insertedAt: Long = System.currentTimeMillis()
)

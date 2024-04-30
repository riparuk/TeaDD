package com.dicoding.asclepius.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class PredictionHistory(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val imagePath: String, // Path file gambar
    val predictionResult: String, // Hasil prediksi
    val confidenceScore: Float // Skor kepercayaan
)

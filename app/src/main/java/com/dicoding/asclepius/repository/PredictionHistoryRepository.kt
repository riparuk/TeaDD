package com.dicoding.asclepius.repository

import androidx.annotation.WorkerThread
import com.dicoding.asclepius.database.PredictionHistory
import com.dicoding.asclepius.database.PredictionHistoryDao
import kotlinx.coroutines.flow.Flow

class PredictionHistoryRepository(private val predictionHistoryDao: PredictionHistoryDao) {
    val allHistory: Flow<List<PredictionHistory>> = predictionHistoryDao.getAllPredictionHistory()

    @WorkerThread
    suspend fun insert(history: PredictionHistory) {
        predictionHistoryDao.insertPredictionHistory(history)
    }

    @WorkerThread
    suspend fun getHistoryById(id: Long): PredictionHistory? {
        return predictionHistoryDao.getPredictionHistoryById(id)
    }
}
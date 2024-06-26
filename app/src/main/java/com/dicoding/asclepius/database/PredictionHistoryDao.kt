package com.dicoding.asclepius.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface PredictionHistoryDao {
    @Query("SELECT * FROM predictionhistory ORDER BY id DESC")
    fun getAllPredictionHistory(): Flow<List<PredictionHistory>>

    @Query("SELECT * FROM predictionhistory WHERE id = :id")
    suspend fun getPredictionHistoryById(id: Long): PredictionHistory?

    @Insert
    suspend fun insertPredictionHistory(predictionHistory: PredictionHistory)

    @Query("DELETE FROM predictionhistory")
    suspend fun deleteAll()
}
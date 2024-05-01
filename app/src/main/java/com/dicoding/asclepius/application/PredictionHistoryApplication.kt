package com.dicoding.asclepius.application

import android.app.Application
import com.dicoding.asclepius.database.PredictionHistoryDatabase
import com.dicoding.asclepius.repository.PredictionHistoryRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob

class PredictionHistoryApplication : Application() {
    // No need to cancel this scope as it'll be torn down with the process
    val applicationScope = CoroutineScope(SupervisorJob())

    // Using by lazy so the database and the repository are only created when they're needed
    // rather than when the application starts
    val database by lazy { PredictionHistoryDatabase.getDatabase(this, applicationScope) }
    val repository by lazy { PredictionHistoryRepository(database.predictionHistoryDao()) }
}
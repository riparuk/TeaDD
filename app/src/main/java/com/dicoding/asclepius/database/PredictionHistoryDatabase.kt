package com.dicoding.asclepius.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Database(entities = [PredictionHistory::class], version = 1, exportSchema = false)
abstract class PredictionHistoryDatabase : RoomDatabase() {
    abstract fun predictionHistoryDao(): PredictionHistoryDao

    companion object {
        @Volatile
        private var INSTANCE: PredictionHistoryDatabase? = null

        fun getDatabase(context: Context,
                        scope: CoroutineScope
        ): PredictionHistoryDatabase {

            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    PredictionHistoryDatabase::class.java,
                    "prediction_history_database"
                )
                    .addCallback(HistoryDatabaseCallback(scope))
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }

    private class HistoryDatabaseCallback(
        private val scope: CoroutineScope
    ) : RoomDatabase.Callback() {

        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)
            INSTANCE?.let { database ->
                scope.launch {
                    populateDatabase(database.predictionHistoryDao())
                }
            }
        }

        suspend fun populateDatabase(predictionHistoryDao: PredictionHistoryDao) {
            // Delete all content here.
            predictionHistoryDao.deleteAll()

            // Add sample words.
            var history = PredictionHistory(1, "PATH", "Cancer", 0.8f)
            predictionHistoryDao.insertPredictionHistory(history)
            history = PredictionHistory(2, "PATH", "Non Cancer", 0.6f)
            predictionHistoryDao.insertPredictionHistory(history)


        }
    }
}
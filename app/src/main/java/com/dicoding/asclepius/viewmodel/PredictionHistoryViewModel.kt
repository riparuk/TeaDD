package com.dicoding.asclepius.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.dicoding.asclepius.database.PredictionHistory
import com.dicoding.asclepius.repository.PredictionHistoryRepository
import kotlinx.coroutines.launch

class PredictionHistoryViewModel(private val repository: PredictionHistoryRepository): ViewModel() {
    val allHisory: LiveData<List<PredictionHistory>> = repository.allHistory.asLiveData()

    fun insert(history: PredictionHistory) = viewModelScope.launch {
        repository.insert(history)
    }
}

class HistoryViewModelFactory(private val repository: PredictionHistoryRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(PredictionHistoryViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return PredictionHistoryViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
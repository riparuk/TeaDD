package com.dicoding.asclepius.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dicoding.asclepius.networkapi.response.ArticlesItem
import com.dicoding.asclepius.networkapi.retrofit.ApiConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ArticlesViewModel : ViewModel() {
    private val _articles = MutableLiveData<List<ArticlesItem>>()
    val articles: LiveData<List<ArticlesItem>> get() = _articles

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> get() = _isLoading

    private val _toastMessage = MutableLiveData<String>()
    val toastMessage: LiveData<String> get() = _toastMessage

    private val apiService = ApiConfig.getApiService()

    fun getTopHeadlines() {
        _isLoading.value = true // Menampilkan loading sebelum memulai panggilan API
        viewModelScope.launch {
            try {
                val response = apiService.getTopHeadlines("cancer", "health", "en")
                if (response.isSuccessful) {
                    val articles = response.body()?.articles ?: emptyList()
                    val filteredArticles = articles.filter { it?.title != "[Removed]" } // Filter artikel yang tidak memiliki judul "[Removed]"
                    withContext(Dispatchers.Main) {
                        _articles.value = filteredArticles as List<ArticlesItem>
                    }
                } else {
                    // Tangani jika respons tidak berhasil
                    withContext(Dispatchers.Main) {
                        showToast("Failed to retrieve data from server")
                    }
                }
            } catch (e: Exception) {
                // Tangani jika terjadi kesalahan
                withContext(Dispatchers.Main) {
                    showToast("An error occurred: ${e.message}")
                }
            } finally {
                _isLoading.value = false // Sembunyikan loading setelah panggilan API selesai
            }
        }
    }

    private fun showToast(message: String) {
        _toastMessage.value = message
    }


}
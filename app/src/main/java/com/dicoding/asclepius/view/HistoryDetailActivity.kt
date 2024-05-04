package com.dicoding.asclepius.view

import android.annotation.SuppressLint
import android.net.Uri
import android.os.Bundle
import android.text.format.DateFormat
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.dicoding.asclepius.R
import com.dicoding.asclepius.application.PredictionHistoryApplication
import com.dicoding.asclepius.database.PredictionHistory
import com.dicoding.asclepius.databinding.ActivityHistoryDetailBinding
import com.dicoding.asclepius.databinding.ActivityMainBinding
import com.dicoding.asclepius.viewmodel.HistoryViewModelFactory
import com.dicoding.asclepius.viewmodel.PredictionHistoryViewModel
import java.text.NumberFormat
import java.util.Date

class HistoryDetailActivity : AppCompatActivity() {
    private lateinit var binding: ActivityHistoryDetailBinding

    private val viewModel: PredictionHistoryViewModel by viewModels {
        HistoryViewModelFactory((application as PredictionHistoryApplication).repository)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHistoryDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val historyId = intent.getLongExtra(EXTRA_HISTORY_ID, -1)
        if (historyId != -1L) {
            viewModel.getHistoryById(historyId)
            observeHistory()
        } else {
            showDetailNotFound()
        }

    }

    private fun observeHistory() {
        viewModel.historyById.observe(this) { history ->
            Log.d(TAG, "Hasil Obsrver ID ${history?.id}, ${history?.predictionResult}")
            if (history != null) {
                // Tampilkan detail menggunakan data yang diperoleh dari ViewModel
                showDetail(history)
            } else {
                // Tampilkan pesan kesalahan atau tindakan yang sesuai jika history null
                showDetailNotFound()
            }
        }
    }

    private fun showDetailNotFound() {
        binding.tvId.text = "History Not Found"
        binding.tvLabel.text = ""
        binding.tvScore.text = ""
        binding.tvTime.text = ""
        binding.ivHistory.setImageDrawable(null) // Menghapus gambar yang mungkin ditetapkan sebelumnya
    }

    @SuppressLint("SetTextI18n")
    private fun showDetail(history: PredictionHistory) {
        // Implementasi tampilan detail berdasarkan data yang diperoleh dari ViewModel
        binding.tvId.text = "ID : ${history.id}"
        binding.tvLabel.text = "Prediction Result : ${history.predictionResult}"
        binding.tvScore.text = "Confident Score : ${history.confidenceScore}"
        binding.tvScore.text = NumberFormat.getPercentInstance()
            .format(history.confidenceScore).trim()

        // Convert insertedAt time to a readable date-time format
        val formattedDateTime = DateFormat.format("dd/MM/yyyy HH:mm:ss", Date(history.insertedAt))
        binding.tvTime.text = "insertAt : $formattedDateTime"

        history.imagePath?.let {
            binding.ivHistory.setImageURI(Uri.parse(it))
        } ?: run {
            binding.ivHistory.setImageDrawable(null)
        }
    }

    companion object {
        const val TAG = "HistoryDetailActivity"
        const val EXTRA_HISTORY_ID = "history_id"
    }
}
package com.dicoding.asclepius.view

import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.viewModels
import com.bumptech.glide.Glide
import com.dicoding.asclepius.application.PredictionHistoryApplication
import com.dicoding.asclepius.database.PredictionHistory
import com.dicoding.asclepius.databinding.ActivityResultBinding
import com.dicoding.asclepius.helper.ImageClassifierHelper
import com.dicoding.asclepius.viewmodel.HistoryViewModelFactory
import com.dicoding.asclepius.viewmodel.PredictionHistoryViewModel
import org.tensorflow.lite.task.vision.classifier.Classifications
import java.text.NumberFormat

class ResultActivity : AppCompatActivity() {

    private lateinit var binding: ActivityResultBinding

    private lateinit var imageClassifierHelper: ImageClassifierHelper

    private val historyViewModel: PredictionHistoryViewModel by viewModels {
        HistoryViewModelFactory((application as PredictionHistoryApplication).repository)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityResultBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val imageUriString = intent.getStringExtra(EXTRA_IMAGE_URI)

        // Inisialisasi ImageView dengan URI gambar
        imageUriString?.let {
            Log.d("Image URI", "showImage: $it")
            val imageUri = Uri.parse(it)
            // Load image using Glide library
            Glide.with(this)
                .load(imageUri)
                .into(binding.resultImage)
        }

        // Inisialisasi ImageClassifierHelper dan memproses gambar
        imageClassifierHelper = ImageClassifierHelper(
            context = this,
            classifierListener = object : ImageClassifierHelper.ClassifierListener {
                override fun onError(error: String) {
                    Toast.makeText(this@ResultActivity, error, Toast.LENGTH_SHORT).show()
                }

                override fun onResults(results: List<Classifications>?, inferenceTime: Long) {
                    results?.let {
                        if (it.isNotEmpty() && it[0].categories.isNotEmpty()) {
                            val sortedCategories =
                                it[0].categories.sortedByDescending { it?.score }
                            val displayResult =
                                sortedCategories.joinToString("\n") {
                                    "${it.label} " + NumberFormat.getPercentInstance()
                                        .format(it.score).trim()
                                }
                            // Menampilkan hasil klasifikasi dan waktu inferensi
                            binding.resultText.text = "$displayResult\nInference Time: $inferenceTime ms"
                            saveToHistory(imageUriString.toString(), sortedCategories[0].label, sortedCategories[0].score)
                            showToast("Prediction results stored in history!")
                            Log.d(TAG, "Result : ${imageUriString.toString()}, ${sortedCategories[0].label}, ${sortedCategories[0].score}")
                        } else {
                            binding.resultText.text = "Empty"
                        }
                    }
                }
            }
        )

        // Memproses gambar menggunakan ImageClassifierHelper
        imageUriString?.let {
            val imageUri = Uri.parse(it)
            imageClassifierHelper.classifyStaticImage(imageUri)
        }
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    private fun saveToHistory(imageUriString: String, label: String, score: Float) {
        val predictionHistory = PredictionHistory(
            imagePath = imageUriString,
            predictionResult = label,
            confidenceScore = score
        )
        historyViewModel.insert(predictionHistory)
    }

    companion object {
        const val TAG = "ResultActivity"
        const val EXTRA_IMAGE_URI = "extra_image_uri"
    }
}
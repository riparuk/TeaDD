package com.dicoding.asclepius.view

import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.viewModels
import com.dicoding.asclepius.R
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
        val imageUri = Uri.parse(imageUriString)

        // Inisialisasi ImageView dengan URI gambar
        imageUri?.let {
            Log.d("Image URI", "showImage: $it")
            binding.resultImage.setImageURI(it)
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
                            Log.d(TAG, "Result : ${imageUriString.toString()}, ${sortedCategories[0].label}, ${sortedCategories[0].score}")
                        } else {
                            binding.resultText.text = "Empty"
                        }
                    }
                }
            }
        )

        // Memproses gambar menggunakan ImageClassifierHelper
        imageUri?.let { imageUri ->
            imageClassifierHelper.classifyStaticImage(imageUri)
        }
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
        const val EXTRA_RESULT = "extra_result"
    }
}
package com.dicoding.asclepius.helper

import android.content.Context
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.os.SystemClock
import android.provider.MediaStore
import android.util.Log
import com.dicoding.asclepius.R
import org.tensorflow.lite.DataType
import org.tensorflow.lite.support.common.ops.CastOp
import org.tensorflow.lite.support.image.ImageProcessor
import org.tensorflow.lite.support.image.TensorImage
import org.tensorflow.lite.support.image.ops.ResizeOp
import org.tensorflow.lite.task.core.BaseOptions
import org.tensorflow.lite.task.vision.classifier.Classifications
import org.tensorflow.lite.task.vision.classifier.ImageClassifier

class ImageClassifierHelper(
    private var threshold: Float = 0.1f,
    private var maxResults: Int = 3,
    private val modelName: String = "cancer_classification.tflite",
    val context: Context,
    val classifierListener: ClassifierListener?
) {
    private lateinit var imageClassifier: ImageClassifier

    init {
        setupImageClassifier()
    }

    private fun setupImageClassifier() {
        val optionsBuilder = ImageClassifier.ImageClassifierOptions.builder()
            .setScoreThreshold(threshold)
            .setMaxResults(maxResults)
        val baseOptionsBuilder = BaseOptions.builder()
            .setNumThreads(4)
        optionsBuilder.setBaseOptions(baseOptionsBuilder.build())

        try {
            imageClassifier = ImageClassifier.createFromFileAndOptions(
                context,
                modelName,
                optionsBuilder.build()
            )
        } catch (e: IllegalStateException) {
            classifierListener?.onError(context.getString(R.string.image_classifier_failed))
            Log.e(TAG, e.message.toString())
        }
    }

    fun classifyStaticImage(imageUri: Uri) {

        val imageProcessor = ImageProcessor.Builder()
            .add(ResizeOp(224, 224, ResizeOp.ResizeMethod.NEAREST_NEIGHBOR))
            .add(CastOp(DataType.FLOAT32))
            .build()

        try {
            val bitmap = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                val source = ImageDecoder.createSource(context.contentResolver, imageUri)
                ImageDecoder.decodeBitmap(source) { decoder, info, source ->
                    decoder.isMutableRequired = true
                    decoder.allocator = ImageDecoder.ALLOCATOR_SOFTWARE
                }
            } else {
                MediaStore.Images.Media.getBitmap(context.contentResolver, imageUri)
            }

            val argbBitmap = bitmap.copy(Bitmap.Config.ARGB_8888, true)

            val tensorImage = TensorImage.fromBitmap(argbBitmap)
            val processedImage = imageProcessor.process(tensorImage)

            var inferenceTime = SystemClock.uptimeMillis()
            val results = imageClassifier.classify(processedImage)
            inferenceTime = SystemClock.uptimeMillis() - inferenceTime

            if (results != null) {
                for (output in results) {
                    println("Label: ${output.categories.first().label}, Score: ${output.categories.first().score}")
                }
            }

            classifierListener?.onResults(results, inferenceTime)
        } catch (e: Exception) {
            classifierListener?.onError(context.getString(R.string.image_processing_failed))
            Log.e(TAG, "Error processing image: ${e.message}")
        }
    }


    interface ClassifierListener {
        fun onError(error: String)
        fun onResults(
            results: List<Classifications>?,
            inferenceTime: Long
        )
    }

    companion object {
        private const val TAG = "ImageClassifierHelper"
    }

}
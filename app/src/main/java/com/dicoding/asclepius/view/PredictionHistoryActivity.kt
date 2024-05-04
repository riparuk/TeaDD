package com.dicoding.asclepius.view

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.dicoding.asclepius.R
import com.dicoding.asclepius.adapter.HistoryListAdapter
import com.dicoding.asclepius.adapter.PredictionHistoryListAdapter
import com.dicoding.asclepius.application.PredictionHistoryApplication
import com.dicoding.asclepius.database.PredictionHistory
import com.dicoding.asclepius.databinding.ActivityMainBinding
import com.dicoding.asclepius.databinding.ActivityPredictionHistoryBinding
import com.dicoding.asclepius.databinding.ActivityResultBinding
import com.dicoding.asclepius.repository.PredictionHistoryRepository
import com.dicoding.asclepius.viewmodel.HistoryViewModelFactory
import com.dicoding.asclepius.viewmodel.PredictionHistoryViewModel

class PredictionHistoryActivity : AppCompatActivity(), HistoryListAdapter.OnHistoryItemClickListener {
    private lateinit var binding: ActivityPredictionHistoryBinding

    private val historyViewModel: PredictionHistoryViewModel by viewModels {
        HistoryViewModelFactory((application as PredictionHistoryApplication).repository)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPredictionHistoryBinding.inflate(layoutInflater)

        setContentView(binding.root)
        showPredictionHistory()
        Log.d(TAG, "run showPredictionHistory")
    }

    private fun showPredictionHistory() {
        val recyclerView = findViewById<RecyclerView>(R.id.recycler_view)
        val adapter = HistoryListAdapter(this)
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(this)

        historyViewModel.allHisory.observe(this) {
            it.let { adapter.submitList(it) }
        }


//        val dataset = arrayOf("January", "February", "March")
//        val customAdapter = PredictionHistoryListAdapter(dataset)
//
//        val layoutManager = LinearLayoutManager(this)
//        binding.recyclerView.layoutManager = layoutManager
//
//        val recyclerView: RecyclerView = binding.recyclerView
//        recyclerView.adapter = customAdapter
    }

    companion object {
        private const val TAG = "Prediction History Activity"
    }

    override fun onHistoryItemClicked(history: PredictionHistory) {
        val intent = Intent(this, HistoryDetailActivity::class.java)
        intent.putExtra(HistoryDetailActivity.EXTRA_HISTORY_ID, history.id)
        startActivity(intent)
    }
}
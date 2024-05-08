package com.dicoding.asclepius.view

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.dicoding.asclepius.R
import com.dicoding.asclepius.adapter.HistoryListAdapter
import com.dicoding.asclepius.application.PredictionHistoryApplication
import com.dicoding.asclepius.database.PredictionHistory
import com.dicoding.asclepius.databinding.ActivityPredictionHistoryBinding
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

        binding.ivBack.setOnClickListener {
            finish()
        }

        setContentView(binding.root)
        showPredictionHistory()
        Log.d(TAG, "run showPredictionHistory")
    }

    private fun showPredictionHistory() {
        val recyclerView = findViewById<RecyclerView>(R.id.recycler_view)
        val adapter = HistoryListAdapter(this)
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(this)

        binding.recyclerView.addItemDecoration(
            DividerItemDecoration(
                baseContext,
                LinearLayoutManager(this).orientation
            ).apply {
                setDrawable(ContextCompat.getDrawable(this@PredictionHistoryActivity, R.drawable.divider)!!)
            }
        )

        historyViewModel.allHisory.observe(this) {
            if (it.isEmpty()) {
                showMessage("You don't have a prediction history yet")
            }
            it.let { adapter.submitList(it) }
        }
    }

    companion object {
        private const val TAG = "Prediction History Activity"
    }

    override fun onHistoryItemClicked(history: PredictionHistory) {
        val intent = Intent(this, HistoryDetailActivity::class.java)
        intent.putExtra(HistoryDetailActivity.EXTRA_HISTORY_ID, history.id)
        startActivity(intent)
    }

    private fun showMessage(message: String) {
        if (message.isNotEmpty()) {
            binding.tvMessage.text = message
            binding.tvMessage.visibility = View.VISIBLE
        } else {
            binding.tvMessage.visibility = View.GONE
        }
    }
}
package com.dicoding.asclepius.adapter

import android.net.Uri
import android.text.format.DateFormat
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.dicoding.asclepius.R
import com.dicoding.asclepius.database.PredictionHistory
import com.dicoding.asclepius.databinding.PredictionHistoryRowItemBinding
import java.text.NumberFormat
import java.util.Date

class HistoryListAdapter(private val listener: OnHistoryItemClickListener) : ListAdapter<PredictionHistory, HistoryListAdapter.HistoryViewHolder>(HistoryComparator()) {

    interface OnHistoryItemClickListener {
        fun onHistoryItemClicked(history: PredictionHistory)
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HistoryViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = PredictionHistoryRowItemBinding.inflate(inflater, parent, false)
        return HistoryViewHolder(binding)
    }

    override fun onBindViewHolder(holder: HistoryViewHolder, position: Int) {
        val current = getItem(position)
        holder.bind(current)
        holder.itemView.setOnClickListener {
            listener.onHistoryItemClicked(current)
        }
    }

    class HistoryViewHolder(private val binding: PredictionHistoryRowItemBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(data: PredictionHistory) {
            binding.tvResult.text = data.predictionResult
            binding.tvScore.text = NumberFormat.getPercentInstance()
                .format(data.confidenceScore).trim()

            val context = binding.root.context

            if (data.predictionResult == "Cancer") {
                binding.tvResult.setTextColor(ContextCompat.getColor(context, R.color.red))
            } else {
                // Set default color for other cases
                binding.tvResult.setTextColor(ContextCompat.getColor(context, R.color.secondary))
            }

            // Convert insertedAt time to a readable date-time format
            val formattedDateTime = DateFormat.format("dd/MM/yyyy HH:mm:ss", Date(data.insertedAt))

            binding.tvTime.text = formattedDateTime
            Log.d("DBC", "Data : ${data.imagePath}, ${data.predictionResult}, ${data.confidenceScore.toString()}")

            data.imagePath?.let {
                val imageUri = Uri.parse(data.imagePath)
                // Load image using Glide library
                Glide.with(itemView)
                    .load(imageUri)
                    .override(200, 200)
                    .into(binding.ivHistory)
            }

        }
    }

    class HistoryComparator : DiffUtil.ItemCallback<PredictionHistory>() {
        override fun areItemsTheSame(oldItem: PredictionHistory, newItem: PredictionHistory): Boolean {
            return oldItem === newItem
        }

        override fun areContentsTheSame(oldItem: PredictionHistory, newItem: PredictionHistory): Boolean {
            return oldItem.imagePath == newItem.imagePath
        }
    }
}
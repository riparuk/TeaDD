package com.dicoding.asclepius.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.dicoding.asclepius.R
import com.dicoding.asclepius.database.PredictionHistory

class HistoryListAdapter : ListAdapter<PredictionHistory, HistoryListAdapter.HistoryViewHolder>(HistoryComparator()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HistoryViewHolder {
        return HistoryViewHolder.create(parent)
    }

    override fun onBindViewHolder(holder: HistoryViewHolder, position: Int) {
        val current = getItem(position)
        holder.bind(current.predictionResult)
    }

    class HistoryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val historyItemView: TextView = itemView.findViewById(R.id.tvResult)

        fun bind(text: String?) {
            historyItemView.text = text
        }

        companion object {
            fun create(parent: ViewGroup): HistoryViewHolder {
                val view: View = LayoutInflater.from(parent.context)
                    .inflate(R.layout.prediction_history_row_item, parent, false)
                return HistoryViewHolder(view)
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
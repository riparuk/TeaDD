package com.dicoding.asclepius.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.dicoding.asclepius.R
import com.dicoding.asclepius.databinding.ArticleRowItemBinding
import com.dicoding.asclepius.networkapi.response.ArticlesItem

class ArticlesAdapter(private val onItemClick: (ArticlesItem) -> Unit) : RecyclerView.Adapter<ArticlesAdapter.ArticleViewHolder>() {

    private var articles: List<ArticlesItem> = ArrayList()

    inner class ArticleViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val binding = ArticleRowItemBinding.bind(itemView)

        fun bind(article: ArticlesItem) {
            binding.tvTitle.text = article.title
            binding.tvDescription.text = article.description
            Glide.with(itemView.context)
                .load(article.urlToImage)
                .into(binding.ivArticle)

            itemView.setOnClickListener {
                onItemClick(article)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ArticleViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.article_row_item, parent, false)
        return ArticleViewHolder(view)
    }

    override fun onBindViewHolder(holder: ArticleViewHolder, position: Int) {
        holder.bind(articles[position])
    }

    override fun getItemCount(): Int {
        return articles.size
    }

    fun setData(data: List<ArticlesItem?>) {
        articles = data as List<ArticlesItem>
        notifyDataSetChanged()
    }
}
package com.dicoding.asclepius.view

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.dicoding.asclepius.R
import com.dicoding.asclepius.adapter.ArticlesAdapter
import com.dicoding.asclepius.databinding.ActivityArticlesBinding
import com.dicoding.asclepius.viewmodel.ArticlesViewModel

class ArticlesActivity : AppCompatActivity() {
    private lateinit var binding: ActivityArticlesBinding
    private lateinit var viewModel: ArticlesViewModel
    private lateinit var adapter: ArticlesAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityArticlesBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel = ViewModelProvider(this).get(ArticlesViewModel::class.java)

        binding.ivBack.setOnClickListener {
            finish()
        }

        setupRecyclerView()
        observeViewModel()

        viewModel.getTopHeadlines()

    }

    private fun observeViewModel() {
        viewModel.articles.observe(this) { articles ->
            adapter.setData(articles)
        }

        viewModel.isLoading.observe(this) { isLoading ->
            showLoading(isLoading)
        }

        viewModel.toastMessage.observe(this) { message ->
            message?.let {
                Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
                showMessage(message)
            }
        }
    }

    private fun showMessage(message: String) {
        if (message.isNotEmpty()) {
            binding.tvMessage.text = message
            binding.tvMessage.visibility = View.VISIBLE
        } else {
            binding.tvMessage.visibility = View.GONE
        }
    }


    private fun setupRecyclerView() {
        adapter = ArticlesAdapter { article ->
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(article.url))
            startActivity(intent)
        }
        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        binding.recyclerView.adapter = adapter

        binding.recyclerView.addItemDecoration(
            DividerItemDecoration(
                baseContext,
                LinearLayoutManager(this).orientation
            ).apply {
                setDrawable(ContextCompat.getDrawable(this@ArticlesActivity, R.drawable.divider)!!)
            }
        )
    }

    private fun showLoading(isLoading: Boolean) {
        if (isLoading) {
            binding.progressBar.visibility = View.VISIBLE
        } else {
            binding.progressBar.visibility = View.GONE
        }
    }
}


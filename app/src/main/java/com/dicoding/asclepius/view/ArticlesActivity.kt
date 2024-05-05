package com.dicoding.asclepius.view

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.dicoding.asclepius.adapter.ArticlesAdapter
import com.dicoding.asclepius.databinding.ActivityArticlesBinding
import com.dicoding.asclepius.networkapi.retrofit.ApiConfig
import com.dicoding.asclepius.viewmodel.ArticlesViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Response
import java.io.IOException

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
            }
        }
    }


    private fun setupRecyclerView() {
        adapter = ArticlesAdapter()
        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        binding.recyclerView.adapter = adapter
    }

    private fun showLoading(isLoading: Boolean) {
        if (isLoading) {
            binding.progressBar.visibility = View.VISIBLE
        } else {
            binding.progressBar.visibility = View.GONE
        }
    }
}


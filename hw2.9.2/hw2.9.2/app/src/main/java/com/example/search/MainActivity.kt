package com.example.search

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.viewModels
import androidx.core.view.isVisible
import androidx.core.widget.doOnTextChanged
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.search.databinding.ActivityMainBinding
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {

    private lateinit var binding: ActivityMainBinding
    private val viewModel: SearchViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.etSearchQuery.doOnTextChanged { text, _, _, _ ->
            viewModel.onQueryChanged(text.toString())
        }

        binding.btnSearch.setOnClickListener {
            viewModel.onQueryChanged(binding.etSearchQuery.text.toString())
        }

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.uiResults.collect { results ->
                        val query = binding.etSearchQuery.text.toString().trim()
                        binding.tvEmptyState.isVisible = results.isEmpty()
                        if (results.isEmpty() && query.length >= 3) {
                            binding.tvEmptyState.text = "Нічого не знайдено"
                        } else {
                            binding.tvEmptyState.setText(R.string.search_initial_message)
                        }
                        binding.tvSearchResults.text = results.joinToString("\n") { "• $it" }
                    }
                }
                launch {
                    viewModel.isLoading.collect { isLoading ->
                        binding.progressBar.isVisible = isLoading
                    }
                }
            }
        }
    }
}
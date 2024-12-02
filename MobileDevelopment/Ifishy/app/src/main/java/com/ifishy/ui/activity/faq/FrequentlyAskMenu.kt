package com.ifishy.ui.activity.faq

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.ifishy.databinding.ActivityFrequentlyAskMenuBinding
import com.ifishy.ui.adapter.community.AdapterFAQ
import com.ifishy.ui.viewmodel.ViewModelFAQ

class FrequentlyAskMenu : AppCompatActivity() {

    private lateinit var binding: ActivityFrequentlyAskMenuBinding
    private val viewModel: ViewModelFAQ by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()
        binding = ActivityFrequentlyAskMenuBinding.inflate(layoutInflater)
        setContentView(binding.root)


        val adapter = AdapterFAQ(emptyList()) { index ->
            viewModel.toggleFaqExpansion(index)
        }

        binding.rvFAQ.layoutManager = LinearLayoutManager(this)
        binding.rvFAQ.adapter = adapter

        viewModel.faqlist.observe(this) { faqList ->
            adapter.updateData(faqList)
        }
    }
}
package com.ifishy.ui.activity.bookmark

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.tabs.TabLayoutMediator
import com.ifishy.R
import com.ifishy.databinding.ActivityBookmarkBinding
import com.ifishy.ui.adapter.boomark.BookmarkAdapterPage
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class BookmarkActivity : AppCompatActivity() {

    private lateinit var binding:ActivityBookmarkBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()
        enableEdgeToEdge()
        binding = ActivityBookmarkBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        binding.back.setOnClickListener {
            finish()
        }

        binding.content.adapter = BookmarkAdapterPage(this)
        TabLayoutMediator(binding.bookmarkSelector, binding.content) { tab, position ->
            tab.text = when (position) {
                0 -> "Artikel"
                1 -> "Postingan"
                else -> throw IllegalStateException("Unexpected tab position: $position")
            }
        }.attach()
    }
}
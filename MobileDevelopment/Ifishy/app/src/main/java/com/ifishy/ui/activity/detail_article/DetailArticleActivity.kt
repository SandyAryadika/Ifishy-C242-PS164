package com.ifishy.ui.activity.detail_article

import android.os.Bundle
import android.text.Html
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.bumptech.glide.Glide
import com.ifishy.R
import com.ifishy.databinding.ActivityDetailBinding
import com.ifishy.ui.viewmodel.article.ArticleViewModel
import com.ifishy.utils.Date
import com.ifishy.utils.ResponseState
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DetailArticleActivity : AppCompatActivity() {

    private lateinit var binding:ActivityDetailBinding
    private val articleViewModel: ArticleViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        supportActionBar?.hide()
        binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        binding.back.setOnClickListener { finish() }

        val id = intent.getIntExtra(ID,0)

        getArticleById(id)

    }

    private fun isLoading(loading:Boolean){
        if (loading){
            binding.loadingDetail.apply {
                this.startShimmer()
                this.visibility = View.VISIBLE
            }
            binding.articleContent.visibility = View.GONE
        }else{
            binding.loadingDetail.apply {
                this.stopShimmer()
                this.visibility = View.GONE
            }
            binding.articleContent.visibility = View.VISIBLE
        }
    }

    private fun getArticleById(id:Int){
        articleViewModel.getArticleById(id).apply {
            articleViewModel.articleById.observe(this@DetailArticleActivity){response->
                when(response){
                    is ResponseState.Loading -> {
                        isLoading(true)
                    }
                    is ResponseState.Success -> {
                        isLoading(false)
                        response.data.data?.let {
                            binding.articleTitle.text = it.title
                            binding.author.text = it.author
                            binding.dateUpload.text = Date.format(it.publishedAt!!)
                            Glide.with(this@DetailArticleActivity)
                                .load(it.coverImage)
                                .into(binding.img)
                            binding.descriptionArticle.text = Html.fromHtml(response.data.data.content,Html.FROM_HTML_MODE_LEGACY)
                        }

                    }
                    is ResponseState.Error -> {
                        isLoading(false)

                    }
                }}
        }
    }


    companion object {
        const val ID = "ARTICLE_ID"
    }

}
package com.ifishy.ui.activity.detail_article

import android.content.Intent
import android.os.Bundle
import android.text.Html
import android.view.View
import android.view.Window
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.bumptech.glide.Glide
import com.ifishy.R
import com.ifishy.data.model.bookmark.BookmarkRequest
import com.ifishy.data.preference.PreferenceViewModel
import com.ifishy.databinding.ActivityDetailBinding
import com.ifishy.ui.viewmodel.BookmarkViewModel
import com.ifishy.ui.viewmodel.article.ArticleViewModel
import com.ifishy.utils.Date
import com.ifishy.utils.ResponseState
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DetailArticleActivity : AppCompatActivity() {

    private lateinit var binding:ActivityDetailBinding
    private val articleViewModel: ArticleViewModel by viewModels()
    private val bookmarkViewModel: BookmarkViewModel by viewModels()
    private val preferenceViewModel: PreferenceViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()
        binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.back.setOnClickListener { finish() }

        val id = intent.getIntExtra(ID,0)

        getArticleById(id)

        preferenceViewModel.token.observe(this){token->
            isBookmarked(token,id)
            binding.bookmarkButton.setOnClickListener {
                isBookmarked(token,id){ isBookmark->
                    if (isBookmark){
                        removeBookmark(token,BookmarkRequest(id,"article"))
                        binding.bookmarkButton.setImageResource(R.drawable.bookmark_false)
                    }else{
                        setBookmark(token,BookmarkRequest(id,"article"))
                        binding.bookmarkButton.setImageResource(R.drawable.bookmark_true)
                    }
                }
            }
        }

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

    private fun isBookmarked(token: String, id: Int, callback:((Boolean) -> Unit)?=null ) {
        bookmarkViewModel.getAllBookmark(token)
        bookmarkViewModel.allBookmark.observe(this@DetailArticleActivity) { response ->
            when (response) {
                is ResponseState.Error -> {
                    binding.bookmarkButton.isEnabled = true
                    callback?.let {
                        it(false)
                    }
                }
                is ResponseState.Loading -> {
                    binding.bookmarkButton.isEnabled = false
                }
                is ResponseState.Success -> {
                    binding.bookmarkButton.isEnabled = true
                    val exist = response.data.bookmarks
                        ?.filter { it.type == "article" }
                        ?.any { it.itemId == id } == true

                    binding.bookmarkButton.setImageResource(
                        if (exist) R.drawable.bookmark_true else R.drawable.bookmark_false
                    )

                    callback?.invoke(exist)
                }
            }
        }
    }

    private fun setBookmark(token:String,item: BookmarkRequest){
        bookmarkViewModel.setBookmark(token,item).apply {
            bookmarkViewModel.setBookmark.observe(this@DetailArticleActivity){event->
                event.getContentIfNotHandled()?.let { response->
                    when(response){
                        is ResponseState.Loading -> {
                            binding.bookmarkButton.isEnabled = false
                        }
                        is ResponseState.Success -> {
                            binding.bookmarkButton.isEnabled = true
                            Toast.makeText(this@DetailArticleActivity,
                                getString(R.string.added_to_bookmark), Toast.LENGTH_SHORT).show()
                        }
                        is ResponseState.Error -> {
                            binding.bookmarkButton.isEnabled = true
                        }
                    }
                }
            }
        }
    }

    private fun removeBookmark(token:String,item: BookmarkRequest){
        bookmarkViewModel.deleteBookmark(token,item).apply {
            bookmarkViewModel.deleteBookmark.observe(this@DetailArticleActivity){event->
                event.getContentIfNotHandled()?.let { response->
                    when(response){
                        is ResponseState.Loading -> {
                            binding.bookmarkButton.isEnabled = false
                        }
                        is ResponseState.Success -> {
                            binding.bookmarkButton.isEnabled = true
                            Toast.makeText(this@DetailArticleActivity,
                                getString(R.string.removed_from_bookmark), Toast.LENGTH_SHORT).show()
                        }
                        is ResponseState.Error -> {
                            binding.bookmarkButton.isEnabled = true
                        }
                    }
                }
            }
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
                            binding.shareButton.setOnClickListener {
                                val intent = Intent(Intent.ACTION_SEND).apply {
                                    type = "text/plain"
                                    putExtra(Intent.EXTRA_TEXT, """
                                        ${response.data.data.title}
                                        
                                        Baca Selengkapnya di : Ifishy
                                    """.trimIndent())
                                }
                                startActivity(Intent.createChooser(intent, "Bagikan menggunakan"))
                            }
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
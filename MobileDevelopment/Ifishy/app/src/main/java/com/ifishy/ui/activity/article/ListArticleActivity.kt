package com.ifishy.ui.activity.article

import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.Window
import android.view.WindowInsets
import android.view.inputmethod.InputMethodManager
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.ifishy.R
import com.ifishy.data.model.article.DataItem
import com.ifishy.data.preference.PreferenceViewModel
import com.ifishy.databinding.ActivityListArticleBinding
import com.ifishy.ui.activity.detail_article.DetailArticleActivity
import com.ifishy.ui.adapter.article.ArticleHorizontalAdapter
import com.ifishy.ui.adapter.article.ArticleVerticalAdapter
import com.ifishy.ui.viewmodel.article.ArticleViewModel
import com.ifishy.utils.ResponseState
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ListArticleActivity : AppCompatActivity() {

    private lateinit var binding:ActivityListArticleBinding
    private val articleViewModel: ArticleViewModel by viewModels()
    private var listArticle: List<DataItem>?=null
    private var adapter : ArticleVerticalAdapter?=null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()
        binding = ActivityListArticleBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val search = intent.getBooleanExtra("Search",false)
        if (search){
            openKeyboard(binding.searchArticle)
        }

        binding.back.setOnClickListener {
            finish()
        }

        binding.searchArticle.addTextChangedListener(object : TextWatcher{
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun afterTextChanged(search: Editable?) {
                search.let {
                    if (search.isNullOrEmpty()){
                        listArticle?.let {
                            adapter?.submitData(it)
                        }
                    }else{
                        listArticle?.filter { it.title?.trim()?.contains(search.trim(),ignoreCase = true) == true }.let {
                            if (it != null) {
                                adapter?.submitData(it)
                            }
                        }
                    }
                }
            }

        })

        getAllArticle()

    }

    private fun isLoading(loading:Boolean){
        if (loading){
            binding.loadingVertical.apply {
                this.startShimmer()
                this.visibility = View.VISIBLE
            }
            binding.allArticles.visibility = View.GONE
            binding.error.visibility = View.GONE
        }else{
            binding.loadingVertical.apply {
                this.stopShimmer()
                this.visibility = View.GONE
            }
            binding.allArticles.visibility = View.VISIBLE
            binding.error.visibility = View.GONE
        }
    }

    private fun openKeyboard(view: View) {
        view.requestFocus()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            view.windowInsetsController?.show(WindowInsets.Type.ime())
        } else {
            val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.showSoftInput(view, InputMethodManager.SHOW_IMPLICIT)
        }
    }

    private fun getAllArticle(){
        articleViewModel.getArticle().apply {
            articleViewModel.article.observe(this@ListArticleActivity){response->
                when(response){
                    is ResponseState.Loading -> {
                        isLoading(true)
                    }
                    is ResponseState.Success -> {
                        isLoading(false)
                        listArticle = response.data.data
                        this@ListArticleActivity.adapter = response.data.data?.let {
                            ArticleVerticalAdapter(
                                it
                            )
                        }
                        this@ListArticleActivity.adapter?.onItemClicked(object : ArticleVerticalAdapter.OnClickArticlesVertical{
                            override fun toDetail(id: Int) {
                                startActivity(Intent(this@ListArticleActivity,DetailArticleActivity::class.java)
                                    .putExtra(DetailArticleActivity.ID,id)
                                )
                            }

                        })
                        binding.allArticles.apply {
                            this.adapter = this@ListArticleActivity.adapter
                            this.layoutManager = LinearLayoutManager(this@ListArticleActivity)
                        }

                    }
                    is ResponseState.Error -> {
                        isLoading(false)
                        binding.error.visibility = View.VISIBLE
                        binding.error.text = response.message
                    }
                }
            }
        }
    }
}
package com.ifishy.ui.activity.bookmark.fragmentBookmark

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.ifishy.R
import com.ifishy.data.model.article.DataItem
import com.ifishy.data.preference.PreferenceViewModel
import com.ifishy.databinding.FragmentBookmarkArticleBinding
import com.ifishy.databinding.FragmentBookmarkPostBinding
import com.ifishy.ui.activity.detail_article.DetailArticleActivity
import com.ifishy.ui.adapter.article.ArticleVerticalAdapter
import com.ifishy.ui.viewmodel.BookmarkViewModel
import com.ifishy.ui.viewmodel.article.ArticleViewModel
import com.ifishy.utils.ResponseState
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class BookmarkArticleFragment : Fragment() {

    private var _binding: FragmentBookmarkArticleBinding?=null
    private val binding get() = _binding!!
    private val bookmarkViewModel: BookmarkViewModel by viewModels()
    private val articleViewModel: ArticleViewModel by viewModels()
    private val preferenceViewModel: PreferenceViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentBookmarkArticleBinding.inflate(layoutInflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        preferenceViewModel.token.observe(viewLifecycleOwner){token->
            getAllArticleBookmark(token)
        }

    }

    private fun isLoading(loading:Boolean){
        if (loading){
            binding.loading.apply {
                this.startShimmer()
                this.visibility =View.VISIBLE
            }
            binding.rvArticle.visibility = View.GONE
            binding.error.visibility = View.GONE
        }else{
            binding.loading.apply {
                this.stopShimmer()
                this.visibility =View.GONE
            }
            binding.rvArticle.visibility = View.VISIBLE
            binding.error.visibility = View.GONE
        }
    }

    private fun getAllArticleBookmark(token:String){
        bookmarkViewModel.listArticle.clear()
        bookmarkViewModel.getAllBookmark(token).apply {
            bookmarkViewModel.allBookmark.observe(viewLifecycleOwner){response->
                when(response){
                    is ResponseState.Loading -> {
                        isLoading(true)
                    }
                    is ResponseState.Success -> {
                            response.data.bookmarks?.filter { it.type == "article" }.let { article ->
                                val listId: MutableList<Int> = mutableListOf()
                                article?.forEach { item->
                                    item.itemId?.let { listId.add(it) }
                                }

                                articleViewModel.getArticle().apply {
                                    articleViewModel.article.observe(viewLifecycleOwner){ article->
                                        when(article){
                                            is ResponseState.Loading -> {}
                                            is ResponseState.Success -> {
                                                val filtered = article.data.data?.filter { it.id in listId }
                                                if (filtered?.isEmpty() == true){
                                                    isLoading(false)
                                                    binding.error.apply {
                                                        this.text = ContextCompat.getString(requireContext(),R.string.no_bookmark_yet)
                                                        this.visibility = View.VISIBLE
                                                    }
                                                }else{
                                                    filtered?.forEach { item->
                                                        bookmarkViewModel.listArticle.add(item)
                                                    }
                                                    isLoading(false)
                                                    val adapter = ArticleVerticalAdapter(bookmarkViewModel.listArticle)
                                                    binding.rvArticle.adapter = adapter
                                                    binding.rvArticle.layoutManager = LinearLayoutManager(requireActivity())
                                                    adapter.onItemClicked(object : ArticleVerticalAdapter.OnClickArticlesVertical{
                                                        override fun toDetail(id: Int) {
                                                            startActivity(Intent(requireContext(),DetailArticleActivity::class.java)
                                                                .putExtra(DetailArticleActivity.ID,id)
                                                            )
                                                        }

                                                    })
                                                }
                                            }
                                            is ResponseState.Error -> {
                                                isLoading(false)
                                                binding.error.apply {
                                                    this.text = ContextCompat.getString(requireContext(),R.string.no_bookmark_yet)
                                                    this.visibility = View.VISIBLE
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                    }
                    is ResponseState.Error -> {
                        isLoading(false)
                        binding.error.apply {
                            this.text = response.message
                            this.visibility = View.VISIBLE
                        }
                    }
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

}
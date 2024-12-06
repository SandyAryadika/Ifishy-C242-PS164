package com.ifishy.ui.fragment.home

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AccelerateDecelerateInterpolator
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSnapHelper
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.ifishy.R
import com.ifishy.data.preference.PreferenceViewModel
import com.ifishy.databinding.FragmentHomeBinding
import com.ifishy.ui.activity.article.ListArticleActivity
import com.ifishy.ui.activity.detail_article.DetailArticleActivity
import com.ifishy.ui.adapter.article.ArticleHorizontalAdapter
import com.ifishy.ui.adapter.article.ArticleVerticalAdapter
import com.ifishy.ui.viewmodel.article.ArticleViewModel
import com.ifishy.ui.viewmodel.profile.ProfileViewModel
import com.ifishy.utils.ResponseState
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HomeFragment : Fragment() {

    private val preferenceViewModel: PreferenceViewModel by viewModels()
    private val profileViewModel: ProfileViewModel by viewModels()
    private val articleViewModel: ArticleViewModel by viewModels()
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val snapHelper = LinearSnapHelper()
        snapHelper.attachToRecyclerView(binding.articleHorizontal)

        preferenceViewModel.token.observe(viewLifecycleOwner) { token ->
            if (token.isNotEmpty()) {
                preferenceViewModel.email.observe(viewLifecycleOwner) { email ->
                    getProfileInfo(token, email)
                }
                getAllArticle()
            }
        }

        binding.see.setOnClickListener {
            gofull(false)
        }

        binding.searchviewhome.setOnFocusChangeListener{_,hasFocus->
            if (hasFocus){
                binding.searchviewhome.clearFocus()
                gofull(true)
            }
        }

    }

    private fun gofull(search:Boolean){
        startActivity(Intent(requireActivity(),ListArticleActivity::class.java)
            .putExtra("Search",search)
        )
    }

    private fun isProfileLoading(loading: Boolean) {
        if (loading) {
            binding.profile.visibility = View.INVISIBLE
            binding.loading.visibility = View.VISIBLE
            binding.loading.startShimmer()
        } else {
            binding.profile.visibility = View.VISIBLE
            binding.loading.visibility = View.GONE
            binding.loading.stopShimmer()
        }
    }

    private fun getProfileInfo(token: String, email: String) {
        profileViewModel.getProfile(token, email).apply {
            profileViewModel.profile.observe(viewLifecycleOwner) { response ->
                when (response) {
                    is ResponseState.Loading -> {
                        isProfileLoading(true)
                    }
                    is ResponseState.Success -> {
                        isProfileLoading(false)
                        binding.username.text = response.data.profile?.username
                        Glide.with(requireActivity())
                            .load(response.data.profile?.profilePhoto)
                            .placeholder(
                                ContextCompat.getDrawable(
                                    requireActivity(),
                                    R.drawable.user_placeholder
                                )
                            )
                            .error(
                                ContextCompat.getDrawable(
                                    requireActivity(),
                                    R.drawable.user_placeholder
                                )
                            )
                            .into(binding.pp)
                    }

                    is ResponseState.Error -> {
                        isProfileLoading(false)
                    }
                }
            }
        }
    }

    private fun isLoading(loading: Boolean) {
        if (loading) {
            binding.horizontalLoading.apply {
                this.startShimmer()
                this.visibility = View.VISIBLE
            }
            binding.loadingVertical.apply {
                this.startShimmer()
                this.visibility = View.VISIBLE
            }
            binding.articleHorizontal.visibility = View.INVISIBLE
            binding.verticalArticle.visibility = View.INVISIBLE
            binding.error.visibility = View.GONE
            binding.articleContent.visibility = View.VISIBLE
        } else {
            binding.horizontalLoading.apply {
                this.stopShimmer()
                this.visibility = View.INVISIBLE
            }
            binding.loadingVertical.apply {
                this.stopShimmer()
                this.visibility = View.INVISIBLE
            }
            binding.articleHorizontal.visibility = View.VISIBLE
            binding.verticalArticle.visibility = View.VISIBLE
            binding.error.visibility = View.GONE
            binding.articleContent.visibility = View.VISIBLE
        }
    }

    private fun getAllArticle() {
        articleViewModel.getArticle().apply {
            articleViewModel.article.observe(viewLifecycleOwner) { response ->
                when (response) {
                    is ResponseState.Loading -> {
                        isLoading(true)
                    }
                    is ResponseState.Success -> {
                        response.data.data?.let {
                            isLoading(false)
                            val horizontalAdapter = ArticleHorizontalAdapter(it)
                            val verticalAdapter = ArticleVerticalAdapter(it.shuffled(),4)

                            binding.articleHorizontal.adapter = horizontalAdapter
                            binding.verticalArticle.adapter = verticalAdapter

                            binding.verticalArticle.layoutManager =
                                LinearLayoutManager(requireActivity())
                            binding.articleHorizontal.layoutManager = LinearLayoutManager(
                                requireActivity(),
                                LinearLayoutManager.HORIZONTAL,
                                false
                            )

                            binding.articleHorizontal.post {
                                binding.articleHorizontal.smoothScrollToPosition(2)
                            }
                            binding.articleHorizontal.addOnScrollListener(object :
                                RecyclerView.OnScrollListener() {
                                override fun onScrollStateChanged(
                                    recyclerView: RecyclerView,
                                    newState: Int
                                ) {
                                    super.onScrollStateChanged(recyclerView, newState)

                                    val layoutManager =
                                        recyclerView.layoutManager as LinearLayoutManager
                                    val firstVisiblePosition =
                                        layoutManager.findFirstVisibleItemPosition()
                                    val lastVisiblePosition =
                                        layoutManager.findLastVisibleItemPosition()

                                    for (position in firstVisiblePosition..lastVisiblePosition) {
                                        val view = layoutManager.findViewByPosition(position)
                                        val isSelected =
                                            position == layoutManager.findFirstCompletelyVisibleItemPosition()

                                        view?.let { v->
                                            val scale = if (isSelected) 1f else 0.9f

                                            if (isSelected) {
                                                v.animate()
                                                    .scaleX(scale)
                                                    .scaleY(scale)
                                                    .setDuration(400)
                                                    .setInterpolator(
                                                        AccelerateDecelerateInterpolator()
                                                    )
                                                    .start()
                                            } else {
                                                v.animate()
                                                    .scaleX(scale)
                                                    .scaleY(scale)
                                                    .setDuration(400)
                                                    .setInterpolator(
                                                        AccelerateDecelerateInterpolator()
                                                    )
                                                    .start()
                                            }
                                        }
                                    }
                                }
                            })

                            verticalAdapter.onItemClicked(object :
                                ArticleVerticalAdapter.OnClickArticlesVertical {
                                override fun toDetail(id: Int) {
                                    val intent = Intent(
                                        requireContext(),
                                        DetailArticleActivity::class.java
                                    ).apply {
                                        putExtra(DetailArticleActivity.ID, id)
                                    }
                                    startActivity(intent)
                                }

                            })
                            horizontalAdapter.onItemClicked(object :
                                ArticleHorizontalAdapter.OnClickArticle {
                                override fun toDetail(id: Int) {
                                    val intent = Intent(
                                        requireContext(),
                                        DetailArticleActivity::class.java
                                    ).apply {
                                        putExtra(DetailArticleActivity.ID, id)
                                    }
                                    startActivity(intent)
                                }

                            })
                        }

                    }

                    is ResponseState.Error -> {
                        isLoading(false)
                        binding.error.visibility = View.VISIBLE
                        binding.error.text = response.message
                        binding.articleContent.visibility = View.GONE
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
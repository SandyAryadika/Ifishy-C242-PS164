package com.ifishy.ui.fragment.home

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.ifishy.R
import com.ifishy.data.preference.PreferenceViewModel
import com.ifishy.databinding.FragmentHomeBinding
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

        preferenceViewModel.token.observe(viewLifecycleOwner) { token ->
            if (token.isNotEmpty()) {
                preferenceViewModel.email.observe(viewLifecycleOwner) { email ->
                    getProfileInfo(token, email)
                }
                getAllArticle()
            }
        }

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
                        binding.username.text = "a"
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
                            val verticalAdapter = ArticleVerticalAdapter(it)

                            binding.articleHorizontal.adapter = horizontalAdapter
                            binding.verticalArticle.adapter = verticalAdapter

                            binding.verticalArticle.layoutManager =
                                LinearLayoutManager(requireActivity())
                            binding.articleHorizontal.layoutManager = LinearLayoutManager(
                                requireActivity(),
                                LinearLayoutManager.HORIZONTAL,
                                false
                            )

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
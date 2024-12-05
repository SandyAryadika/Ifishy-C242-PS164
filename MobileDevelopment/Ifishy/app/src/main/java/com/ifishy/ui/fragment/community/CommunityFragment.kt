package com.ifishy.ui.fragment.community

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.ifishy.R
import com.ifishy.data.model.community.response.PostsItem
import com.ifishy.data.preference.PreferenceViewModel
import com.ifishy.databinding.FragmentCommunityBinding
import com.ifishy.ui.activity.add_post.AddPostActivity
import com.ifishy.ui.activity.detail_post.DetailPostActivity
import com.ifishy.ui.adapter.community.post.CommunityPostsAdapter
import com.ifishy.ui.viewmodel.community.CommunityViewModel
import com.ifishy.utils.ResponseState
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CommunityFragment : Fragment() {

    private val communityViewModel: CommunityViewModel by viewModels()
    private val preferencesViewModel: PreferenceViewModel by viewModels()
    private var _binding: FragmentCommunityBinding? = null
    private val binding get() = _binding!!
    private var listCommunity: List<PostsItem>? = null
    private val adapter by lazy { CommunityPostsAdapter() }
    private var searchValue: String? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCommunityBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        preferencesViewModel.token.observe(viewLifecycleOwner) { token ->
            getALlPosts(token)
        }

        binding.addPost.setOnClickListener {
            startActivity(Intent(requireActivity(), AddPostActivity::class.java))
        }

        binding.search.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun afterTextChanged(search: Editable?) {
                search.let {
                    searchValue = search.toString()
                    if (search.isNullOrEmpty()) {
                        listCommunity?.let {
                            adapter.submitData(it)
                        }
                    } else {
                        listCommunity?.filter {
                            it.title?.trim()?.contains(search.trim(), true) == true
                        }
                            ?.let { data -> adapter.submitData(data) }
                    }
                }
            }

        })

    }

    private fun isLoading(loading: Boolean) {
        if (loading) {
            binding.apply {
                this.loading.visibility = View.VISIBLE
                this.loading.startShimmer()
                this.community.visibility = View.GONE
                this.error.visibility = View.GONE
            }
        } else {
            binding.apply {
                this.loading.stopShimmer()
                this.loading.visibility = View.GONE
                this.community.visibility = View.VISIBLE
            }
        }
    }

    private fun getALlPosts(token: String) {
        communityViewModel.getAllPosts(token).apply {
            communityViewModel.posts.observe(viewLifecycleOwner) { response ->
                when (response) {
                    is ResponseState.Loading -> {
                        isLoading(true)
                    }

                    is ResponseState.Success -> {
                        isLoading(false)
                        if (response.data.posts?.isEmpty() == true) {
                            binding.community.visibility = View.GONE
                            binding.error.apply {
                                this.visibility = View.VISIBLE
                                this.text =
                                    ContextCompat.getString(requireActivity(), R.string.no_post)
                            }
                        } else {
                            binding.community.visibility = View.VISIBLE
                            binding.error.apply {
                                this.visibility = View.GONE
                                this.text = ""
                            }
                            adapter.apply {
                                response.data.posts?.let { this.submitData(it) }
                                this.onItemClicked(object : CommunityPostsAdapter.OnClick {
                                    override fun getDetail(id: Int?) {
                                        startActivity(
                                            Intent(
                                                requireActivity(),
                                                DetailPostActivity::class.java
                                            )
                                                .putExtra(DetailPostActivity.POST_ID, id)
                                        )
                                    }

                                    override fun upVote(id: Int?) = upVote(token, id!!)
                                    override fun downVote(id: Int?) = downVote(token, id!!)
                                })
                            }
                            binding.community.apply {
                                this.adapter = this@CommunityFragment.adapter
                                this.layoutManager = LinearLayoutManager(requireActivity())
                            }
                            response.data.posts?.let {
                                listCommunity = it
                            }
                        }

                    }


                    is ResponseState.Error -> {
                        isLoading(false)
                        binding.community.visibility = View.GONE
                        binding.error.apply {
                            this.text = response.message
                            this.visibility = View.VISIBLE
                        }
                    }
                }
            }
        }
    }

    private fun upVote(token: String, id: Int) {
        communityViewModel.addUpvote(token, id).apply {
            communityViewModel.addUpvote.observe(viewLifecycleOwner) { event ->
                event.getContentIfNotHandled()
            }
        }
    }

    private fun downVote(token: String, id: Int) {
        communityViewModel.addDownVote(token, id).apply {
            communityViewModel.addDownVote.observe(viewLifecycleOwner) { event ->
                event.getContentIfNotHandled()
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}
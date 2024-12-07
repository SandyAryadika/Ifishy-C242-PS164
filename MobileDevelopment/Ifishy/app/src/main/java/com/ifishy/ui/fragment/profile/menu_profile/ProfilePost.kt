package com.ifishy.ui.fragment.profile.menu_profile

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.ifishy.R
import com.ifishy.databinding.FragmentProfilePostBinding
import com.ifishy.ui.adapter.community.post.CommunityPostsAdapter
import com.ifishy.ui.viewmodel.community.CommunityViewModel
import com.ifishy.data.preference.PreferenceViewModel
import com.ifishy.ui.activity.detail_post.DetailPostActivity
import com.ifishy.utils.ResponseState
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ProfilePost : Fragment() {

    private val communityViewModel: CommunityViewModel by viewModels()
    private val preferenceViewModel: PreferenceViewModel by viewModels()
    private var _binding: FragmentProfilePostBinding? = null
    private val binding get() = _binding!!
    private val adapter by lazy { CommunityPostsAdapter() }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfilePostBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.profilePost.apply {
            adapter = this@ProfilePost.adapter
            layoutManager = LinearLayoutManager(requireActivity())
        }


        preferenceViewModel.token.observe(viewLifecycleOwner) { token ->
                getPosts(token)
        }

    }

    private fun isLoading(loading:Boolean){
        if (loading){
            binding.loadingMypost.apply {
                this.visibility = View.VISIBLE
                this.startShimmer()
            }
            binding.profilePost.visibility = View.GONE
            binding.error.visibility = View.GONE
        }else{
            binding.loadingMypost.apply {
                this.visibility = View.GONE
                this.stopShimmer()
            }
            binding.profilePost.visibility = View.VISIBLE
            binding.error.visibility = View.GONE
        }
    }

    private fun getPosts(token: String) {
        communityViewModel.getAllPosts(token).apply {
            communityViewModel.posts.observe(viewLifecycleOwner) { response ->
                when(response){
                    is ResponseState.Loading -> {
                        isLoading(true)
                    }
                    is ResponseState.Success -> {
                        isLoading(false)
                        if (response.data.posts?.isNotEmpty() == true){
                            adapter.submitData(response.data.posts)
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
                        }else{
                            binding.error.apply {
                                this.visibility = View.VISIBLE
                                this.text = ContextCompat.getString(requireContext(),R.string.no_post)
                            }
                        }
                    }
                    is ResponseState.Error -> {
                        isLoading(false)
                        binding.error.apply {
                            this.visibility = View.GONE
                            this.text = response.message
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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

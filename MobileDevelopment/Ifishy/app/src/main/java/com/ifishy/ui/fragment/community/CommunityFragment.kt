package com.ifishy.ui.fragment.community

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.ifishy.data.preference.PreferenceViewModel
import com.ifishy.databinding.FragmentCommunityBinding
import com.ifishy.ui.activity.detail_post.DetailPostActivity
import com.ifishy.ui.adapter.community.CommunityPostsAdapter
import com.ifishy.ui.viewmodel.CommunityViewModel
import com.ifishy.utils.ResponseState
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CommunityFragment : Fragment() {

    private val communityViewModel: CommunityViewModel by viewModels()
    private val preferencesViewModel : PreferenceViewModel by viewModels()
    private var _binding: FragmentCommunityBinding?=null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCommunityBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        preferencesViewModel.token.observe(viewLifecycleOwner){token->
            getALlPosts(token)
        }

    }

    private fun isLoading(loading:Boolean){
        if (loading){
            binding.apply {
                this.loading.visibility = View.VISIBLE
                this.loading.startShimmer()
                this.community.visibility = View.GONE
                this.error.visibility = View.GONE
            }
        }else{
            binding.apply {
                this.loading.stopShimmer()
                this.loading.visibility = View.GONE
                this.community.visibility = View.VISIBLE
            }
        }
    }

    private fun getALlPosts(token: String) {
        communityViewModel.getAllPosts(token).apply {
            communityViewModel.posts.observe(viewLifecycleOwner){response->
                when(response){
                    is ResponseState.Loading -> {
                        isLoading(true)
                    }
                    is ResponseState.Success -> {
                        val adapter = response.data.posts?.let { CommunityPostsAdapter(it) }
                        adapter?.onItemClicked(object : CommunityPostsAdapter.OnClick{
                            override fun getDetail(id: Int?) {
                                startActivity(Intent(requireActivity(),DetailPostActivity::class.java)
                                    .putExtra(DetailPostActivity.POST_ID,id)
                                )
                            }
                        })
                        isLoading(false)
                        binding.community.apply {
                            this.adapter = adapter
                            this.layoutManager = LinearLayoutManager(requireActivity())
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

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}
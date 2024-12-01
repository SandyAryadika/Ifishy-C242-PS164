package com.ifishy.ui.fragment.community

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.ifishy.data.preference.PreferenceViewModel
import com.ifishy.databinding.FragmentCommunityBinding
import com.ifishy.ui.adapter.CommunityAdapter
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
                this.laoding.visibility = View.VISIBLE
                this.community.visibility = View.GONE
            }
        }else{
            binding.apply {
                this.laoding.visibility = View.GONE
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
                        isLoading(false)
                        binding.community.apply {
                            this.adapter = response.data.posts?.let { CommunityAdapter(it) }
                            this.layoutManager = LinearLayoutManager(requireActivity())
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
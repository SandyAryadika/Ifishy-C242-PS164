package com.ifishy.ui.fragment.community

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.ifishy.databinding.FragmentCommunityBinding
import com.ifishy.ui.adapter.ArticleAdapter
import com.ifishy.ui.viewmodel.ArticleViewModel
import com.ifishy.utils.ResponseState


class CommunityFragment : Fragment() {

    private val articleViewModel: ArticleViewModel by viewModels()
    private lateinit var binding: FragmentCommunityBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentCommunityBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val token = "user_token"
        val adapter = ArticleAdapter()
        binding.rvCommunity.adapter = adapter
        binding.rvCommunity.layoutManager = LinearLayoutManager(requireContext())

        observeViewModel()

        viewModel.fetchPosts(token)
    }

    private fun observeViewModel() {
        viewModel.CommunityResponse.observe(viewLifecycleOwner) { response ->
            response.getContentIfNotHandled()?.let { state ->
                when (state) {
                    is ResponseState.Success -> {


                    }

                    is ResponseState.Error -> {

                    }

                }
            }

        }
    }
}
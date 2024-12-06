package com.ifishy.ui.fragment.profile.menu_profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.ifishy.databinding.FragmentProfilePostBinding
import com.ifishy.ui.adapter.community.post.CommunityPostsAdapter
import com.ifishy.ui.viewmodel.community.CommunityViewModel
import com.ifishy.data.preference.PreferenceViewModel
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
            if (!token.isNullOrEmpty()) {
                getPosts(token)
            }
        }

        communityViewModel.posts.observe(viewLifecycleOwner) { response ->
            if (response is ResponseState.Success) {
                response.data.posts?.let { adapter.submitData(it) }
            }
        }
    }

    private fun getPosts(token: String) {
        communityViewModel.getAllPosts(token)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

package com.ifishy.ui.fragment.community

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.ifishy.R
import com.ifishy.data.preference.PreferenceViewModel
import com.ifishy.databinding.FragmentCommentsModalBinding
import com.ifishy.ui.adapter.community.PostsCommentsAdapter
import com.ifishy.ui.viewmodel.CommunityViewModel
import com.ifishy.ui.viewmodel.ProfileViewModel
import com.ifishy.utils.ResponseState
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CommentsModalFragment : BottomSheetDialogFragment() {

    private val preferenceViewModel: PreferenceViewModel by viewModels()
    private val communityViewModel: CommunityViewModel by viewModels()
    private val profileViewModel: ProfileViewModel by viewModels()
    private var _binding:FragmentCommentsModalBinding?=null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCommentsModalBinding.inflate(layoutInflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        preferenceViewModel.token.observe(viewLifecycleOwner){token->
            getAllComments(token,arguments?.getInt("ID")!!)
            preferenceViewModel.email.observe(viewLifecycleOwner){email->
                getMyProfile(token,email)
            }
        }
    }

    private fun getMyProfile(token: String,email:String){
        profileViewModel.getProfile(token,email).apply {
            profileViewModel.profile.observe(viewLifecycleOwner){response->
                when(response){
                    is ResponseState.Loading -> {
                        binding.profile.setImageDrawable(ContextCompat.getDrawable(requireActivity(),R.drawable.user_placeholder))
                    }
                    is ResponseState.Success -> {
                        Glide.with(requireActivity())
                            .load(response.data.profile?.profilePhoto)
                            .placeholder(ContextCompat.getDrawable(requireActivity(),R.drawable.user_placeholder))
                            .error(ContextCompat.getDrawable(requireActivity(),R.drawable.user_placeholder))
                            .into(binding.profile)
                    }
                    is ResponseState.Error -> {
                        binding.profile.setImageDrawable(ContextCompat.getDrawable(requireActivity(),R.drawable.user_placeholder))
                    }
                }
            }
        }
    }

    private fun isLoading(loading:Boolean){
        if (loading){
            binding.loading.visibility = View.VISIBLE
            binding.comments.visibility = View.INVISIBLE
        }else{
            binding.loading.visibility = View.GONE
            binding.comments.visibility = View.VISIBLE
        }
    }

    private fun getAllComments(token:String,id:Int){
        communityViewModel.getAllComments(token,id).apply {
            communityViewModel.comments.observe(viewLifecycleOwner){response->
                when(response){
                    is ResponseState.Loading -> {
                        isLoading(true)
                    }
                    is ResponseState.Success -> {
                        isLoading(false)
                        val adapter = response.data.comments?.let { PostsCommentsAdapter(it) }
                        binding.comments.apply {
                            this.adapter = adapter
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
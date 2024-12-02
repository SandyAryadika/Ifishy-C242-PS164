package com.ifishy.ui.fragment.home

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import com.bumptech.glide.Glide
import com.ifishy.R
import com.ifishy.data.preference.PreferenceViewModel
import com.ifishy.databinding.FragmentHomeBinding
import com.ifishy.ui.viewmodel.ProfileViewModel
import com.ifishy.utils.ResponseState
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HomeFragment : Fragment() {

    private val preferenceViewModel: PreferenceViewModel by viewModels()
    private val profileViewModel: ProfileViewModel by viewModels()
    private var _binding: FragmentHomeBinding?=null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(layoutInflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        preferenceViewModel.token.observe(viewLifecycleOwner){token->
            if (token.isNotEmpty()){
                preferenceViewModel.email.observe(viewLifecycleOwner){email->
                    getProfileInfo(token,email)
                }
            }
        }

    }

    private fun getProfileInfo(token:String,email: String){
        profileViewModel.getProfile(token,email).apply {
            profileViewModel.profile.observe(viewLifecycleOwner){response->
                when(response){
                    is ResponseState.Loading -> {}
                    is ResponseState.Success -> {
                        binding.username.text = response.data.profile?.username
                        Glide.with(requireActivity())
                            .load(response.data.profile?.profilePhoto)
                            .placeholder(ContextCompat.getDrawable(requireActivity(),R.drawable.user_placeholder))
                            .error(ContextCompat.getDrawable(requireActivity(),R.drawable.user_placeholder))
                            .into(binding.pp)
                    }
                    is ResponseState.Error -> {
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
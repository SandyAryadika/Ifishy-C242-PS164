package com.ifishy.ui.fragment.profile

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import com.bumptech.glide.Glide
import com.google.android.material.tabs.TabLayoutMediator
import com.ifishy.R
import com.ifishy.data.preference.PreferenceViewModel
import com.ifishy.databinding.FragmentProfileBinding
import com.ifishy.ui.activity.bookmark.BookmarkActivity
import com.ifishy.ui.activity.setting.SettingActivity
import com.ifishy.ui.adapter.profile.ProfilePagerAdapter
import com.ifishy.ui.viewmodel.profile.ProfileViewModel
import com.ifishy.utils.ResponseState
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding?=null
    private val binding get() =_binding!!

    private val preferenceViewModel: PreferenceViewModel by viewModels()
    private val profileViewModel: ProfileViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileBinding.inflate(layoutInflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.settings.setOnClickListener {
            startActivity(Intent(requireActivity(),SettingActivity::class.java))
        }

        binding.bookmark.setOnClickListener {
            startActivity(Intent(requireActivity(),BookmarkActivity::class.java))
        }

        preferenceViewModel.token.observe(viewLifecycleOwner){token->
            preferenceViewModel.email.observe(viewLifecycleOwner){email->
                getProfile(email,token)

                binding.content.adapter = ProfilePagerAdapter(this,email)
                TabLayoutMediator(binding.selector, binding.content) { tab, position ->
                    tab.text = when (position) {
                        0 -> "Personal"
                        1 -> "My Post"
                        else -> null
                    }
                }.attach()
            }
        }
    }

    private fun isLoading(loading:Boolean){
        if (loading){
            binding.loadingUsername.apply {
                this.startShimmer()
                this.visibility = View.VISIBLE
            }
            binding.username.visibility = View.INVISIBLE
        }else{
            binding.loadingUsername.apply {
                this.stopShimmer()
                this.visibility = View.GONE
            }
            binding.username.visibility = View.VISIBLE
        }
    }

    private fun getProfile(email:String,token:String){
        profileViewModel.getProfile(token,email).apply {
            profileViewModel.profile.observe(viewLifecycleOwner){response->
                when(response){
                    is ResponseState.Loading -> {
                        isLoading(true)
                    }
                    is ResponseState.Success -> {
                        isLoading(false)
                        binding.username.text = response.data.profile?.username
                        Glide.with(requireContext())
                            .load(response.data.profile?.profilePhoto)
                            .placeholder(ContextCompat.getDrawable(requireContext(), R.drawable.user_placeholder))
                            .error(ContextCompat.getDrawable(requireContext(), R.drawable.user_placeholder))
                            .into(binding.pp)
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
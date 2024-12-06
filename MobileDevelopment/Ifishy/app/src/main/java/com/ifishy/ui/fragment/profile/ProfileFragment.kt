package com.ifishy.ui.fragment.profile

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.tabs.TabLayoutMediator
import com.ifishy.databinding.FragmentProfileBinding
import com.ifishy.ui.activity.setting.SettingActivity
import com.ifishy.ui.adapter.profile.ProfilePagerAdapter


class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding?=null
    private val binding get() =_binding!!
    private lateinit var profilepageadapter: ProfilePagerAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileBinding.inflate(layoutInflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        profilepageadapter = ProfilePagerAdapter(this)

        binding.viewPager.adapter = profilepageadapter

        TabLayoutMediator(binding.tabLayout, binding.viewPager) { tab, position ->
            tab.text = when (position) {
                0 -> "Personal"
                1 -> "My Post"
                else -> null
            }
        }.attach()

        binding.settings.setOnClickListener {
            startActivity(Intent(requireActivity(),SettingActivity::class.java))
        }

        binding.back.setOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }

    }

    override fun onDestroy() {
        super.onDestroy()

        _binding = null
    }

}
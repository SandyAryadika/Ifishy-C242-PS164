package com.ifishy.ui.adapter.profile

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.ifishy.ui.fragment.profile.menu_profile.ProfilePersonal
import com.ifishy.ui.fragment.profile.menu_profile.ProfilePost

class ProfilePagerAdapter(fragment: Fragment) : FragmentStateAdapter(fragment) {
    override fun getItemCount(): Int = 2

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> ProfilePersonal()
            1 -> ProfilePost()
            else -> throw IllegalStateException("Unexpected position $position")
        }
    }
}
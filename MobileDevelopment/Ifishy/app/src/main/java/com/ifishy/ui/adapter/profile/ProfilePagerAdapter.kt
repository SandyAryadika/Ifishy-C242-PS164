package com.ifishy.ui.adapter.profile

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.ifishy.ui.fragment.profile.menu_profile.ProfilePersonalFragment
import com.ifishy.ui.fragment.profile.menu_profile.ProfilePost

class ProfilePagerAdapter(fragment: Fragment,private val email:String) : FragmentStateAdapter(fragment) {
    override fun getItemCount(): Int = 2

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> ProfilePersonalFragment.newInstance(email)
            1 -> ProfilePost()
            else -> throw IllegalStateException("Unexpected position $position")
        }
    }
}
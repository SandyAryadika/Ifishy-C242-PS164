package com.ifishy.ui.adapter.boomark

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.ifishy.ui.activity.bookmark.BookmarkActivity
import com.ifishy.ui.activity.bookmark.fragmentBookmark.BookmarkArticleFragment
import com.ifishy.ui.activity.bookmark.fragmentBookmark.BookmarkPostFragment

class BookmarkAdapterPage(activity: BookmarkActivity) : FragmentStateAdapter(activity) {

    override fun getItemCount(): Int = 2

    override fun createFragment(position: Int): Fragment {
        return when (position){
            0 -> BookmarkArticleFragment()
            1 -> BookmarkPostFragment()
            else -> throw IllegalStateException("Unexpected position: $position")
        }
    }
}
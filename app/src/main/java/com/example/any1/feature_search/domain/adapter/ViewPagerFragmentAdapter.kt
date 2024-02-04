package com.example.any1.feature_search.domain.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.any1.feature_search.presentation.SearchByTags
import com.example.any1.feature_search.presentation.SearchByName
import com.example.any1.feature_search.presentation.SearchByUniqueTag

class ViewPagerFragmentAdapter(fragmentActivity: FragmentActivity) :
    FragmentStateAdapter(fragmentActivity) {
    private val titles = arrayOf("tags", "name", "group tag")
    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> SearchByTags()
            1 -> SearchByName()
            2 -> SearchByUniqueTag()
            else -> SearchByTags()
        }
    }

    override fun getItemCount(): Int {
        return titles.size
    }
}

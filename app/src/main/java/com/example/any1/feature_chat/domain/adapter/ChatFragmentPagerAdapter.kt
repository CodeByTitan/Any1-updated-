package com.example.any1.feature_chat.domain.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.any1.feature_chat.presentation.DirectMessage
import com.example.any1.core.main.BottomNavFrag
import com.example.any1.core.main.Home


class ChatFragmentPagerAdapter(fragmentActivity: FragmentActivity) :
    FragmentStateAdapter(fragmentActivity) {

    override fun createFragment(position: Int): Fragment {
        when (position) {
            0 -> return BottomNavFrag()
            1 -> return DirectMessage()

            else -> {
                return Home()
            }
        }
    }

    override fun getItemCount(): Int {
        return 2
    }
}

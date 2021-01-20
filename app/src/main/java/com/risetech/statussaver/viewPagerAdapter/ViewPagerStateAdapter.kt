package com.risetech.statussaver.viewPagerAdapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.risetech.statussaver.fragments.PreViewFragmentItem
import com.risetech.statussaver.utils.Constants

class ViewPagerStateAdapter(fragmentManager: FragmentManager, lifecycle: Lifecycle) :
    FragmentStateAdapter(fragmentManager, lifecycle) {

    override fun getItemCount(): Int {
        return Constants.passList.size
    }

    override fun createFragment(position: Int): Fragment {
        return PreViewFragmentItem.newInstance(Constants.passList[position].text)
    }

}
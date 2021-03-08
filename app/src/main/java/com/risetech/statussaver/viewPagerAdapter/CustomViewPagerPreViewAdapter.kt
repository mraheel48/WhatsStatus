@file:Suppress("DEPRECATION")

package com.risetech.statussaver.viewPagerAdapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import com.risetech.statussaver.fragments.PreViewFragmentItem.Companion.newInstance
import com.risetech.statussaver.utils.Constants

class CustomViewPagerPreViewAdapter(fragmentManager: FragmentManager?) : FragmentStatePagerAdapter(fragmentManager!!) {

    override fun getItem(position: Int): Fragment {
        return newInstance(Constants.passList[position].text)
    }

    override fun getCount(): Int {
        return Constants.passList.size
    }

    override fun getPageTitle(position: Int): CharSequence {
        return ""
    }

    fun updateUi(position: Int){
        getItem(position)
    }

}
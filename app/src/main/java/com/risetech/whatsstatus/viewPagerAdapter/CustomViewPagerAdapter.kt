@file:Suppress("DEPRECATION")

package com.risetech.whatsstatus.viewPagerAdapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import com.risetech.whatsstatus.fragments.CategoryFragment.Companion.newInstance

class CustomViewPagerAdapter(fragmentManager: FragmentManager?) : FragmentStatePagerAdapter(fragmentManager!!) {

    var fragmentArray = arrayOf("images", "videos", "saved")

    override fun getItem(position: Int): Fragment {
        return newInstance(fragmentArray[position])
    }

    override fun getCount(): Int {
        return fragmentArray.size
    }

    override fun getPageTitle(position: Int): CharSequence {
        return fragmentArray[position]
    }

    fun updateUi(position: Int){
        getItem(position)

    }

}
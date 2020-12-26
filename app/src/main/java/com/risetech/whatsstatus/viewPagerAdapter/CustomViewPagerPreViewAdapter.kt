@file:Suppress("DEPRECATION")

package com.risetech.whatsstatus.viewPagerAdapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import com.risetech.whatsstatus.fragments.PreViewFragmentItem.Companion.newInstance
import com.risetech.whatsstatus.utils.Constants


class CustomViewPagerPreViewAdapter(fragmentManager: FragmentManager?) : FragmentStatePagerAdapter(fragmentManager!!) {

    //var fragmentArray = arrayOf("images",  "saved")
    //var fragmentArray = Constants.passList as (Array<ItemModel>)

    override fun getItem(position: Int): Fragment {
        return newInstance(Constants.passList[position].text.toString())
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
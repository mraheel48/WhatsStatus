package com.risetech.whatsstatus.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.viewpager.widget.ViewPager
import com.risetech.whatsstatus.R
import com.risetech.whatsstatus.viewPagerAdapter.CustomViewPagerPreViewAdapter


class PreViewFragment : Fragment() {

    private lateinit var viewPager: ViewPager
    lateinit var customViewPagerAdapter:CustomViewPagerPreViewAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_pre_view, container, false)

        val strtext = arguments?.getString("itemPosition")

        viewPager = view.findViewById(R.id.view_pager)

        customViewPagerAdapter = CustomViewPagerPreViewAdapter(childFragmentManager)

        viewPager.adapter = customViewPagerAdapter

        viewPager.setCurrentItem(strtext!!.toInt(), true)

        return view
    }


}
package com.risetech.statussaver.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.viewpager.widget.ViewPager
import com.google.android.material.tabs.TabLayout
import com.risetech.statussaver.R
import com.risetech.statussaver.utils.Constants
import com.risetech.statussaver.viewPagerAdapter.CustomViewPagerAdapter


class HomeFragment : Fragment() {

    private lateinit var tabLayout: TabLayout
    private lateinit var viewPager: ViewPager

    var fragmentArray = arrayOf("images", "videos", "saved")

    lateinit var customViewPagerAdapter:CustomViewPagerAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment

        val view = inflater.inflate(R.layout.fragment_home, container, false)
        tabLayout = view.findViewById(R.id.tabs)
        viewPager = view.findViewById(R.id.view_pager)

        customViewPagerAdapter = CustomViewPagerAdapter(childFragmentManager)

        viewPager.adapter = customViewPagerAdapter

        tabLayout.setupWithViewPager(viewPager)

        tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                Log.e("myTabSelect", "${fragmentArray[tab.position]}")
                Constants.fragmentVisible = tab.position
            }

            override fun onTabUnselected(tab: TabLayout.Tab) {}
            override fun onTabReselected(tab: TabLayout.Tab) {}
        })

        return view
    }


    fun updateTabPosition(position:Int){
        viewPager.adapter = null
        viewPager.adapter = customViewPagerAdapter
        customViewPagerAdapter.notifyDataSetChanged()
        viewPager.invalidate()
        tabLayout.getTabAt(position)?.select()

    }

}
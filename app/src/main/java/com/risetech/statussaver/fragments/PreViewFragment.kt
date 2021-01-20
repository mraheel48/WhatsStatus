package com.risetech.statussaver.fragments

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.ViewPager2
import com.risetech.statussaver.R
import com.risetech.statussaver.viewPagerAdapter.CustomViewPagerPreViewAdapter

class PreViewFragment : Fragment(R.layout.fragment_pre_view) {

    private lateinit var viewPager: ViewPager2
    lateinit var customViewPagerAdapter: CustomViewPagerPreViewAdapter


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewPager = view.findViewById(R.id.view_pager)
    }

    /*override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_pre_view, container, false)
        viewPager = view.findViewById(R.id.view_pager)

        *//*  customViewPagerAdapter = CustomViewPagerPreViewAdapter(childFragmentManager)
          viewPager.adapter = customViewPagerAdapter
          viewPager.setCurrentItem(Constants.itemPreviewPosition, true)*//*

        *//* viewPager.addOnPageChangeListener(object : OnPageChangeListener {
             override fun onPageScrolled(
                 position: Int,
                 positionOffset: Float,
                 positionOffsetPixels: Int
             ) {

             }
             override fun onPageSelected(position: Int) {
                 // Here's your instance
                 Constants.itemPreviewPosition = position
             }

             override fun onPageScrollStateChanged(state: Int) {}
         })*//*

        return view
    }*/


}
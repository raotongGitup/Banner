package com.example.banner.listener

import androidx.annotation.Px
import androidx.viewpager2.widget.ViewPager2

/**

 *
 * @author: raotong
 * @date: 2023年06月21日 11:49
 * @Description:
 */
interface OnPageChangeListener {
    fun onPageScrolled(position: Int, positionOffset: Float, @Px positionOffsetPixels: Int)
    fun onPageSelected(position: Int)
    fun onPageScrollStateChanged(@ViewPager2.ScrollState state: Int)
}
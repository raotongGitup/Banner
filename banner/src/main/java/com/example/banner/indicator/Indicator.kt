package com.example.banner.indicator

import android.view.View
import androidx.viewpager.widget.ViewPager.OnPageChangeListener
import com.example.banner.utils.IndicatorConfig

/**

 *
 * @author: raotong
 * @date: 2023年06月21日 11:13
 * @Description:
 */
interface Indicator : OnPageChangeListener {
    fun getIndicatorView(): View

    fun getIndicatorConfig(): IndicatorConfig?

    fun onPageChanged(count: Int, currentPosition: Int)
}
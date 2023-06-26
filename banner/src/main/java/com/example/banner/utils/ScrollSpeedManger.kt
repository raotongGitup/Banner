package com.example.banner.utils

import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSmoothScroller
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.example.banner.view.Banner
import java.lang.reflect.Field

/**
 * @author: raotong
 * @date: 2023年06月21日 10:02
 * @Description: 改变LinearLayoutManager的切换速度
 */
class ScrollSpeedManger<T>(banner: Banner<T>, linearLayoutManager: LinearLayoutManager) :
    LinearLayoutManager(banner.context, linearLayoutManager.orientation, false) {
    private val banner: Banner<T>

    init {
        this.banner = banner
    }

    override fun smoothScrollToPosition(
        recyclerView: RecyclerView,
        state: RecyclerView.State,
        position: Int
    ) {
        val linearSmoothScroller: LinearSmoothScroller =
            object : LinearSmoothScroller(recyclerView.getContext()) {
                protected override fun calculateTimeForDeceleration(dx: Int): Int {
                    return banner.getScrollTime()
                }
            }
        linearSmoothScroller.setTargetPosition(position)
        startSmoothScroll(linearSmoothScroller)
    }

    companion object {
        fun <T> reflectLayoutManager(banner: Banner<T>) {
            if (banner.getScrollTime() < 100) return
            try {
                val viewPager2: ViewPager2? = banner.getViewPage()
                val recyclerView: RecyclerView = viewPager2?.getChildAt(0) as RecyclerView
                recyclerView.setOverScrollMode(RecyclerView.OVER_SCROLL_NEVER)
                val speedManger = ScrollSpeedManger(
                    banner,
                    recyclerView.getLayoutManager() as LinearLayoutManager
                )
                recyclerView.layoutManager = speedManger
                val LayoutMangerField: Field =
                    ViewPager2::class.java.getDeclaredField("mLayoutManager")
                LayoutMangerField.isAccessible = true
                LayoutMangerField[viewPager2] = speedManger
                val pageTransformerAdapterField: Field =
                    ViewPager2::class.java.getDeclaredField("mPageTransformerAdapter")
                pageTransformerAdapterField.isAccessible = true
                val mPageTransformerAdapter = pageTransformerAdapterField[viewPager2]
                if (mPageTransformerAdapter != null) {
                    val aClass: Class<*> = mPageTransformerAdapter.javaClass
                    val layoutManager = aClass.getDeclaredField("mLayoutManager")
                    layoutManager.isAccessible = true
                    layoutManager[mPageTransformerAdapter] = speedManger
                }
                val scrollEventAdapterField: Field =
                    ViewPager2::class.java.getDeclaredField("mScrollEventAdapter")
                scrollEventAdapterField.isAccessible = true
                val mScrollEventAdapter = scrollEventAdapterField[viewPager2]
                if (mScrollEventAdapter != null) {
                    val aClass: Class<*> = mScrollEventAdapter.javaClass
                    val layoutManager = aClass.getDeclaredField("mLayoutManager")
                    layoutManager.isAccessible = true
                    layoutManager[mScrollEventAdapter] = speedManger
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}
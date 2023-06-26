package com.example.banner.utils

import com.example.banner.view.Banner
import java.lang.ref.WeakReference

/**

 *
 * @author: raotong
 * @date: 2023年06月21日 10:02
 * @Description:
 */
class AutoLoopTask<T>(banner: Banner<T>?) : Runnable {
    private val reference: WeakReference<Banner<T>> = WeakReference(banner)


    override fun run() {
        val banner: Banner<T>? = reference.get()
        if (banner != null && banner.getIsAutoLoop()) {
            val count: Int = banner.getAdapter()?.itemCount ?: 0
            if (count == 0) {
                return
            }
            val next: Int = ((banner.getViewPage()?.currentItem ?: 0) + 1) % count
            banner.getViewPage()?.currentItem = next
            banner.postDelayed(banner.getLoopTask(), banner.getLoopTime())
        }

    }

}
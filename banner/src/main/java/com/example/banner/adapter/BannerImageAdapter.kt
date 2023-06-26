package com.example.banner.adapter

import android.view.ViewGroup
import android.widget.ImageView
import com.example.banner.holder.BannerImageHolder

/**

 *
 * @author: raotong
 * @date: 2023年06月20日 18:40
 * @Description: 只有图片的adapter
 */
abstract class BannerImageAdapter<T>(var arrayList: ArrayList<T>) :
    BannerAdapter<T, BannerImageHolder>(arrayList) {
    override fun onCreateHolder(parent: ViewGroup?, viewType: Int): BannerImageHolder {
        val imageView = ImageView(parent?.context)
        val params = ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        )
        imageView.layoutParams = params
        imageView.scaleType = ImageView.ScaleType.CENTER_CROP
        return BannerImageHolder(imageView)

    }
}
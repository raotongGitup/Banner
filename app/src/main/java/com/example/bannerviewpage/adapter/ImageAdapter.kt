package com.example.bannerviewpage.adapter

import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.banner.adapter.BannerAdapter
import com.example.banner.utils.BannerUtils

/**

 *
 * @author: raotong
 * @date: 2023年06月25日 10:21
 * @Description: 单张图片的banner
 */
class ImageAdapter(imageUrls: ArrayList<String>) : BannerAdapter<String, ImageAdapter.ImageHolder>(imageUrls) {


    class ImageHolder(view: ImageView) : RecyclerView.ViewHolder(view){
        var imageView: ImageView = view as ImageView
    }

    override fun onCreateHolder(parent: ViewGroup?, viewType: Int): ImageHolder {
        val imageView = ImageView(parent?.context)
        val params = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
        imageView.layoutParams = params
        imageView.scaleType = ImageView.ScaleType.CENTER_CROP
        //通过裁剪实现圆角
        BannerUtils.setBannerRound(imageView, 20f)
        return ImageHolder(imageView)
    }

    override fun onBindHolder(holder: ImageHolder, data: String, position: Int) {
        Glide.with(holder.itemView)
            .load(data)
            .into(holder.imageView)
    }
}
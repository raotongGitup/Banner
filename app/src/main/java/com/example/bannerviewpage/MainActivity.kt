package com.example.bannerviewpage

import android.os.Bundle
import androidx.activity.ComponentActivity
import com.example.banner.adapter.BannerImageAdapter
import com.example.banner.holder.BannerImageHolder
import com.example.banner.indicator.RoundIndicator
import com.example.banner.view.Banner
import com.example.bannerviewpage.adapter.ImageAdapter

class MainActivity : ComponentActivity() {
    private var mBanner : Banner<String>? = null

    var imageUrls = ArrayList<String>()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        mBanner = findViewById(R.id.banner)
        imageUrls.add("https://img.zcool.cn/community/01b72057a7e0790000018c1bf4fce0.png")
        imageUrls.add("https://img.zcool.cn/community/016a2256fb63006ac7257948f83349.jpg")
        imageUrls.add("https://img.zcool.cn/community/01233056fb62fe32f875a9447400e1.jpg")
        imageUrls.add("https://img.zcool.cn/community/01700557a7f42f0000018c1bd6eb23.jpg")

        mBanner?.apply {
            setBannerRound(20f)
            setIndicator(RoundIndicator(this@MainActivity))
            setAdapter(ImageAdapter(imageUrls))
        }


    }
}
package com.example.banner.listener

/**
 * @author: raotong
 * @date: 2023年06月20日 18:25
 * @Description:
 */
open interface OnBannerListener<T> {

    /**
     * @param position Int  当前类
     * @param data T   当前数据
     * @return
     *
     * */
    fun onBannerClick(position: Int, data: T)
}
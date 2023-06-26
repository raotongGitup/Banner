package com.example.banner.utils

/**

 *
 * @author: raotong
 * @date: 2023年06月21日 10:27
 * @Description:
 */
class BannerConfig {
    companion object {
        const val IS_AUTO_LOOP = true
        const val IS_INFINITE_LOOP = true
        const val LOOP_TIME = 3000L
        const val SCROLL_TIME = 600
        const val INCREASE_COUNT = 2
        const val INDICATOR_NORMAL_COLOR = -0x77000001
        const val INDICATOR_SELECTED_COLOR = -0x78000000
        val INDICATOR_NORMAL_WIDTH = BannerUtils.dp2px(5f)
        val INDICATOR_SELECTED_WIDTH = BannerUtils.dp2px(7f)
        val INDICATOR_SPACE = BannerUtils.dp2px(5f)
        val INDICATOR_MARGIN = BannerUtils.dp2px(5f)

        val INDICATOR_HEIGHT = BannerUtils.dp2px(3f)
        val INDICATOR_RADIUS = BannerUtils.dp2px(3f)

        const val HORIZONTAL = 0
        const val VERTICAL = 1
    }
}
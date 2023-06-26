package com.example.banner.utils

import android.content.res.Resources
import android.graphics.Outline
import android.os.Build
import android.util.TypedValue
import android.view.View
import android.view.ViewOutlineProvider
import androidx.annotation.RequiresApi

/**

 *
 * @author: raotong
 * @date: 2023年06月21日 10:30
 * @Description:
 */
class BannerUtils {
    companion object {
        fun dp2px(dp: Float): Int {
            return TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                dp,
                Resources.getSystem().displayMetrics
            ).toInt()
        }

        fun getRealPosition(isIncrease: Boolean, position: Int, realCount: Int): Int {
            if (!isIncrease) {
                return position
            }
            var realPosition = when (position) {
                0 -> {
                    realCount - 1
                }

                realCount + 1 -> {
                    0
                }

                else -> {
                    position - 1
                }
            }
            return realPosition
        }

        /**
         * 设置view圆角
         *
         * @param radius
         * @return
         */
        @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
        fun setBannerRound(view: View, radius: Float) {
            view.outlineProvider = object : ViewOutlineProvider() {
                override fun getOutline(view: View, outline: Outline) {
                    outline.setRoundRect(0, 0, view.width, view.height, radius)
                }
            }
            view.clipToOutline = true
        }
    }
}
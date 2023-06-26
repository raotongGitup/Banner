package com.example.banner.indicator

import android.content.Context
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import com.example.banner.utils.IndicatorConfig

/**

 *
 * @author: raotong
 * @date: 2023年06月21日 11:15
 * @Description: 显示指示器圆点
 */
open class BaseIndicator @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defaultValue: Int = 0
) : View(context, attrs, defaultValue), Indicator {
     var config: IndicatorConfig? = null
     var mPaint: Paint? = null
     var offset = 0f

    init {
        config = IndicatorConfig()
        mPaint = Paint()
        mPaint?.isAntiAlias = true
        mPaint?.color = Color.TRANSPARENT
        mPaint?.color = config?.normalColor?: Color.TRANSPARENT
    }

    override fun getIndicatorView(): View {
        if (config?.isAttachToBanner != false) {
            val layoutParams = FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT
            )
            when (config?.gravity) {
                IndicatorConfig.Direction.LEFT -> layoutParams.gravity =
                    Gravity.BOTTOM or Gravity.START

                IndicatorConfig.Direction.CENTER -> layoutParams.gravity =
                    Gravity.BOTTOM or Gravity.CENTER_HORIZONTAL

                IndicatorConfig.Direction.RIGHT -> layoutParams.gravity =
                    Gravity.BOTTOM or Gravity.END
            }
            config?.getMargins()?.leftMargin?.let {
                layoutParams.leftMargin = it
            }
            config?.getMargins()?.rightMargin?.let {
                layoutParams.rightMargin = it
            }
            config?.getMargins()?.topMargin?.let {
                layoutParams.topMargin = it
            }
            config?.getMargins()?.bottomMargin?.let {
                layoutParams.bottomMargin = it
            }
            setLayoutParams(layoutParams)
        }
        return this
    }

    override fun getIndicatorConfig(): IndicatorConfig? {
        return config
    }

    override fun onPageChanged(count: Int, currentPosition: Int) {
        config?.setIndicatorSize(count)
        config?.setCurrentPosition(currentPosition)
        requestLayout()
    }

    override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
        offset = positionOffset
        invalidate()
    }

    override fun onPageSelected(position: Int) {
        config?.setCurrentPosition(position)
        invalidate()
    }

    override fun onPageScrollStateChanged(state: Int) {

    }
}
package com.example.banner.indicator

import android.content.Context
import android.graphics.Canvas
import android.util.AttributeSet
import com.example.banner.utils.BannerConfig

/**

 *
 * @author: raotong
 * @date: 2023年06月27日 10:59
 * @Description: 圆点指示器
 */
class CircleIndicator @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defaultValue: Int = 0
) : BaseIndicator(context, attrs, defaultValue) {

    private var mNormalRadius = 0   // 默认圆角大小
    private var mSelectedRadius = 0  // 选中的时候
    private var maxRadius = 0     // 最大圆角大小

    init {
        mNormalRadius = (config?.normalWidth ?: BannerConfig.INDICATOR_NORMAL_WIDTH) / 2
        mSelectedRadius = (config?.selectedWidth ?: BannerConfig.INDICATOR_SELECTED_WIDTH) / 2

    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        val count: Int = config?.indicatorSize ?: 0
        if (count <= 1) {
            return
        }

        mNormalRadius = (config?.normalWidth ?: BannerConfig.INDICATOR_NORMAL_WIDTH) / 2
        mSelectedRadius = (config?.selectedWidth ?: BannerConfig.INDICATOR_SELECTED_WIDTH) / 2
        maxRadius = mSelectedRadius.coerceAtLeast(mNormalRadius)
        val width: Int =
            (count - 1) * (config?.indicatorSpace
                ?: BannerConfig.INDICATOR_SPACE) + (config?.selectedWidth
                ?: BannerConfig.INDICATOR_SELECTED_WIDTH) + (config?.normalWidth
                ?: BannerConfig.INDICATOR_NORMAL_WIDTH) * (count - 1)
        setMeasuredDimension(
            width,
            (config?.normalWidth ?: BannerConfig.INDICATOR_NORMAL_WIDTH).coerceAtLeast(
                config?.selectedWidth ?: BannerConfig.INDICATOR_SELECTED_WIDTH
            )
        )
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        val count: Int = config?.indicatorSize ?: 0
        if (count <= 1) {
            return
        }
        var left = 0f
        for (i in 0 until count) {
            mPaint?.color = if (config?.currentPosition ?: 0 === i) (config?.selectedColor
                ?: BannerConfig.INDICATOR_SELECTED_COLOR) else (config?.normalColor
                ?: BannerConfig.INDICATOR_NORMAL_COLOR)
            val indicatorWidth: Int =
                if (config?.currentPosition ?: 0 === i) (config?.selectedWidth
                    ?: BannerConfig.INDICATOR_SELECTED_WIDTH) else (config?.normalWidth
                    ?: BannerConfig.INDICATOR_NORMAL_WIDTH)
            val radius = if (config?.currentPosition ?: 0 === i) mSelectedRadius else mNormalRadius
            canvas?.drawCircle(left + radius, maxRadius.toFloat(), radius.toFloat(), mPaint!!)
            left += indicatorWidth + (config?.indicatorSpace ?: BannerConfig.INDICATOR_SPACE)
        }
    }


}
package com.example.banner.indicator

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import com.example.banner.utils.BannerConfig

/**

 *
 * @author: raotong
 * @date: 2023年06月25日 16:50
 * @Description: 圆点指示器（模版）
 */
class RoundIndicator @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defaultValue: Int = 0
) : BaseIndicator(context, attrs, defaultValue) {
    init {

        mPaint?.style = Paint.Style.FILL
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        val count: Int = config?.indicatorSize ?:0
        if (count <= 1) return
        setMeasuredDimension(((config?.selectedWidth ?:0) * count) as Int, (config?.height) ?:0)
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        val count: Int = config?.indicatorSize ?:0
        if (count <= 1) return

        mPaint?.color = config?.normalColor ?: BannerConfig.INDICATOR_NORMAL_COLOR
        canvas?.let {
            val oval = RectF(0f, 0f, it.width.toFloat(),
                (config?.height ?:BannerConfig.INDICATOR_HEIGHT).toFloat()
            )
            it.drawRoundRect(oval,
                (config?.radius ?:BannerConfig.INDICATOR_RADIUS).toFloat(),  (config?.radius ?:BannerConfig.INDICATOR_RADIUS).toFloat(), mPaint!!)

            mPaint!!.color = config?.selectedColor ?:BannerConfig.INDICATOR_SELECTED_COLOR
            val left: Int =(config?.currentPosition ?: 0)  * (config?.selectedWidth ?: 0)
            val rectF = RectF(left.toFloat(), 0f, (left + (config?.selectedWidth ?:0)).toFloat(),
                (config?.height ?: BannerConfig.INDICATOR_HEIGHT).toFloat()
            )
            it.drawRoundRect(rectF, (config?.radius ?:BannerConfig.INDICATOR_RADIUS).toFloat(), (config?.radius ?:BannerConfig.INDICATOR_RADIUS).toFloat(), mPaint!!)
        }



    }




}
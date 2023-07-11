package com.example.banner.view

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.graphics.PorterDuff
import android.graphics.PorterDuffXfermode
import android.graphics.RectF
import android.os.Build
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.ViewConfiguration
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.annotation.ColorInt
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.AdapterDataObserver
import androidx.viewpager.widget.ViewPager
import androidx.viewpager2.widget.CompositePageTransformer
import androidx.viewpager2.widget.ViewPager2
import com.example.banner.R
import com.example.banner.adapter.BannerAdapter
import com.example.banner.indicator.Indicator
import com.example.banner.listener.OnBannerListener
import com.example.banner.listener.OnPageChangeListener
import com.example.banner.utils.AutoLoopTask
import com.example.banner.utils.BannerConfig
import com.example.banner.utils.BannerUtils
import com.example.banner.utils.IndicatorConfig
import com.example.banner.utils.ScrollSpeedManger

/**

 *
 * @author: raotong
 * @date: 2023年06月20日 18:45
 * @Description: banner 布局
 */
class Banner<T> @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defaultValue: Int = 0
) : FrameLayout(context, attrs, defaultValue) {
    companion object {
        const val INVALID_VALUE = -1
    }

    // 滑动的距离
    private var mTouchSlop = 0
    private var mAdapter: BannerAdapter<T, out RecyclerView.ViewHolder>? = null

    // 轮播切换间隔时间
    private var mLoopTime: Long = BannerConfig.LOOP_TIME

    // 是否自动轮播
    private var mIsAutoLoop = BannerConfig.IS_AUTO_LOOP


    // 轮播切换时间
    private var mScrollTime = BannerConfig.SCROLL_TIME
    private var mIndicator: Indicator? = null

    // 轮播开始位置
    private var mStartPosition = 1

    // banner圆角半径，默认没有圆角
    private var mBannerRadius = 0f

    // banner圆角方向，如果一个都不设置，默认四个角全部圆角
    private var mRoundTopLeft = false  // banner圆角方向，如果一个都不设置，默认四个角全部圆角
    private var mRoundTopRight = false  // banner圆角方向，如果一个都不设置，默认四个角全部圆角
    private var mRoundBottomLeft = false  // banner圆角方向，如果一个都不设置，默认四个角全部圆角
    private var mRoundBottomRight = false

    // 是否允许无限轮播（即首尾直接切换）
    private var mIsInfiniteLoop = BannerConfig.IS_INFINITE_LOOP

    private var mOrientation: Int = BannerConfig.HORIZONTAL

    // 展示样式
    private var mCompositePageTransformer: CompositePageTransformer? = null
    private var mPageChangeCallback: BannerOnPageChangeCallback? = null
    private var mLoopTask: AutoLoopTask<T>? = null
    private var mViewPager2: ViewPager2? = null

    //绘制圆角视图
    private var mRoundPaint: Paint? = null
    private var mImagePaint: Paint? = null

    // 指示器相关配置
    private var normalWidth = BannerConfig.INDICATOR_NORMAL_WIDTH
    private var selectedWidth = BannerConfig.INDICATOR_SELECTED_WIDTH
    private var normalColor = BannerConfig.INDICATOR_NORMAL_COLOR
    private var selectedColor = BannerConfig.INDICATOR_SELECTED_COLOR
    private var indicatorGravity: Int = IndicatorConfig.Direction.CENTER
    private var indicatorSpace = 0
    private var indicatorMargin = 0
    private var indicatorMarginLeft = 0
    private var indicatorMarginTop = 0
    private var indicatorMarginRight = 0
    private var indicatorMarginBottom = 0
    private var indicatorHeight = BannerConfig.INDICATOR_HEIGHT
    private var indicatorRadius = BannerConfig.INDICATOR_RADIUS
    private var mOnPageChangeListener: OnPageChangeListener? = null

    // 是否要拦截事件
    private val isIntercept = true

    // 记录触摸的位置（主要用于解决事件冲突问题）
    private var mStartX = 0f  // 记录触摸的位置（主要用于解决事件冲突问题）
    private var mStartY = 0f

    // 记录viewpager2是否被拖动
    private var mIsViewPager2Drag = false


    init {
        init(context)
        initTypedArray(context, attrs)

    }

    private fun init(context: Context) {
        ViewConfiguration.get(context).scaledTouchSlop / 2     //  获取系统的最小滑动距离
        mCompositePageTransformer = CompositePageTransformer()
        mPageChangeCallback = BannerOnPageChangeCallback()
        mLoopTask = AutoLoopTask(this)
        mViewPager2 = ViewPager2(context)
        mViewPager2?.layoutParams =
            LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
        mViewPager2?.offscreenPageLimit = 2
        mViewPager2?.registerOnPageChangeCallback(mPageChangeCallback!!)
        mViewPager2?.setPageTransformer(mCompositePageTransformer)
        ScrollSpeedManger.reflectLayoutManager(this)
        addView(mViewPager2)
        mRoundPaint = Paint()
        mRoundPaint?.color = Color.WHITE
        mRoundPaint?.isAntiAlias = true
        mRoundPaint?.style = Paint.Style.FILL
        mRoundPaint?.xfermode = PorterDuffXfermode(PorterDuff.Mode.DST_OUT)
        mImagePaint = Paint()
        mImagePaint?.xfermode = null
    }

    private fun initTypedArray(
        context: Context,
        attrs: AttributeSet? = null,
    ) {
        attrs?.let {
            val a = context.obtainStyledAttributes(it, R.styleable.Banner)
            mBannerRadius = a.getDimensionPixelSize(R.styleable.Banner_banner_radius, 0).toFloat()
            mLoopTime =
                a.getInt(R.styleable.Banner_banner_loop_time, BannerConfig.LOOP_TIME.toInt())
                    .toLong()
            mIsAutoLoop =
                a.getBoolean(R.styleable.Banner_banner_auto_loop, BannerConfig.IS_AUTO_LOOP)
            mIsInfiniteLoop =
                a.getBoolean(R.styleable.Banner_banner_infinite_loop, BannerConfig.IS_INFINITE_LOOP)
            normalWidth = a.getDimensionPixelSize(
                R.styleable.Banner_banner_indicator_normal_width,
                BannerConfig.INDICATOR_NORMAL_WIDTH
            )
            selectedWidth = a.getDimensionPixelSize(
                R.styleable.Banner_banner_indicator_selected_width,
                BannerConfig.INDICATOR_SELECTED_WIDTH
            )
            normalColor = a.getColor(
                R.styleable.Banner_banner_indicator_normal_color,
                BannerConfig.INDICATOR_NORMAL_COLOR
            )
            selectedColor = a.getColor(
                R.styleable.Banner_banner_indicator_selected_color,
                BannerConfig.INDICATOR_SELECTED_COLOR
            )
            indicatorGravity = a.getInt(
                R.styleable.Banner_banner_indicator_gravity,
                IndicatorConfig.Direction.CENTER
            )
            indicatorSpace = a.getDimensionPixelSize(R.styleable.Banner_banner_indicator_space, 0)
            indicatorMargin = a.getDimensionPixelSize(R.styleable.Banner_banner_indicator_margin, 0)
            indicatorMarginLeft =
                a.getDimensionPixelSize(R.styleable.Banner_banner_indicator_marginLeft, 0)
            indicatorMarginTop =
                a.getDimensionPixelSize(R.styleable.Banner_banner_indicator_marginTop, 0)
            indicatorMarginRight =
                a.getDimensionPixelSize(R.styleable.Banner_banner_indicator_marginRight, 0)
            indicatorMarginBottom =
                a.getDimensionPixelSize(R.styleable.Banner_banner_indicator_marginBottom, 0)
            indicatorHeight = a.getDimensionPixelSize(
                R.styleable.Banner_banner_indicator_height,
                BannerConfig.INDICATOR_HEIGHT
            )
            indicatorRadius = a.getDimensionPixelSize(
                R.styleable.Banner_banner_indicator_radius,
                BannerConfig.INDICATOR_RADIUS
            )
            mOrientation = a.getInt(R.styleable.Banner_banner_orientation, BannerConfig.HORIZONTAL)
            mRoundTopLeft = a.getBoolean(R.styleable.Banner_banner_round_top_left, false)
            mRoundTopRight = a.getBoolean(R.styleable.Banner_banner_round_top_right, false)
            mRoundBottomLeft = a.getBoolean(R.styleable.Banner_banner_round_bottom_left, false)
            mRoundBottomRight = a.getBoolean(R.styleable.Banner_banner_round_bottom_right, false)
            a.recycle()
        }

        setOrientation(mOrientation)
        setInfiniteLoop()
    }

    /**
     * 设置banner的适配器
     * @param adapter
     * @param isInfiniteLoop 是否支持无限循环
     * @return
     */
    fun setAdapter(
        adapter: BannerAdapter<T, out RecyclerView.ViewHolder>,
        isInfiniteLoop: Boolean
    ): Banner<T> {
        mIsInfiniteLoop = isInfiniteLoop
        setInfiniteLoop()
        setAdapter(adapter)
        return this
    }

    /**
     * 重新设置banner数据，当然你也可以在你adapter中自己操作数据,不要过于局限在这个方法，举一反三哈
     *
     * @param data 数据集合，当传null或者datas没有数据时，banner会变成空白的，请做好占位UI处理
     */
    fun setData(data: ArrayList<T>):  Banner<T> {
        getAdapter()?.setData(data)
        setCurrentItem(mStartPosition, false)
        setIndicatorPageChange()
        start()

        return this
    }

    /**
     * 设置指示器adapter
     * */
    fun setAdapter(adapter: BannerAdapter<T, out RecyclerView.ViewHolder>?): Banner<T> {
        if (adapter == null)
            throw RuntimeException("banner adapter is null")

        this.mAdapter = adapter
        mAdapter = adapter
        if (!isInfiniteLoop()) {
            getAdapter()?.setIncreaseCount(0)
        }
        getAdapter()?.registerAdapterDataObserver(mAdapterDataObserver)
        mViewPager2?.adapter = adapter
        setCurrentItem(mStartPosition, false)
        initIndicator()
        return this


    }

    /**
     * 设置点击事件
     */
    fun setOnBannerListener(listener: OnBannerListener<T>?): Banner<T> {
        getAdapter()?.setOnBannerListener(listener)
        return this
    }

    /**
     * 改变最小滑动距离
     */
    fun setTouchSlop(mTouchSlop: Int):  Banner<T> {
        this.mTouchSlop = mTouchSlop
        return this
    }

    /**
     * 添加viewpager切换事件
     *
     *
     * 在viewpager2中切换事件[ViewPager2.OnPageChangeCallback]是一个抽象类，
     * 为了方便使用习惯这里用的是和viewpager一样的[ViewPager.OnPageChangeListener]接口
     *
     */
    fun addOnPageChangeListener(pageListener: OnPageChangeListener):  Banner<T> {
        this.mOnPageChangeListener = pageListener
        return this
    }

    /**
     * 设置banner圆角
     *
     *
     * 默认没有圆角，需要取消圆角把半径设置为0即可
     *
     * @param radius 圆角半径
     */
    fun setBannerRound(radius: Float):  Banner<T> {
        mBannerRadius = radius
        return this
    }

    /**
     * 设置banner圆角(第二种方式，和上面的方法不要同时使用)，只支持5.0以上
     */
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    fun setBannerRound2(radius: Float):  Banner<T> {
        BannerUtils.setBannerRound(this, radius)
        return this
    }


    private val mAdapterDataObserver: AdapterDataObserver = object : AdapterDataObserver() {
        override fun onChanged() {
            if ((getAdapter()?.itemCount ?: 0) <= 1) {
                stop()
            } else {
                start()
            }
            setIndicatorPageChange()
        }
    }

    fun setIndicatorPageChange():  Banner<T>{

        val realPosition: Int =
            BannerUtils.getRealPosition(
                isInfiniteLoop(),
                getViewPage()?.currentItem ?: 0,
                getRealCount()
            )
        getIndicator()?.onPageChanged(getRealCount(), realPosition)

        return this
    }

    /**
     * 返回banner真实总数
     */
    fun getRealCount(): Int {
        return if (getAdapter() != null) {
            getAdapter()!!.getRealCount()
        } else 0
    }

    /**
     * 开始轮播
     */
    fun start():  Banner<T> {
        if (mIsAutoLoop) {
            stop()
            postDelayed(mLoopTask, mLoopTime)
        }
        return this
    }

    /**
     * 停止轮播
     */
    fun stop():  Banner<T> {
        if (mIsAutoLoop) {
            removeCallbacks(mLoopTask)
        }
        return this
    }

    /**
     * 移除一些引用
     */
    fun destroy() {
        mPageChangeCallback?.let {
                getViewPage()?.unregisterOnPageChangeCallback(it)
                mPageChangeCallback = null
            }
        stop()
    }


    /**
     * 跳转到指定位置（最好在设置了数据后在调用，不然没有意义）
     * @param position
     * @param smoothScroll
     * @return
     */
    fun setCurrentItem(position: Int, smoothScroll: Boolean):  Banner<T> {
        getViewPage()?.setCurrentItem(position, smoothScroll)
        return this
    }

    private fun initIndicator() {
        if (getIndicator() == null || getAdapter() == null) {
            return
        }
        if (getIndicator()?.getIndicatorConfig()?.isAttachToBanner != false) {
            removeIndicator()
            addView(getIndicator()?.getIndicatorView())
        }
        initIndicatorAttr()
        setIndicatorPageChange()
    }

    private fun initIndicatorAttr() {
        if (indicatorMargin != 0) {
            setIndicatorMargins(IndicatorConfig.Margins(indicatorMargin))
        } else if (indicatorMarginLeft != 0 || indicatorMarginTop != 0 || indicatorMarginRight != 0 || indicatorMarginBottom != 0) {
            setIndicatorMargins(
                IndicatorConfig.Margins(
                    indicatorMarginLeft,
                    indicatorMarginTop,
                    indicatorMarginRight,
                    indicatorMarginBottom
                )
            )
        }
        if (indicatorSpace > 0) {
            setIndicatorSpace(indicatorSpace)
        }
        if (indicatorGravity != IndicatorConfig.Direction.CENTER) {
            setIndicatorGravity(indicatorGravity)
        }
        if (normalWidth > 0) {
            setIndicatorNormalWidth(normalWidth)
        }
        if (selectedWidth > 0) {
            setIndicatorSelectedWidth(selectedWidth)
        }
        if (indicatorHeight > 0) {
            setIndicatorHeight(indicatorHeight)
        }
        if (indicatorRadius > 0) {
            setIndicatorRadius(indicatorRadius)
        }
        setIndicatorNormalColor(normalColor)
        setIndicatorSelectedColor(selectedColor)
    }

    fun setIndicatorSelectedColor(@ColorInt color: Int):  Banner<T> {
        getIndicatorConfig()?.setSelectedColor(color)

        return this
    }

    fun setIndicatorNormalColor(@ColorInt color: Int):  Banner<T>{

        getIndicatorConfig()?.normalColor = color

        return this
    }

    fun setIndicatorHeight(indicatorHeight: Int):  Banner<T> {
        getIndicatorConfig()?.setHeight(indicatorHeight)

        return this
    }

    fun setIndicatorRadius(indicatorRadius: Int):  Banner<T> {
        getIndicatorConfig()?.setRadius(indicatorRadius)

        return this
    }

    fun setIndicatorNormalWidth(normalWidth: Int):  Banner<T> {
        getIndicatorConfig()?.setNormalWidth(normalWidth)

        return this
    }

    fun setIndicatorSelectedWidth(selectedWidth: Int):  Banner<T> {

        getIndicatorConfig()?.setSelectedWidth(selectedWidth)

        return this
    }

    fun setIndicatorMargins(margins: IndicatorConfig.Margins?):  Banner<T> {
        if (getIndicatorConfig()?.isAttachToBanner != false) {
            getIndicatorConfig()?.setMargins(margins)
            getIndicator()?.getIndicatorView()?.requestLayout()
        }
        return this
    }

    fun setIndicatorGravity(@IndicatorConfig.Direction gravity: Int):  Banner<T> {
        if (getIndicatorConfig()?.isAttachToBanner!= false) {
            getIndicatorConfig()?.gravity = gravity
            getIndicator()?.getIndicatorView()?.postInvalidate()
        }
        return this
    }

    fun setIndicatorSpace(indicatorSpace: Int):  Banner<T> {

        getIndicatorConfig()?.setIndicatorSpace(indicatorSpace)

        return this
    }

    fun getIndicatorConfig(): IndicatorConfig? {
        return if (getIndicator() != null) {
            getIndicator()?.getIndicatorConfig()
        } else null
    }

    fun getIndicator(): Indicator? {
        return mIndicator
    }

    fun removeIndicator():  Banner<T> {
        removeView(getIndicator()?.getIndicatorView())

        return this
    }


    /**
     * 设置banner轮播方向
     *
     * @param orientation [Orientation]
     */
    fun setOrientation(orientation: Int):  Banner<T> {
        getViewPage()?.orientation = orientation
        return this
    }

    private fun setInfiniteLoop() {
        // 当不支持无限循环时，要关闭自动轮播
        if (!isInfiniteLoop()) {
            isAutoLoop(false)
        }
        setStartPosition(if (isInfiniteLoop()) mStartPosition else 0)
    }

    fun isInfiniteLoop(): Boolean {
        return mIsInfiniteLoop
    }

    /**
     * 是否允许自动轮播
     *
     * @param isAutoLoop ture 允许，false 不允许
     */
    fun isAutoLoop(isAutoLoop: Boolean):  Banner<T> {
        mIsAutoLoop = isAutoLoop
        return this
    }

    /**
     * 设置开始的位置 (需要在setAdapter或者setDatas之前调用才有效哦)
     */
    fun setStartPosition(mStartPosition: Int):  Banner<T> {
        this.mStartPosition = mStartPosition
        return this
    }

    inner class BannerOnPageChangeCallback : ViewPager2.OnPageChangeCallback() {
        private var mTempPosition: Int = Banner.INVALID_VALUE
        private var isScrolled = false

        override fun onPageScrolled(
            position: Int,
            positionOffset: Float,
            positionOffsetPixels: Int
        ) {
            val realPosition =
                BannerUtils.getRealPosition(isInfiniteLoop(), position, getRealCount())
            if (realPosition == (getViewPage()?.currentItem ?: 0) - 1) {
                mOnPageChangeListener?.onPageScrolled(
                    realPosition,
                    positionOffset,
                    positionOffsetPixels
                )
            }
            if (realPosition == (getViewPage()?.currentItem ?: 0) - 1) {
                getIndicator()?.onPageScrolled(realPosition, positionOffset, positionOffsetPixels)
            }
        }

        override fun onPageScrollStateChanged(state: Int) {
            //手势滑动中,代码执行滑动中

            //手势滑动中,代码执行滑动中
            if (state == ViewPager2.SCROLL_STATE_DRAGGING || state == ViewPager2.SCROLL_STATE_SETTLING) {
                isScrolled = true
            } else if (state == ViewPager2.SCROLL_STATE_IDLE) {
                //滑动闲置或滑动结束
                isScrolled = false
                if (mTempPosition != INVALID_VALUE && mIsInfiniteLoop) {
                    if (mTempPosition == 0) {
                        setCurrentItem(getRealCount(), false)
                    } else if (mTempPosition == (getAdapter()?.itemCount ?: 0) - 1) {
                        setCurrentItem(1, false)
                    }
                }
            }

            mOnPageChangeListener?.onPageScrollStateChanged(state)

            getIndicator()?.onPageScrollStateChanged(state)

        }

        override fun onPageSelected(position: Int) {
            if (isScrolled) {
                mTempPosition = position
                val realPosition =
                    BannerUtils.getRealPosition(isInfiniteLoop(), position, getRealCount())
                mOnPageChangeListener?.onPageSelected(realPosition)
                getIndicator()?.onPageSelected(realPosition)

            }
        }
    }

    override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {
        if (getViewPage()?.isUserInputEnabled == false) {
            return super.dispatchTouchEvent(ev)
        }

        val action = ev!!.actionMasked
        if (action == MotionEvent.ACTION_UP || action == MotionEvent.ACTION_CANCEL || action == MotionEvent.ACTION_OUTSIDE) {
            start()
        } else if (action == MotionEvent.ACTION_DOWN) {
            stop()
        }
        return super.dispatchTouchEvent(ev)
    }

    override fun onInterceptHoverEvent(event: MotionEvent?): Boolean {
        if (getViewPage()?.isUserInputEnabled == false || !isIntercept) {
            return super.onInterceptTouchEvent(event)
        }
        when (event!!.action) {
            MotionEvent.ACTION_DOWN -> {
                mStartX = event.x
                mStartY = event.y
                parent.requestDisallowInterceptTouchEvent(true)
            }

            MotionEvent.ACTION_MOVE -> {
                val endX = event.x
                val endY = event.y
                val distanceX: Float = Math.abs(endX - mStartX)
                val distanceY: Float = Math.abs(endY - mStartY)
                if (getViewPage()?.orientation == BannerConfig.HORIZONTAL) {
                    mIsViewPager2Drag = distanceX > mTouchSlop && distanceX > distanceY
                } else {
                    mIsViewPager2Drag = distanceY > mTouchSlop && distanceY > distanceX
                }
                parent.requestDisallowInterceptTouchEvent(mIsViewPager2Drag)
            }

            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> parent.requestDisallowInterceptTouchEvent(
                false
            )
        }
        return super.onInterceptTouchEvent(event)
    }

    override fun dispatchDraw(canvas: Canvas?) {
        if (mBannerRadius > 0) {
            canvas?.saveLayer(
                RectF(0f, 0f, canvas.width.toFloat(), canvas.height.toFloat()),
                mImagePaint,
                Canvas.ALL_SAVE_FLAG
            )
            super.dispatchDraw(canvas)
            //绘制外圆环边框圆环
            //默认四个角都设置
            if (!mRoundTopRight && !mRoundTopLeft && !mRoundBottomRight && !mRoundBottomLeft) {
                drawTopLeft(canvas)
                drawTopRight(canvas)
                drawBottomLeft(canvas)
                drawBottomRight(canvas)
                canvas?.restore()
                return
            }
            if (mRoundTopLeft) {
                drawTopLeft(canvas)
            }
            if (mRoundTopRight) {
                drawTopRight(canvas)
            }
            if (mRoundBottomLeft) {
                drawBottomLeft(canvas)
            }
            if (mRoundBottomRight) {
                drawBottomRight(canvas)
            }
            canvas?.restore()
        } else {
            super.dispatchDraw(canvas)
        }
    }

    private fun drawTopLeft(canvas: Canvas?) {
        val path = Path()
        path.moveTo(0f, mBannerRadius)
        path.lineTo(0f, 0f)
        path.lineTo(mBannerRadius, 0f)
        path.arcTo(RectF(0f, 0f, mBannerRadius * 2, mBannerRadius * 2), -90f, -90f)
        path.close()
        mRoundPaint?.let {
            canvas?.drawPath(path, it)
        }

    }

    private fun drawTopRight(canvas: Canvas?) {
        val width = width
        val path = Path()
        path.moveTo(width - mBannerRadius, 0f)
        path.lineTo(width.toFloat(), 0f)
        path.lineTo(width.toFloat(), mBannerRadius)
        path.arcTo(
            RectF(width - 2 * mBannerRadius, 0f, width.toFloat(), mBannerRadius * 2),
            0f,
            -90f
        )
        path.close()
        mRoundPaint?.let {
            canvas?.drawPath(path, it)
        }

    }

    private fun drawBottomLeft(canvas: Canvas?) {
        val height = height
        val path = Path()
        path.moveTo(0f, height - mBannerRadius)
        path.lineTo(0f, height.toFloat())
        path.lineTo(mBannerRadius, height.toFloat())
        path.arcTo(
            RectF(0f, height - 2 * mBannerRadius, mBannerRadius * 2, height.toFloat()),
            90f,
            90f
        )
        path.close()
        mRoundPaint?.let {
            canvas?.drawPath(path, it)
        }

    }

    private fun drawBottomRight(canvas: Canvas?) {
        val height = height
        val width = width
        val path = Path()
        path.moveTo(width - mBannerRadius, height.toFloat())
        path.lineTo(width.toFloat(), height.toFloat())
        path.lineTo(width.toFloat(), height - mBannerRadius)
        path.arcTo(
            RectF(
                width - 2 * mBannerRadius,
                height - 2 * mBannerRadius,
                width.toFloat(),
                height.toFloat()
            ), 0f, 90f
        )
        path.close()
        mRoundPaint?.let {
            canvas?.drawPath(path, it)
        }

    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        start()
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        stop()
    }


    fun getAdapter(): BannerAdapter<T, out RecyclerView.ViewHolder>? {
        return mAdapter
    }

    fun getIsAutoLoop(): Boolean {
        return mIsAutoLoop
    }

    fun getViewPage(): ViewPager2? {
        return mViewPager2

    }

    fun getLoopTask(): AutoLoopTask<T>? {
        return mLoopTask
    }

    fun getLoopTime(): Long {
        return mLoopTime

    }

    fun getScrollTime(): Int {
        return mScrollTime
    }
    /**
     * 设置轮播指示器(显示在banner上)
     */
    fun setIndicator(indicator: Indicator): Banner<T>? {
        return setIndicator(indicator, true)
    }

    /**
     * 设置轮播指示器(如果你的指示器写在布局文件中，attachToBanner传false)
     *
     * @param attachToBanner 是否将指示器添加到banner中，false 代表你可以将指示器通过布局放在任何位置
     * 注意：设置为false后，内置的 setIndicatorGravity()和setIndicatorMargins() 方法将失效。
     * 想改变可以自己调用系统提供的属性在布局文件中进行设置
     */
    fun setIndicator(indicator: Indicator, attachToBanner: Boolean): Banner<T>? {
        removeIndicator()
        indicator.getIndicatorConfig()?.isAttachToBanner=attachToBanner
        mIndicator = indicator
        initIndicator()
        return this
    }
}
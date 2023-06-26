package com.example.banner.adapter

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.banner.holder.IViewHolder
import com.example.banner.listener.OnBannerListener
import com.example.banner.utils.BannerConfig
import com.example.banner.utils.BannerUtils

/**

 *
 * @author: raotong
 * @date: 2023年06月20日 18:02
 * @Description:
 */
abstract class BannerAdapter<T, VH : RecyclerView.ViewHolder>(var mData: ArrayList<T>) :
    RecyclerView.Adapter<VH>(),
    IViewHolder<T, VH> {
    //    private var mData: ArrayList<T> = ArrayList()
    private var mOnBannerListener: OnBannerListener<T>? = null
    private var mViewHolder: VH? = null
    private var mIncreaseCount: Int = BannerConfig.INCREASE_COUNT

    fun setOnBannerListener(onBannerListener: OnBannerListener<T>?) {
        mOnBannerListener = onBannerListener
    }

    open fun setData(data: ArrayList<T>) {

        this.mData = data
        notifyDataSetChanged()
    }

    /**
     * 获取数据
     * */
    open fun getData(position: Int): T? {
        return if (position < mData.size) {
            mData[position]
        } else {
            null
        }

    }

    /**
     * 获取当前布局
     * */
    fun getViewHolder(): VH? {
        return mViewHolder
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val vh = onCreateHolder(parent, viewType)
        return vh

    }


    override fun onBindViewHolder(holder: VH, position: Int) {
        var visposition = position % (mData.size)
        var data = mData[visposition]
        mViewHolder = holder
        onBindHolder(holder, data, visposition)
        holder.itemView.setOnClickListener {
            mOnBannerListener?.onBannerClick(visposition, data)

        }

    }


    override fun getItemCount(): Int {
        return if (getRealCount() > 1) getRealCount() + mIncreaseCount else getRealCount()
    }

    open fun getRealCount(): Int {
        return if (mData == null) 0 else mData.size
    }

    open fun getRealPosition(position: Int): Int {
        return BannerUtils.getRealPosition(
            mIncreaseCount == BannerConfig.INCREASE_COUNT,
            position,
            getRealCount()
        )
    }

    fun setIncreaseCount(increaseCount: Int) {
        mIncreaseCount = increaseCount
    }


}
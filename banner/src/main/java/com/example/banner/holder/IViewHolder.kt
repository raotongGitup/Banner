package com.example.banner.holder

import android.view.ViewGroup

/**

 *
 * @author: raotong
 * @date: 2023年06月20日 18:06
 * @Description:
 */
interface IViewHolder<T, VH> {

  /**
   *
   * 创建 viewholder
   * @param parent ViewGroup 布局
   * @param viewType Int 类型
   * @return
   *
   * */

    fun onCreateHolder(parent: ViewGroup?, viewType: Int): VH

    /**
     * 绑定布局
     * @param holder 布局
     * @param data 数据
     * @return
     * */

    fun onBindHolder(holder: VH, data: T, position: Int)


}
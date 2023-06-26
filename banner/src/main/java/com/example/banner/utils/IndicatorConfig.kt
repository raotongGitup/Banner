package com.example.banner.utils

import androidx.annotation.ColorInt
import java.lang.annotation.Retention
import java.lang.annotation.RetentionPolicy

class IndicatorConfig {
    var indicatorSize : Int = 0

    var currentPosition : Int = 0

    var gravity  = Direction.CENTER

    var indicatorSpace : Int = BannerConfig.INDICATOR_SPACE

    var normalWidth : Int = BannerConfig.INDICATOR_NORMAL_WIDTH

    var selectedWidth : Int  = BannerConfig.INDICATOR_SELECTED_WIDTH


    @ColorInt
    var normalColor  : Int = BannerConfig.INDICATOR_NORMAL_COLOR


    @ColorInt
    var selectedColor  : Int = BannerConfig.INDICATOR_SELECTED_COLOR

    var radius  : Int = BannerConfig.INDICATOR_RADIUS

    var height  : Int = BannerConfig.INDICATOR_HEIGHT

    private var margins: Margins? = null

    //是将指示器添加到banner上
    var isAttachToBanner = true


    @Retention(RetentionPolicy.SOURCE)
    annotation class Direction {
        companion object {
            var LEFT = 0
            var CENTER = 1
            var RIGHT = 2
        }
    }

    class Margins(
        var leftMargin: Int,
        var topMargin: Int,
        var rightMargin: Int,
        var bottomMargin: Int
    ) {
        @JvmOverloads
        constructor(marginSize: Int = BannerConfig.INDICATOR_MARGIN) : this(
            marginSize,
            marginSize,
            marginSize,
            marginSize
        ) {
        }
    }

    fun getMargins(): Margins? {
        if (margins == null) {
            setMargins(Margins())
        }
        return margins
    }



    fun setMargins(margins: Margins?): IndicatorConfig {
        this.margins = margins
        return this
    }

    fun setIndicatorSize(indicatorSize: Int): IndicatorConfig {
        this.indicatorSize = indicatorSize
        return this
    }



    fun setSelectedColor(selectedColor: Int): IndicatorConfig {
        this.selectedColor = selectedColor
        return this
    }

    fun setIndicatorSpace(indicatorSpace: Int): IndicatorConfig {
        this.indicatorSpace = indicatorSpace
        return this
    }

    fun setCurrentPosition(currentPosition: Int): IndicatorConfig {
        this.currentPosition = currentPosition
        return this
    }

    fun setNormalWidth(normalWidth: Int): IndicatorConfig {
        this.normalWidth = normalWidth
        return this
    }

    fun setSelectedWidth(selectedWidth: Int): IndicatorConfig {
        this.selectedWidth = selectedWidth
        return this
    }




    fun setRadius(radius: Int): IndicatorConfig {
        this.radius = radius
        return this
    }

    fun setHeight(height: Int): IndicatorConfig {
        this.height = height
        return this
    }





}
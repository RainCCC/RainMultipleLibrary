package com.rain.liba_customviewutil.util

import android.content.Context

/**
 *
 * Describe # 像素转换
 *
 * Created by Rain on 2018/6/12.
 */
object DensityUtils {
    /**
     * dp转换成px
     */
    fun dp2px(context: Context?, dpValue: Float): Int {
        context?.let {
            val scale = it.resources.displayMetrics.density
            return (dpValue * scale + 0.5f).toInt()
        }
        return 0
    }

    /**
     * px转换成dp
     */
    fun px2dp(context: Context?, pxValue: Float): Int {
        context?.let {
            val scale = it.resources.displayMetrics.density
            return (pxValue / scale + 0.5f).toInt()
        }
        return 0
    }

    /**
     * sp转换成px
     */
    fun sp2px(context: Context?, spValue: Float): Int {
        context?.let {
            val fontScale = it.resources.displayMetrics.scaledDensity
            return (spValue * fontScale + 0.5f).toInt()
        }
        return 0

    }

    /**
     * px转换成sp
     */
    fun px2sp(context: Context?, pxValue: Float): Int {
        context?.let {
            val fontScale = it.resources.displayMetrics.scaledDensity
            return (pxValue / fontScale + 0.5f).toInt()
        }
        return 0
    }
}
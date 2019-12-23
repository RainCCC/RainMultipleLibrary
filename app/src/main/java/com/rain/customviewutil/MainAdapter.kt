package com.rain.customviewutil

import android.app.Activity
import com.rain.liba_rainbaseadapter.base.RainBaseRvAdapter
import com.rain.liba_rainbaseadapter.base.RainBaseRvViewHolder

/**
 *
 * @author Rain
 * @Date on 2019-12-23
 * @Description RecyclerView适配器
 *
 */
class MainAdapter(activity: Activity, list: MutableList<String>) : RainBaseRvAdapter<String>(activity, list) {

    /**
     * 设置数据
     */
    override fun convertData(holder: RainBaseRvViewHolder?, entity: String?, position: Int) {
        holder?.getTextView(R.id.vTvContent)?.text = entity
    }

    /**
     * adapter使用的layout
     */
    override fun getLayoutViewId(viewType: Int): Int {
        return R.layout.adapter_main
    }

}
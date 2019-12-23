package com.rain.liba_rainbaseadapter.listener;

/**
 * Describe # 下拉加载更多点击监听
 * <p>
 * Created by Rain on 2019/12/20
 */
public interface RainAdapterLoadMoreClickListener {
    void onLoadMoreClick();

    void onLoadFinishClick();

    void onLoadErrorClick();
}
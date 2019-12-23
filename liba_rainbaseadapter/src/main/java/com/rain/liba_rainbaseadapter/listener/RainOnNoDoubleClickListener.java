package com.rain.liba_rainbaseadapter.listener;

import android.view.View;

import java.util.Calendar;

/**
 * Describe # 防止多点触控
 * <p>
 * Created by Rain on 2019/12/20
 */
public abstract class RainOnNoDoubleClickListener implements View.OnClickListener {

    public static final int MIN_CLICK_DELAY_TIME = 500;
    private long lastClickTime = 0;

    @Override
    public void onClick(View v) {
        long currentTime = Calendar.getInstance().getTimeInMillis();
        if (currentTime - lastClickTime > MIN_CLICK_DELAY_TIME) {
            lastClickTime = currentTime;
            onNoDoubleClick(v);
        }
    }

    protected abstract void onNoDoubleClick(View v);

}

package com.rain.liba_rainbaseadapter.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.rain.liba_rainbaseadapter.R;
import com.rain.liba_rainbaseadapter.listener.RainAdapterLoadMoreClickListener;

/**
 * Describe # adapter上拉加载更多view
 * <p>
 * Created by Rain on 2019/12/20
 */
public class RainLoadMoreView extends LinearLayout implements View.OnClickListener {

    private LinearLayout mLoadingLl, mFinishLl, mErrorLl;
    private TextView mLoadingTv, mFinishTv, mErrorTv;
    private RainAdapterLoadMoreClickListener mListener;

    public RainLoadMoreView(Context context) {
        super(context);
        init();
    }

    public RainLoadMoreView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public RainLoadMoreView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        LayoutInflater.from(getContext()).inflate(R.layout.rba_layout_load_more, this);
        setView();
    }

    /**
     * 初始化数据
     */
    private void setView() {
        mLoadingLl = findViewById(R.id.base_adapter_ll_loading);
        mFinishLl = findViewById(R.id.base_adapter_ll_finish);
        mErrorLl = findViewById(R.id.base_adapter_ll_error);
        mLoadingTv = findViewById(R.id.base_adapter_tv_loading);
        mFinishTv = findViewById(R.id.base_adapter_tv_finish);
        mErrorTv = findViewById(R.id.base_adapter_tv_error);
        showLoadMore();
        mLoadingLl.setOnClickListener(this);
        mFinishLl.setOnClickListener(this);
        mErrorLl.setOnClickListener(this);
    }

    public void showLoadMore() {
        mErrorLl.setVisibility(View.GONE);
        mFinishLl.setVisibility(View.GONE);
        mLoadingLl.setVisibility(View.VISIBLE);
    }

    public void showLoadFinish() {
        mErrorLl.setVisibility(View.GONE);
        mFinishLl.setVisibility(View.VISIBLE);
        mLoadingLl.setVisibility(View.GONE);
    }

    public void showLoadError() {
        mErrorLl.setVisibility(View.VISIBLE);
        mFinishLl.setVisibility(View.GONE);
        mLoadingLl.setVisibility(View.GONE);
    }

    public void setLoadErrorTitle(String str) {
        mErrorTv.setText(str);
    }

    public void setLoadFinishTitle(String str) {
        mFinishTv.setText(str);
    }

    public void setLoadingTitle(String str) {
        mLoadingTv.setText(str);
    }

    /**
     * 设置点击监听
     *
     * @param listener
     */
    public void setAdapterLoadMoreClickListener(RainAdapterLoadMoreClickListener listener) {
        this.mListener = listener;
    }

    @Override
    public void onClick(View v) {
        if (v == mLoadingLl) {
            if (null != mListener)
                mListener.onLoadMoreClick();
        } else if (v == mFinishLl) {
            if (null != mListener)
                mListener.onLoadFinishClick();
        } else if (v == mErrorLl) {
            if (null != mListener)
                showLoadMore();
            mListener.onLoadErrorClick();
        }
    }

}

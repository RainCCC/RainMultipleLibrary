package com.rain.liba_rainbaseadapter.base;

import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.HashMap;
import java.util.Map;

/**
 * Describe # RecyclerView 的ViewHolder基类
 * <p>
 * Created by Rain on 2019/12/20
 */
public class RainBaseRvViewHolder extends RecyclerView.ViewHolder {

    public RainBaseRvViewHolder(@NonNull View itemView) {
        super(itemView);
    }

    private Map<Integer, View> mViewMap = new HashMap<>();

    /**
     * 获取设置的view
     *
     * @param id
     * @return_transfer
     */
    public <T extends View> T getView(int id) {
        View view = mViewMap.get(id);
        if (null == view) {
            view = itemView.findViewById(id);
            mViewMap.put(id, view);
        }
        return (T) view;
    }

    public TextView getTextView(int id) {
        return ((TextView) getView(id));
    }

    public ImageView getImageView(int id) {
        return ((ImageView) getView(id));
    }

    public Button getButton(int id) {
        return ((Button) getView(id));
    }

    public EditText getEditText(int id) {
        return ((EditText) getView(id));
    }

}

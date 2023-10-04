package com.example.myapplication;

import android.content.Context;
import android.view.View;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class LayoutManager extends LinearLayoutManager {
    public LayoutManager(Context context) {
        super(context);
    }

    @Override
    public void onLayoutChildren(RecyclerView.Recycler recycler, RecyclerView.State state) {
        super.onLayoutChildren(recycler, state);

        for (int i = 0; i < getChildCount(); i++) {
            View child = getChildAt(i);
            float offset = child.getY(); // 获取item在RecyclerView中的Y坐标
            float itemHeight = child.getHeight(); // 获取item的高度

            // 根据偏移量和高度，设置item的缩放和透明度
            float scale = 1 - offset / itemHeight;
            child.setScaleX(scale);
            child.setScaleY(scale);
            child.setAlpha(scale);
        }
    }
}

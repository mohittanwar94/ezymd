package com.ezymd.restaurantapp.utils;

import android.graphics.Rect;
import android.view.View;

import androidx.recyclerview.widget.RecyclerView;

public class HorizontialSpacesItemDecoration extends RecyclerView.ItemDecoration {
    private final int space;

    public HorizontialSpacesItemDecoration(int space) {
        this.space = space;
    }

    @Override
    public void getItemOffsets(Rect outRect, View view,
                               RecyclerView parent, RecyclerView.State state) {

        outRect.right = space;

        // Add top margin only for the first item to avoid double space between items
        if (parent.getChildLayoutPosition(view) == 0) {
            outRect.left = 0;
        } else {
            outRect.left = space;
        }

    }
}

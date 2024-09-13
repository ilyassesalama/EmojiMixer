package com.emojimixer.classes.recylerview;

import android.graphics.Rect;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class OffsetItemDecoration extends RecyclerView.ItemDecoration {
    private final int offset;

    public OffsetItemDecoration(int offset) {
        this.offset = offset;
    }

    @Override
    public void getItemOffsets(
            @NonNull Rect outRect,
            @NonNull View view,
            @NonNull RecyclerView parent,
            @NonNull RecyclerView.State state
    ) {
        int position = parent.getChildAdapterPosition(view);
        int itemCount = state.getItemCount();

        if (position == 0) {
            outRect.left = offset;
        } else if (position == itemCount - 1) {
            outRect.right = offset;
        }
    }
}

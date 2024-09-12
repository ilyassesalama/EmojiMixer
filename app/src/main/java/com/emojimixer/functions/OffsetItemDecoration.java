package com.emojimixer.functions;

import android.app.Activity;
import android.content.Context;
import android.graphics.Rect;
import android.os.Build;
import android.view.View;
import android.view.ViewGroup.MarginLayoutParams;
import android.view.WindowInsets;
import androidx.recyclerview.widget.RecyclerView;

public class OffsetItemDecoration extends RecyclerView.ItemDecoration {
    private final Context ctx;

    public OffsetItemDecoration(Context ctx) {
        this.ctx = ctx;
    }

    @Override
    public void getItemOffsets(
            Rect outRect,
            View view,
            RecyclerView parent,
            RecyclerView.State state
    ) {
        super.getItemOffsets(outRect, view, parent, state);

        int offset = (int) (getScreenWidth((Activity) ctx) / 2.0f) - view.getWidth() / 2;

        if (parent.getChildAdapterPosition(view) == 0) {
            ((MarginLayoutParams) view.getLayoutParams()).leftMargin = 0;
            setupOutRect(outRect, offset, true);
        } else if (parent.getChildAdapterPosition(view) == state.getItemCount() - 1) {
            ((MarginLayoutParams) view.getLayoutParams()).rightMargin = 0;
            setupOutRect(outRect, offset, false);
        }
    }

    private void setupOutRect(Rect rect, int offset, boolean start) {
        if (start) {
            rect.left = offset;
        } else {
            rect.right = offset;
        }
    }

    public static int getScreenWidth(Activity activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            WindowInsets windowInsets = activity.getWindowManager().getCurrentWindowMetrics()
                    .getWindowInsets();
            int insetsLeft = windowInsets.getInsetsIgnoringVisibility(WindowInsets.Type.systemBars()).left;
            int insetsRight = windowInsets.getInsetsIgnoringVisibility(WindowInsets.Type.systemBars()).right;
            return activity.getWindowManager().getCurrentWindowMetrics().getBounds().width() - insetsLeft - insetsRight;
        } else {
            return activity.getResources().getDisplayMetrics().widthPixels;
        }
    }
}

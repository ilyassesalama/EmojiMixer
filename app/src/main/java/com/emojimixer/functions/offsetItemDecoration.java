package com.emojimixer.functions;

import android.app.Activity;
import android.content.Context;
import android.graphics.Insets;
import android.graphics.Point;
import android.graphics.Rect;
import android.os.Build;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowInsets;
import android.view.WindowManager;
import android.view.WindowMetrics;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class offsetItemDecoration extends RecyclerView.ItemDecoration {

    private final Context ctx;

    public offsetItemDecoration(Context ctx) {

        this.ctx = ctx;
    }

    @Override
    public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {

        super.getItemOffsets(outRect, view, parent, state);
        int offset = (int) (getScreenWidth((Activity) ctx) / (float) (2)) - view.getLayoutParams().width / 2;
        ViewGroup.MarginLayoutParams lp = (ViewGroup.MarginLayoutParams) view.getLayoutParams();
        if (parent.getChildAdapterPosition(view) == 0) {
            ((ViewGroup.MarginLayoutParams) view.getLayoutParams()).leftMargin = 0;
            setupOutRect(outRect, offset, true);
        } else if (parent.getChildAdapterPosition(view) == state.getItemCount() - 1) {
            ((ViewGroup.MarginLayoutParams) view.getLayoutParams()).rightMargin = 0;
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


    public static int getScreenWidth(@NonNull Activity activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            WindowMetrics windowMetrics = activity.getWindowManager().getCurrentWindowMetrics();
            Insets insets = windowMetrics.getWindowInsets()
                    .getInsetsIgnoringVisibility(WindowInsets.Type.systemBars());
            return windowMetrics.getBounds().width() - insets.left - insets.right;
        } else {
            DisplayMetrics displayMetrics = new DisplayMetrics();
            activity.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
            return displayMetrics.widthPixels;
        }
    }


}
package com.emojimixer.classes.recylerview;

import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearSnapHelper;
import androidx.recyclerview.widget.OrientationHelper;
import androidx.recyclerview.widget.RecyclerView;

public class CenteredSnapHelper extends LinearSnapHelper {

    @Override
    public int[] calculateDistanceToFinalSnap(
            @NonNull RecyclerView.LayoutManager layoutManager,
            @NonNull View targetView
    ) {
        int[] out = new int[2];

        if (layoutManager.canScrollHorizontally()) {
            out[0] = distanceToCenter(targetView, getHorizontalHelper(layoutManager));
        } else {
            out[0] = 0;
        }

        if (layoutManager.canScrollVertically()) {
            out[1] = distanceToCenter(targetView, getVerticalHelper(layoutManager));
        } else {
            out[1] = 0;
        }

        return out;
    }

    private int distanceToCenter(View targetView, OrientationHelper helper) {
        int childCenter = helper.getDecoratedStart(targetView) +
                (helper.getDecoratedMeasurement(targetView) / 2);
        int containerCenter = helper.getStartAfterPadding() +
                (helper.getTotalSpace() / 2);
        return childCenter - containerCenter;
    }

    private OrientationHelper getHorizontalHelper(RecyclerView.LayoutManager layoutManager) {
        return OrientationHelper.createHorizontalHelper(layoutManager);
    }

    private OrientationHelper getVerticalHelper(RecyclerView.LayoutManager layoutManager) {
        return OrientationHelper.createVerticalHelper(layoutManager);
    }
}

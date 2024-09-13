package com.emojimixer.components;

import android.content.Context;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SnapHelper;

import com.emojimixer.adapters.EmojisSliderAdapter;
import com.emojimixer.classes.recylerview.CenterZoomLayoutManager;
import com.emojimixer.classes.recylerview.CenteredSnapHelper;
import com.emojimixer.classes.recylerview.OffsetItemDecoration;
import com.emojimixer.utils.Utils;

import java.util.ArrayList;
import java.util.HashMap;

public class EmojiSlider {
    private final RecyclerView recyclerView;
    private final Context context;
    private final CenterZoomLayoutManager layoutManager;
    private final SnapHelper snapHelper;
    private EmojisSliderAdapter adapter;
    private ArrayList<HashMap<String, Object>> emojisList;
    private EmojiSliderListener listener;

    public interface EmojiSliderListener {
        void onEmojiSelected(String emojiUnicode, String date);
    }

    public EmojiSlider(RecyclerView recyclerView, Context context) {
        this.recyclerView = recyclerView;
        this.context = context;

        // Initialize LayoutManager and SnapHelper
        layoutManager = new CenterZoomLayoutManager(context);
        recyclerView.setLayoutManager(layoutManager);

        snapHelper = new CenteredSnapHelper();
        snapHelper.attachToRecyclerView(recyclerView);

        // Set up OffsetItemDecoration after layout
        recyclerView.post(this::setOffsetDecoration);

        // Listen to scroll events
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView rv, int newState) {
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    notifyEmojiSelected();
                }
            }
        });
    }

    private void setOffsetDecoration() {
        if (recyclerView.getChildCount() > 0) {
            View child = recyclerView.getChildAt(0);
            int itemWidth = child.getWidth();
            int recyclerViewWidth = recyclerView.getWidth();
            int offset = (recyclerViewWidth - itemWidth) / 2;

            recyclerView.addItemDecoration(new OffsetItemDecoration(offset));
        } else {
            recyclerView.post(this::setOffsetDecoration);
        }
    }

    public void setData(ArrayList<HashMap<String, Object>> data) {
        this.emojisList = data;
        adapter = new EmojisSliderAdapter(data, context);
        recyclerView.setAdapter(adapter);
    }

    public void setListener(EmojiSliderListener listener) {
        this.listener = listener;
    }

    public void scrollToRandomPosition() {
        if (emojisList != null && !emojisList.isEmpty()) {
            int randomPos = (int) (Math.random() * emojisList.size());
            scrollToPositionAndSelect(randomPos);
        }
    }

    public void scrollToPositionAndSelect(int position) {
        recyclerView.scrollToPosition(position);

        recyclerView.post(() -> {
            View viewAtPosition = layoutManager.findViewByPosition(position);
            if (viewAtPosition != null) {
                int[] snapDistances = snapHelper.calculateDistanceToFinalSnap(layoutManager, viewAtPosition);
                if (snapDistances != null && (snapDistances[0] != 0 || snapDistances[1] != 0)) {
                    recyclerView.scrollBy(snapDistances[0], snapDistances[1]);
                }
            } else {
                recyclerView.post(() -> scrollToPositionAndSelect(position));
                return;
            }
            notifyEmojiSelected();
        });
    }

    private void notifyEmojiSelected() {
        int currentItem = Utils.getRecyclerCurrentItem(recyclerView, snapHelper, layoutManager);
        if (currentItem != RecyclerView.NO_POSITION && listener != null && emojisList != null && !emojisList.isEmpty()) {
            HashMap<String, Object> emojiData = emojisList.get(currentItem);
            String emojiUnicode = emojiData.get("emojiUnicode").toString();
            String date = emojiData.get("date").toString();
            listener.onEmojiSelected(emojiUnicode, date);
        }
    }
}

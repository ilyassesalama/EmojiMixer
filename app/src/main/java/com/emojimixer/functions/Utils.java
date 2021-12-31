package com.emojimixer.functions;

import android.view.View;

import androidx.recyclerview.widget.LinearSnapHelper;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SnapHelper;

public class Utils {

    public static String convertEmojisToUnicode(String emoji) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < emoji.length(); i++) {
            if (Character.isSurrogate(emoji.charAt(i))) {
                int res = Character.codePointAt(emoji, i);
                i++;
                sb.append("u").append(Integer.toHexString(res).toLowerCase());
            } else {
                sb.append(emoji.charAt(i));
            }
        }
        return sb.toString();
    }
    public static void setSnapHelper(RecyclerView recyclerView, SnapHelper snapHelper, RecyclerView.LayoutManager layoutManager) {
//        why it should be re-assigned while it was passed as parameter?
//        snapHelper = new LinearSnapHelper();
        recyclerView.setLayoutManager(layoutManager);
        snapHelper.attachToRecyclerView(recyclerView);
    }

    public static int getRecyclerCurrentItem(RecyclerView recyclerView, SnapHelper snapHelper, RecyclerView.LayoutManager layoutManager) {
        View view = snapHelper.findSnapView(layoutManager);
        if (view != null) {
            return recyclerView.getChildAdapterPosition(view);
        } else {
            return 0;
        }
    }


}

package com.emojimixer.functions;

import android.app.Activity;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

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

    public static void setImageFromUrl(ImageView image, String url, Activity context) {
        Glide.with(context)
                .load(url)
                .fitCenter()
                .into(image);

    }
}

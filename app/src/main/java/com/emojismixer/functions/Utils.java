package com.emojismixer.functions;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.github.twocoffeesoneteam.glidetovectoryou.GlideToVectorYou;

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

    public static void setImageFromUri(ImageView image, String url, Context context) {
        Glide.with(context)
                .load(url)
                .fitCenter()
                .into(image);
    }

    public static void setSVGFromUrl(ImageView image, String url, Activity context) {
        Uri uri = Uri.parse(url);
        GlideToVectorYou.justLoadImage(context, uri, image);
    }
}

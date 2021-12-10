package com.emojismixer.functions;

import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.widget.ImageView;

import com.ahmadrosid.svgloader.SvgLoader;

import java.io.InputStream;
import java.net.URL;

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

    public static void setImageFromUri(ImageView image, String url, Activity context) {
        new Thread(() -> {
            try {
                InputStream is = (InputStream) new URL(url).getContent();
                Drawable d = Drawable.createFromStream(is, "src");
                image.setImageDrawable(d);
            } catch (Exception ignored) {
            }
            context.runOnUiThread(() -> {
            });
        }).start();

    }

    public static void setSVGFromUrl(ImageView image, String url, Activity context) {
        SvgLoader.pluck()
                .with(context)
                .load(url, image);
    }
}

package com.emojimixer.utils;

import android.graphics.drawable.Drawable;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SnapHelper;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.function.Consumer;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class Utils {
    private static final Handler mainHandler = new Handler(Looper.getMainLooper());

    /**
     * Run a Runnable on the UI thread without a Context.
     */
    public static void runOnUiThread(Runnable runnable) {
        mainHandler.post(runnable);
    }

    /**
     * Run a Runnable on the UI thread without a Context after duration.
     */
    public static void waitThenRun(long duration, Runnable runnable) {
        mainHandler.postDelayed(runnable, duration);
    }

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

    public static int getRecyclerCurrentItem(RecyclerView recyclerView, SnapHelper snapHelper, RecyclerView.LayoutManager layoutManager) {
        View view = snapHelper.findSnapView(layoutManager);
        if (view != null) {
            return recyclerView.getChildAdapterPosition(view);
        } else {
            return 0;
        }
    }

    public static void setImageFromUrl(ImageView image, String url) {
        Glide.with(image.getContext())
                .load(url)
                .fitCenter()
                .transition(DrawableTransitionOptions.withCrossFade())
                .into(image);
    }

    public static void setImageFromUrl(ImageView image, String url, Consumer<Boolean> callback) {
        Glide.with(image.getContext())
                .load(url)
                .fitCenter()
                .transition(DrawableTransitionOptions.withCrossFade())
                .listener(new RequestListener<>() {
                    @Override
                    public boolean onLoadFailed(GlideException e, Object model, @NonNull Target<Drawable> target, boolean isFirstResource) {
                        callback.accept(false);
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(@NonNull Drawable resource, @NonNull Object model, @NonNull Target<Drawable> target, @NonNull DataSource dataSource, boolean isFirstResource) {
                        callback.accept(true);
                        return false;
                    }
                })
                .into(image);
    }

    public static void saveImageToDevice(String url) {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder().url(url).build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                Log.e("Utils", "Failed to save image: " + e);
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if (response.isSuccessful()) {
                    try (InputStream is = response.body().byteStream()) {
                        File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), "emoji_" + System.currentTimeMillis() + ".png");
                        try (FileOutputStream fos = new FileOutputStream(file)) {
                            byte[] buffer = new byte[8192];
                            int bytesRead;
                            while ((bytesRead = is.read(buffer)) != -1) {
                                fos.write(buffer, 0, bytesRead);
                            }
                        }
                    }
                } else {
                    Log.e("Utils", "Failed to save image: " + response.message());
                }
            }
        });
    }
}

package com.emojismixer;

import android.app.DownloadManager;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.webkit.CookieManager;
import android.webkit.URLUtil;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.google.android.material.button.MaterialButton;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainActivity extends AppCompatActivity {
    public static final String LOG_API = "API_LOG";
    private final int maxDatesCount = 2;
    private MaterialButton mixEmojis;
    private MaterialButton saveEmoji;
    private EditText input_emoji1;
    private EditText input_emoji2;
    private ImageView mixedEmoji;
    private ImageView loading;
    private String API = "https://www.gstatic.com/android/keyboard/emojikitchen/";
    private String finalEmojiURL = "";
    private String unicode1 = "";
    private String unicode2 = "";
    private int iteratorCount = 0;
    private boolean callingForReverse = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initLogic();
        LOGIC_BACKEND();
    }

    public void initLogic() {
        mixEmojis = findViewById(R.id.mixEmojis);
        loading = findViewById(R.id.loading);
        input_emoji1 = findViewById(R.id.input_emoji1);
        input_emoji2 = findViewById(R.id.input_emoji2);
        mixedEmoji = findViewById(R.id.mixedEmoji);
        saveEmoji = findViewById(R.id.saveEmoji);

        mixEmojis.setOnClickListener(view -> {
            unicode1 = convertEmojisToUnicode(input_emoji1.getText().toString().trim());
            unicode2 = convertEmojisToUnicode(input_emoji2.getText().toString().trim());
            if (unicode1.isEmpty() || unicode2.isEmpty()) {
                Toast.makeText(this, "Enter 1 emoji in both fields", Toast.LENGTH_SHORT).show();
            } else {
                callingForReverse = false;
                iteratorCount = 0;
                mixEmojis(true, unicode1, unicode2);
            }
        });
        saveEmoji.setOnClickListener(view -> downloadFile(finalEmojiURL));
    }

    private void LOGIC_BACKEND() {
        RotateAnimation rotate = new RotateAnimation(0, 360, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        rotate.setDuration(900);
        rotate.setRepeatCount(Animation.INFINITE);
        rotate.setInterpolator(new LinearInterpolator());
        loading.startAnimation(rotate);
    }

    private void mixEmojis(boolean isFirstResource, String emoji1, String emoji2) {
        saveEmoji.setVisibility(View.GONE);
        loading.setVisibility(View.VISIBLE);
        String date = "";
        if (isFirstResource) {
            date = "20201001";
        } else {
            if (iteratorCount == 0) {
                date = "20210521";
            } else if (iteratorCount == 1) {
                date = "20210218";
            } else if (iteratorCount == 2) {
                date = "20210831";
            }
            iteratorCount++;
        }

        String unicode1 = convertEmojisToUnicode(emoji1);
        String unicode2 = convertEmojisToUnicode(emoji2);
        String EmojiURL = API + date + "/" + unicode1 + "/" + unicode1 + "_" + unicode2 + ".png";
        Log.d(LOG_API, "Testing if this link is working: " + EmojiURL);
        setImageFromUri(mixedEmoji, EmojiURL);
    }

    private String convertEmojisToUnicode(String emoji) {
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

    public void setImageFromUri(ImageView image, String url) {
        Handler handler = new Handler();
        Glide.with(this)
                .load(url)
                .fitCenter()
                .transition(DrawableTransitionOptions.withCrossFade())
                .listener(new RequestListener<Drawable>() {
                              @Override
                              public boolean onLoadFailed(GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                                  if (maxDatesCount == iteratorCount) {
                                      if (callingForReverse) {
                                          //Used setImageResource instead of .error() in Glide because it causes some glitches when there are too many failed attempts.
                                          mixedEmoji.setImageResource(R.drawable.sad);
                                          Toast.makeText(MainActivity.this, "No combinations found :(", Toast.LENGTH_SHORT).show();
                                      } else {
                                          Log.d(LOG_API, "Couldn't find an appropriate emoji, but wait we're not done yet. Reversing emojis order and trying again...");
                                          callingForReverse = true;
                                          iteratorCount = 0;
                                          handler.post(() -> mixEmojis(true, unicode2, unicode1));
                                      }
                                  } else {
                                      if (callingForReverse) {
                                          handler.post(() -> mixEmojis(false, unicode2, unicode1));
                                      } else {
                                          handler.post(() -> mixEmojis(false, unicode1, unicode2));
                                      }
                                  }
                                  return false;
                              }

                              @Override
                              public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                                  Log.d(LOG_API, "This URL worked!!: " + url);
                                  finalEmojiURL = url;
                                  loading.setVisibility(View.INVISIBLE);
                                  saveEmoji.setVisibility(View.VISIBLE);
                                  return false;
                              }
                          }
                )
                .into(image);
    }


    private void downloadFile(String url) {
        Toast.makeText(this, "Download started, check notifications bar.", Toast.LENGTH_SHORT).show();
        String fileName = "MixedEmoji_" + new SimpleDateFormat("yyyy-MM-dd HH-mm-ss", Locale.US).format(new Date());
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());
        executor.execute(() -> {
            try {
                DownloadManager downloadmanager = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
                DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));
                request.setMimeType(getContentResolver().getType(Uri.parse(url)));
                request.addRequestHeader("cookie", CookieManager.getInstance().getCookie(url));
                request.setTitle(fileName);
                request.setDescription("Downloading your cool mixed emoji...");
                request.allowScanningByMediaScanner();
                request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
                request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, "/MixedEmojis/" + fileName + URLUtil.guessFileName(url, "", ""));
                downloadmanager.enqueue(request);
                handler.post(() -> {
                });
            } catch (Exception e) {
                Log.e("Download error", e.toString());
            }
        });
    }
}

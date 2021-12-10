package com.emojismixer;

import static com.emojismixer.functions.UIMethods.rotateAnimation;
import static com.emojismixer.functions.UIMethods.shadAnim;

import android.Manifest;
import android.app.DownloadManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.webkit.CookieManager;
import android.webkit.URLUtil;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.emojismixer.functions.CheckEmojiExistence;
import com.emojismixer.functions.CheckEmojiExistence.EmojiListener;
import com.google.android.material.button.MaterialButton;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainActivity extends AppCompatActivity {
    private MaterialButton mixEmojis;
    private MaterialButton saveEmoji;
    private EditText input_emoji1;
    private EditText input_emoji2;
    private ImageView mixedEmoji;
    private ImageView loading;
    private String finalEmojiURL = "";
    private String emote1 = "";
    private String emote2 = "";

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
            emote1 = input_emoji1.getText().toString().trim();
            emote2 = input_emoji2.getText().toString().trim();

            if (emote1.isEmpty() || emote2.isEmpty()) {
                Toast.makeText(this, "Enter 1 emoji in both fields", Toast.LENGTH_SHORT).show();
            } else {
                showEmoji(false);
                mixEmojis(emote1, emote2);
            }
        });

        saveEmoji.setOnClickListener(view -> {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                downloadFile(finalEmojiURL);
            } else {
                if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                    downloadFile(finalEmojiURL);
                } else {
                    ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                }
            }

        });
    }

    private void LOGIC_BACKEND() {
        rotateAnimation(loading);
    }

    private void mixEmojis(String emoji1, String emoji2) {
        saveEmoji.setVisibility(View.GONE);
        loading.setVisibility(View.VISIBLE);

        CheckEmojiExistence cee = new CheckEmojiExistence(emoji1, emoji2, this, new EmojiListener() {
            @Override
            public void onSuccess(String emojiUrl) {
                finalEmojiURL = emojiUrl;
                showEmoji(true);
                loading.setVisibility(View.INVISIBLE);
                saveEmoji.setVisibility(View.VISIBLE);
                setImageFromUri(mixedEmoji, emojiUrl);
            }

            @Override
            public void onFailure(String failureReason) {
                Toast.makeText(MainActivity.this, failureReason, Toast.LENGTH_SHORT).show();
                showEmoji(true);
                loading.setVisibility(View.INVISIBLE);
            }
        });

        Thread thread = new Thread(cee);
        thread.start();
    }


    public void setImageFromUri(ImageView image, String url) {
        Glide.with(this)
                .load(url)
                .fitCenter()
                .placeholder(R.drawable.shocked)
                .into(image);
    }

    private void showEmoji(boolean shouldShow) {
        if (shouldShow) {
            shadAnim(mixedEmoji, "scaleY", 1, 300);
            shadAnim(mixedEmoji, "scaleX", 1, 300);
        } else {
            shadAnim(mixedEmoji, "scaleY", 0, 300);
            shadAnim(mixedEmoji, "scaleX", 0, 300);
        }
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
            } catch (Exception e) {
                Log.e("Download error", e.toString());
            }
            handler.post(() -> {
            });
        });
    }
}

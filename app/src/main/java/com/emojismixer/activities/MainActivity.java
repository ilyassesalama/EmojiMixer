package com.emojismixer.activities;

import static com.emojismixer.functions.UIMethods.colorAnimator;
import static com.emojismixer.functions.UIMethods.rotateAnimation;
import static com.emojismixer.functions.UIMethods.shadAnim;
import static com.emojismixer.functions.Utils.setImageFromUrl;

import android.Manifest;
import android.app.Activity;
import android.app.DownloadManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
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
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.palette.graphics.Palette;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.CompositePageTransformer;
import androidx.viewpager2.widget.ViewPager2;

import com.emojismixer.R;
import com.emojismixer.adapters.EmojisSliderAdapter;
import com.emojismixer.functions.CheckEmojiExistence;
import com.emojismixer.functions.CheckEmojiExistence.EmojiListener;
import com.emojismixer.functions.RequestNetwork;
import com.emojismixer.functions.RequestNetworkController;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainActivity extends AppCompatActivity {
    private ExtendedFloatingActionButton saveEmoji;
    private ImageView mixedEmoji;
    private ImageView progressBar;
    private TextView activityDesc;
    private String emote1 = "";
    private String emote2 = "";
    private String finalEmojiURL = "";
    private ViewPager2 emojisSlider1;
    private ViewPager2 emojisSlider2;
    private ArrayList<HashMap<String, Object>> supportedEmojisList = new ArrayList<>();
    private RequestNetwork requestSupportedEmojis;
    private RequestNetwork.RequestListener requestSupportedEmojisListener;
    private SharedPreferences sharedPref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initLogic();
        LOGIC_BACKEND();
    }

    public void initLogic() {
        progressBar = findViewById(R.id.progressBar);
        activityDesc = findViewById(R.id.activityDesc);
        mixedEmoji = findViewById(R.id.mixedEmoji);
        saveEmoji = findViewById(R.id.saveEmoji);
        emojisSlider1 = findViewById(R.id.emojisSlider1);
        emojisSlider2 = findViewById(R.id.emojisSlider2);
        requestSupportedEmojis = new RequestNetwork(this);
        sharedPref = getSharedPreferences("AppData", Activity.MODE_PRIVATE);

        mixedEmoji.setOnClickListener(view -> {
            for (int i = 0; i < 2; i++) {
                Random rand = new Random();
                int randomNum = rand.nextInt((supportedEmojisList.size()) + 1);
                if (i == 0) {
                    emojisSlider1.setCurrentItem(randomNum);
                } else {
                    emojisSlider2.setCurrentItem(randomNum);
                }
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

        requestSupportedEmojisListener = new RequestNetwork.RequestListener() {
            @Override
            public void onResponse(String tag, String response, HashMap<String, Object> responseHeaders) {
                try {
                    supportedEmojisList = new Gson().fromJson(response, new TypeToken<ArrayList<HashMap<String, Object>>>() {
                    }.getType());
                    sharedPref.edit().putString("supportedEmojisList", new Gson().toJson(supportedEmojisList)).apply();
                    addDataToSliders(response);
                } catch (Exception e) {
                }
            }

            @Override
            public void onErrorResponse(String tag, String message) {

            }
        };
    }

    private void LOGIC_BACKEND() {
        viewpagerTransformation(emojisSlider1);
        viewpagerTransformation(emojisSlider2);
        rotateAnimation(progressBar);
        if (sharedPref.getString("supportedEmojisList", "").isEmpty()) {
            requestSupportedEmojis.startRequestNetwork(RequestNetworkController.GET, "https://ilyassesalama.github.io/EmojisMixer/emojis/supported_emojis.json", "", requestSupportedEmojisListener);
        } else {
            addDataToSliders(sharedPref.getString("supportedEmojisList", ""));
        }
    }

    private void viewpagerTransformation(ViewPager2 viewpager) {
        viewpager.setOrientation(ViewPager2.ORIENTATION_HORIZONTAL);

        viewpager.setOffscreenPageLimit(1);
        int pageMarginPx = getResources().getDimensionPixelOffset(R.dimen.margin_card);
        int peekMarginPx = getResources().getDimensionPixelOffset(R.dimen.peek_offset_card);

        RecyclerView rv = (RecyclerView) viewpager.getChildAt(0);
        rv.setClipToPadding(false);
        int padding = peekMarginPx + pageMarginPx;
        rv.setPadding(padding, 0, padding, 0);

        CompositePageTransformer transformer = new CompositePageTransformer();
        transformer.addTransformer((page, position) -> {
            float a = 1 - Math.abs(position);
            page.setScaleY(0.85f + a * 0.15f);
            page.setScaleX(0.85f + a * 0.15f);
        });
        viewpager.setPageTransformer(transformer);
    }

    private void addDataToSliders(String data) {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());
        executor.execute(() -> {
            supportedEmojisList = new Gson().fromJson(data, new TypeToken<ArrayList<HashMap<String, Object>>>() {
            }.getType());
            handler.post(() -> {
                emojisSlider1.setAdapter(new EmojisSliderAdapter(supportedEmojisList, MainActivity.this));
                emojisSlider2.setAdapter(new EmojisSliderAdapter(supportedEmojisList, MainActivity.this));
                new Handler().postDelayed(() -> {
                    for (int i = 0; i < 2; i++) {
                        Random rand = new Random();
                        int randomNum = rand.nextInt((supportedEmojisList.size()) + 1);
                        if (i == 0) {
                            emojisSlider1.setCurrentItem(randomNum);
                        } else {
                            emojisSlider2.setCurrentItem(randomNum);
                        }
                    }
                    double hex = (double) supportedEmojisList.get(emojisSlider1.getCurrentItem()).get("emojiHexCode");
                    double hex1 = (double) supportedEmojisList.get(emojisSlider2.getCurrentItem()).get("emojiHexCode");
                    emote1 = "u" + Integer.toHexString((int) hex);
                    emote2 = "u" + Integer.toHexString((int) hex1);
                    shouldShowEmoji(false);
                    mixEmojis(emote1, emote2);
                    registerViewPagersListener();
                }, 1000);

            });
        });
    }

    private void registerViewPagersListener() {
        emojisSlider1.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                new Handler().postDelayed(() -> {

                }, 100);
                double hex = (double) supportedEmojisList.get(position).get("emojiHexCode");
                emote1 = "u" + Integer.toHexString((int) hex);
                shouldShowEmoji(false);
                mixEmojis(emote1, emote2);
            }
        });
        emojisSlider2.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                double hex = (double) supportedEmojisList.get(position).get("emojiHexCode");
                emote2 = "u" + Integer.toHexString((int) hex);
                shouldShowEmoji(false);
                mixEmojis(emote1, emote2);
            }
        });
    }


    private void mixEmojis(String emoji1, String emoji2) {
        shouldEnableSave(false);
        progressBar.setVisibility(View.VISIBLE);

        CheckEmojiExistence cee = new CheckEmojiExistence(emoji1, emoji2, this, new EmojiListener() {
            @Override
            public void onSuccess(String emojiUrl) {
                finalEmojiURL = emojiUrl;
                shouldShowEmoji(true);
                progressBar.setVisibility(View.GONE);
                shouldEnableSave(true);
                setImageFromUrl(mixedEmoji, emojiUrl, MainActivity.this);
            }

            @Override
            public void onFailure(String failureReason) {
                changeActivityDesc(failureReason);
                shouldEnableSave(false);
                mixedEmoji.setImageResource(R.drawable.sad);
                shouldShowEmoji(true);
                progressBar.setVisibility(View.GONE);
            }
        });

        Thread thread = new Thread(cee);
        thread.start();
    }


    private void shouldShowEmoji(boolean shouldShow) {
        if (shouldShow) {
            shadAnim(mixedEmoji, "scaleY", 1, 300);
            shadAnim(mixedEmoji, "scaleX", 1, 300);
        } else {
            shadAnim(mixedEmoji, "scaleY", 0, 300);
            shadAnim(mixedEmoji, "scaleX", 0, 300);
        }
    }

    private void shouldEnableSave(boolean shouldShow) {

        if (shouldShow) {
            new Handler().postDelayed(() -> {
            saveEmoji.setBackgroundColor(getDominantColor(mixedEmoji));
            saveEmoji.setEnabled(true);
            saveEmoji.setTextColor(Color.parseColor("#422B0D"));
            saveEmoji.setIconTint(ColorStateList.valueOf(Color.parseColor("#422B0D")));
            }, 1000);
        } else {
            colorAnimator(saveEmoji, "#FF9D05", "#2A2B28", 250);
            saveEmoji.setEnabled(false);
            saveEmoji.setTextColor(Color.parseColor("#A3A3A3"));
            saveEmoji.setIconTint(ColorStateList.valueOf(Color.parseColor("#A3A3A3")));
        }

    }

    private void changeActivityDesc(String text) {
        shadAnim(activityDesc, "alpha", 0, 300);
        shadAnim(activityDesc, "translationY", -50, 300);
        new Handler().postDelayed(() -> {
            activityDesc.setText(text);
            shadAnim(activityDesc, "alpha", 1, 300);
            shadAnim(activityDesc, "translationY", 0, 300);
            new Handler().postDelayed(() -> {
                shadAnim(activityDesc, "alpha", 0, 300);
                shadAnim(activityDesc, "translationY", -50, 300);
                new Handler().postDelayed(() -> {
                    activityDesc.setText("Swipe left or right to mix emojis");
                    shadAnim(activityDesc, "alpha", 1, 300);
                    shadAnim(activityDesc, "translationY", 0, 300);
                }, 400);
            }, 2000);
        }, 400);
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

    public int getDominantColor(ImageView Iview) {
        Drawable drawable = Iview.getDrawable();
        BitmapDrawable bitDraw = (BitmapDrawable) drawable;
        if (bitDraw != null) {
            Bitmap bitmap = bitDraw.getBitmap();
            Palette palette = Palette.generate(bitmap);
            int vibrant = palette.getVibrantColor(0x000000);
            return vibrant;
        } else {
            return Color.parseColor("#FF9D05");
        }
    }

}

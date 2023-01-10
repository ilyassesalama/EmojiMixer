package com.emojimixer.activities;

import static androidx.recyclerview.widget.RecyclerView.SCROLL_STATE_IDLE;
import static com.emojimixer.functions.UIMethods.shadAnim;
import static com.emojimixer.functions.Utils.getRecyclerCurrentItem;
import static com.emojimixer.functions.Utils.setSnapHelper;

import android.Manifest;
import android.app.Activity;
import android.app.DownloadManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
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
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.LinearSnapHelper;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SnapHelper;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.emojimixer.R;
import com.emojimixer.adapters.EmojisSliderAdapter;
import com.emojimixer.functions.CenterZoomLayoutManager;
import com.emojimixer.functions.EmojiMixer;
import com.emojimixer.functions.RequestNetwork;
import com.emojimixer.functions.RequestNetworkController;
import com.emojimixer.functions.Utils;
import com.emojimixer.functions.offsetItemDecoration;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.progressindicator.CircularProgressIndicator;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Objects;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainActivity extends AppCompatActivity {
    private ExtendedFloatingActionButton saveEmoji;
    private ExtendedFloatingActionButton exportEmoji;
    private ImageView mixedEmoji;
    private CircularProgressIndicator progressBar;
    private TextView activityDesc;
    private String emote1;
    private String emote2;
    private String finalEmojiURL;
    private RecyclerView emojisSlider1;
    private RecyclerView emojisSlider2;
    private ArrayList<HashMap<String, Object>> supportedEmojisList = new ArrayList<>();
    private RequestNetwork requestSupportedEmojis;
    private RequestNetwork.RequestListener requestSupportedEmojisListener;
    private SharedPreferences sharedPref;
    private boolean isFineToUseListeners = false;
    private LinearLayoutManager emojisSlider1LayoutManager;
    private LinearLayoutManager emojisSlider2LayoutManager;
    private final SnapHelper emojisSlider1SnapHelper = new LinearSnapHelper();
    private final SnapHelper emojisSlider2SnapHelper = new LinearSnapHelper();

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
        exportEmoji = findViewById(R.id.export);
        emojisSlider1 = findViewById(R.id.emojisSlider1);
        emojisSlider2 = findViewById(R.id.emojisSlider2);
        requestSupportedEmojis = new RequestNetwork(this);
        sharedPref = getSharedPreferences("AppData", Activity.MODE_PRIVATE);

        mixedEmoji.setOnClickListener(view -> {
            isFineToUseListeners = false;
            int random1 = 0;
            int random2 = 0;
            for (int i = 0; i < 2; i++) {
                Random rand = new Random();

                int randomNum = rand.nextInt((supportedEmojisList.size()) - 1);
                if (i == 0) {
                    random1 = randomNum;
                    emojisSlider1.smoothScrollToPosition(randomNum);
                } else {
                    random2 = randomNum;
                    emojisSlider2.smoothScrollToPosition(randomNum);
                }
            }

            emote1 = Objects.requireNonNull(supportedEmojisList.get(random1).get("emojiUnicode")).toString();
            emote2 = Objects.requireNonNull(supportedEmojisList.get(random2).get("emojiUnicode")).toString();
            mixEmojis(emote1, emote2, Objects.requireNonNull(supportedEmojisList.get(random1).get("date")).toString());
        });

        saveEmoji.setOnClickListener(view -> {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                Utils.saveImage(mixedEmoji, MainActivity.this, "\uD83D\uDE22", false);
            } else {
                if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                    Utils.saveImage(mixedEmoji, MainActivity.this, "\uD83D\uDE22", false);
                } else {
                    ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                }
            }

        });

        exportEmoji.setOnClickListener(v ->{
            BottomSheetDialog sheetDialog = new BottomSheetDialog(this);
            View view = getLayoutInflater().inflate(R.layout.layout, null);
            LinearLayout whatsapp = view.findViewById(R.id.whatsapp);
            LinearLayout telegram = view.findViewById(R.id.telegram);
            sheetDialog.setContentView(view);
            sheetDialog.show();
            telegram.setOnClickListener(vw -> {
                Utils.saveImage(mixedEmoji, MainActivity.this, "\uD83D\uDE22", true);
                sheetDialog.dismiss();
            });
            whatsapp.setOnClickListener(vw -> {
                sheetDialog.dismiss();
                Toast.makeText(this, "Coming Soon", Toast.LENGTH_SHORT).show();
            });
        });

        requestSupportedEmojisListener = new RequestNetwork.RequestListener() {
            @Override
            public void onResponse(String tag, String response, HashMap<String, Object> responseHeaders) {
                try {
                    sharedPref.edit().putString("supportedEmojisList", response).apply();
                    addDataToSliders(response);
                } catch (Exception ignored) {
                }
            }

            @Override
            public void onErrorResponse(String tag, String message) {

            }
        };
    }

    private void LOGIC_BACKEND() {
        emojisSlider1LayoutManager = new CenterZoomLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        emojisSlider2LayoutManager = new CenterZoomLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        setSnapHelper(emojisSlider1, emojisSlider1SnapHelper, emojisSlider1LayoutManager);
        setSnapHelper(emojisSlider2, emojisSlider2SnapHelper, emojisSlider2LayoutManager);

        emojisSlider1.setLayoutManager(emojisSlider1LayoutManager);
        emojisSlider2.setLayoutManager(emojisSlider2LayoutManager);

        emojisSlider1.addItemDecoration(new offsetItemDecoration(this));

        if (sharedPref.getString("supportedEmojisList", "").isEmpty()) {
            requestSupportedEmojis.startRequestNetwork(RequestNetworkController.GET, "https://ilyassesalama.github.io/EmojiMixer/emojis/supported_emojis.json", "", requestSupportedEmojisListener);
        } else {
            addDataToSliders(sharedPref.getString("supportedEmojisList", ""));
        }
    }


    private void addDataToSliders(String data) {
        isFineToUseListeners = false;
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
                        int randomNum = rand.nextInt((supportedEmojisList.size()) - 1);
                        if (i == 0) {
                            emojisSlider1.smoothScrollToPosition(randomNum);
                        } else {
                            emojisSlider2.smoothScrollToPosition(randomNum);
                        }
                    }

                    shouldShowEmoji(false);
                    emote1 = Objects.requireNonNull(supportedEmojisList.get(getRecyclerCurrentItem(emojisSlider1, emojisSlider1SnapHelper, emojisSlider1LayoutManager)).get("emojiUnicode")).toString();
                    emote2 = Objects.requireNonNull(supportedEmojisList.get(getRecyclerCurrentItem(emojisSlider2, emojisSlider2SnapHelper, emojisSlider2LayoutManager)).get("emojiUnicode")).toString();
                    mixEmojis(emote1, emote2, Objects.requireNonNull(supportedEmojisList.get(getRecyclerCurrentItem(emojisSlider1, emojisSlider1SnapHelper, emojisSlider1LayoutManager)).get("date")).toString());
                    registerViewPagersListener();
                    isFineToUseListeners = true;
                }, 1000);
            });
        });
    }

    private void registerViewPagersListener() {
        emojisSlider1.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                if (isFineToUseListeners && newState == SCROLL_STATE_IDLE) {
                    emote1 = Objects.requireNonNull(supportedEmojisList.get(getRecyclerCurrentItem(emojisSlider1, emojisSlider1SnapHelper, emojisSlider1LayoutManager)).get("emojiUnicode")).toString();
                    shouldShowEmoji(false);
                    mixEmojis(emote1, emote2, Objects.requireNonNull(supportedEmojisList.get(getRecyclerCurrentItem(emojisSlider1, emojisSlider1SnapHelper, emojisSlider1LayoutManager)).get("date")).toString());
                }
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                int visibleItemCount = emojisSlider1LayoutManager.getChildCount();
                int totalItemCount = emojisSlider1LayoutManager.getItemCount();
                int firstVisibleItemPosition = emojisSlider1LayoutManager.findFirstVisibleItemPosition();

                if (firstVisibleItemPosition <= 0) {
                    ((EmojisSliderAdapter) Objects.requireNonNull(recyclerView.getAdapter())).addItems(0);
                } else if (visibleItemCount + firstVisibleItemPosition >= totalItemCount) {
                    ((EmojisSliderAdapter) Objects.requireNonNull(recyclerView.getAdapter())).addItems(recyclerView.getAdapter().getItemCount());
                }
            }
        });

        emojisSlider2.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                if (isFineToUseListeners && newState == SCROLL_STATE_IDLE) {
                    emote2 = Objects.requireNonNull(supportedEmojisList.get(getRecyclerCurrentItem(emojisSlider2, emojisSlider2SnapHelper, emojisSlider2LayoutManager)).get("emojiUnicode")).toString();
                    shouldShowEmoji(false);
                    mixEmojis(emote1, emote2, Objects.requireNonNull(supportedEmojisList.get(getRecyclerCurrentItem(emojisSlider2, emojisSlider2SnapHelper, emojisSlider2LayoutManager)).get("date")).toString());
                }
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                int visibleItemCount = emojisSlider1LayoutManager.getChildCount();
                int totalItemCount = emojisSlider1LayoutManager.getItemCount();
                int firstVisibleItemPosition = emojisSlider1LayoutManager.findFirstVisibleItemPosition();

                if (firstVisibleItemPosition <= 0) {
                    ((EmojisSliderAdapter) Objects.requireNonNull(recyclerView.getAdapter())).addItems(0);
                } else if (visibleItemCount + firstVisibleItemPosition >= totalItemCount) {
                    ((EmojisSliderAdapter) Objects.requireNonNull(recyclerView.getAdapter())).addItems(recyclerView.getAdapter().getItemCount());
                }
            }
        });
    }


    private void mixEmojis(String emoji1, String emoji2, String date) {
        shouldEnableSave(false);
        progressBar.setVisibility(View.VISIBLE);

        EmojiMixer em = new EmojiMixer(emoji1, emoji2, date, this, new EmojiMixer.EmojiListener() {
            @Override
            public void onSuccess(String emojiUrl) {
                finalEmojiURL = emojiUrl;
                shouldEnableSave(true);
                setImageFromUrl(mixedEmoji, emojiUrl);
            }

            @Override
            public void onFailure(String failureReason) {
                changeActivityDesc(failureReason);
                shouldEnableSave(false);
                mixedEmoji.setImageResource(R.drawable.sad);
                shouldShowEmoji(true);
            }
        });
        Thread thread = new Thread(em);
        thread.start();
    }


    private void shouldShowEmoji(boolean shouldShow) {
        isFineToUseListeners = true;
        if (shouldShow) {
            shadAnim(mixedEmoji, "scaleY", 1, 300);
            shadAnim(mixedEmoji, "scaleX", 1, 300);
            shadAnim(progressBar, "scaleY", 0, 300);
            shadAnim(progressBar, "scaleX", 0, 300);
        } else {
            shadAnim(mixedEmoji, "scaleY", 0, 300);
            shadAnim(mixedEmoji, "scaleX", 0, 300);
            shadAnim(progressBar, "scaleY", 1, 300);
            shadAnim(progressBar, "scaleX", 1, 300);
        }
    }

    private void shouldEnableSave(boolean shouldShow) {

        if (shouldShow) {
            new Handler().postDelayed(() -> {
                saveEmoji.setEnabled(true);
                //colorAnimator(saveEmoji, "#2A2B28", "#FF9D05", 250);
                //saveEmoji.setTextColor(Color.parseColor("#422B0D"));
                //saveEmoji.setIconTint(ColorStateList.valueOf(Color.parseColor("#422B0D")));
            }, 1000);
        } else {
            saveEmoji.setEnabled(false);
            //colorAnimator(saveEmoji, "#FF9D05", "#2A2B28", 250);
            //saveEmoji.setTextColor(Color.parseColor("#A3A3A3"));
            //saveEmoji.setIconTint(ColorStateList.valueOf(Color.parseColor("#A3A3A3")));
        }
    }

    private void setImageFromUrl(ImageView image, String url) {
        Glide.with(this)
                .load(url)
                .fitCenter()
                .transition(DrawableTransitionOptions.withCrossFade())
                .listener(
                        new RequestListener<Drawable>() {
                            @Override
                            public boolean onLoadFailed(GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                                shouldShowEmoji(true);
                                return false;
                            }

                            @Override
                            public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                                shouldShowEmoji(true);
                                return false;
                            }
                        }
                )
                .into(image);
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
                    activityDesc.setText(R.string.activity_hint_1);
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
        executor.execute(() -> {
            try {
                DownloadManager downloadmanager = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
                DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));
                request.setMimeType(getContentResolver().getType(Uri.parse(url)));
                request.addRequestHeader("cookie", CookieManager.getInstance().getCookie(url));
                request.setTitle(fileName);
                request.setDescription(getString(R.string.downloading));
                request.allowScanningByMediaScanner();
                request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
                request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, "/MixedEmojis/" + fileName + URLUtil.guessFileName(url, "", ""));
                downloadmanager.enqueue(request);
            } catch (Exception e) {
                Log.e("Download error", e.toString());
            }
        });
    }
}

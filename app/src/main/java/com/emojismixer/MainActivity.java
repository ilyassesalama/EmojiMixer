package com.emojismixer;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.google.android.material.button.MaterialButton;

public class MainActivity extends AppCompatActivity {

    private MaterialButton mixEmojis;
    private EditText input_emoji1;
    private EditText input_emoji2;
    private TextView tv_unicode1;
    private TextView tv_unicode2;
    private ImageView mixedEmoji;
    private ImageView loading;
    private String API = "https://www.gstatic.com/android/keyboard/emojikitchen/";
    private final int maxDatesCount = 2;
    private String unicode1 = "";
    private String unicode2 = "";
    private int iteratorCount = 0;
    private boolean callingForReverse = false;
    public static final String LOG_API = "API_LOG";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initLogic();
    }

    public void initLogic() {
        mixEmojis = findViewById(R.id.mixEmojis);
        loading = findViewById(R.id.loading);
        input_emoji1 = findViewById(R.id.input_emoji1);
        input_emoji2 = findViewById(R.id.input_emoji2);
        tv_unicode1 = findViewById(R.id.unicode1);
        tv_unicode2 = findViewById(R.id.unicode2);
        mixedEmoji = findViewById(R.id.mixedEmoji);

        mixEmojis.setOnClickListener(view -> {
            unicode1 = convertEmojisToUnicode(input_emoji1.getText().toString());
            unicode2 = convertEmojisToUnicode(input_emoji2.getText().toString());
            tv_unicode1.setText(unicode1);
            tv_unicode2.setText(unicode2);
            callingForReverse = false;
            iteratorCount = 0;
            mixEmojis(true, unicode1, unicode2);
        });
    }

    private void mixEmojis(boolean isFirstResource, String emoji1, String emoji2) {
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
        Log.d(LOG_API, "Mixed emoji API: " + EmojiURL);
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
                .error(R.drawable.sad)
                .listener(new RequestListener<Drawable>() {
                              @Override
                              public boolean onLoadFailed(GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                                  if (maxDatesCount == iteratorCount) {

                                      if (callingForReverse) {
                                          Toast.makeText(MainActivity.this, "No combinations found :(", Toast.LENGTH_SHORT).show();

                                      } else {
                                          Log.d(LOG_API, "Couldn't find an appropriate emoji, but wait we're not done yet.\nReversing emojis order and trying again...");
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
                                  loading.setVisibility(View.INVISIBLE);
                                  return false;
                              }
                          }
                )
                .into(image);
    }

}

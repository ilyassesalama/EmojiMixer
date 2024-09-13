package com.emojimixer.activities;

import static com.emojimixer.Config.SUPPORTED_EMOJIS_URL;
import static com.emojimixer.utils.Utils.saveImageToDevice;
import static com.emojimixer.utils.Utils.setImageFromUrl;

import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.emojimixer.R;
import com.emojimixer.classes.Network;
import com.emojimixer.classes.emojimixer.EmojiMixer;
import com.emojimixer.components.EmojiSlider;
import com.emojimixer.databinding.ActivityMainBinding;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;

    private EmojiSlider emojiSlider1;
    private EmojiSlider emojiSlider2;

    private String emote1;
    private String date1;

    private String emote2;
    private String date2;

    private String loadedEmojiUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        initViews();
        initData();
    }

    private void initViews() {
        emojiSlider1 = new EmojiSlider(binding.emojisSlider1, this);
        emojiSlider2 = new EmojiSlider(binding.emojisSlider2, this);

        emojiSlider1.setListener((emojiUnicode, date) -> {
            setStatusTexts(getString(R.string.app_name), getString(R.string.main_swipe_to_mix));
            emote1 = emojiUnicode;
            date1 = date;
            mixEmojis();
        });

        emojiSlider2.setListener((emojiUnicode, date) -> {
            setStatusTexts(getString(R.string.app_name), getString(R.string.main_swipe_to_mix));
            emote2 = emojiUnicode;
            date2 = date;
            mixEmojis();
        });

        binding.saveEmoji.setOnClickListener(v -> {
            saveImageToDevice(loadedEmojiUrl);
            setStatusTexts(getString(R.string.msg_downloading_success), getString(R.string.main_swipe_to_mix));
        });
    }

    private void initData() {
        Network network = new Network(this);

        network.sendHTTPRequest(SUPPORTED_EMOJIS_URL, new Network.RequestListener() {
            @Override
            public void onSuccess(String response) {
                addDataToSliders(response);
            }

            @Override
            public void onFailure(String error) {
                setStatusTexts(getString(R.string.msg_no_internet), getString(R.string.msg_no_internet_subtitle));
            }
        });
    }

    private void addDataToSliders(String data) {
        ArrayList<HashMap<String, Object>> emojisList = new Gson().fromJson(data,
                new TypeToken<ArrayList<HashMap<String, Object>>>() {
                }.getType());

        emojiSlider1.setData(emojisList);

        ArrayList<HashMap<String, Object>> emojisList2 = new ArrayList<>(emojisList);

        Collections.reverse(emojisList2);
        emojiSlider2.setData(emojisList2);

        emojiSlider1.scrollToRandomPosition();
        emojiSlider2.scrollToRandomPosition();
    }

    private void mixEmojis() {
        if (emote1 == null || emote2 == null) return;

        binding.progressBar.setVisibility(View.VISIBLE);
        EmojiMixer em = new EmojiMixer(this);

        // Use the latest date among the two
        String date = date1.compareTo(date2) > 0 ? date1 : date2;

        em.mixEmojis(emote1, emote2, date, new EmojiMixer.EmojiListener() {
            @Override
            public void onSuccess(String emojiUrl) {
                loadedEmojiUrl = emojiUrl;
                setImageFromUrl(binding.mixedEmoji, emojiUrl, isLoaded -> binding.progressBar.setVisibility(View.GONE));
            }

            @Override
            public void onFailure(int failureReason) {
                binding.progressBar.setVisibility(View.GONE);
                binding.mixedEmoji.setImageResource(R.drawable.sad);

                if (failureReason == EmojiMixer.NO_INTERNET) {
                    setStatusTexts(getString(R.string.msg_no_internet), getString(R.string.msg_no_internet_subtitle));
                } else if (failureReason == EmojiMixer.NO_EMOJI_FOUND) {
                    setStatusTexts(getString(R.string.msg_no_emoji_combination), getString(R.string.msg_no_emoji_combination_subtitle));
                }
            }
        });
    }

    private void setStatusTexts(String title, String subtitle) {
        if (!binding.statusTitle.getText().equals(title)) {
            binding.statusTitle.animate().alpha(0f).setDuration(300).withEndAction(() -> {
                binding.statusTitle.setText(title);
                binding.statusTitle.animate().alpha(1f).setDuration(300).start();
            }).start();
        }
        if (!binding.statusSubtitle.getText().equals(subtitle)) {
            binding.statusSubtitle.animate().alpha(0f).setDuration(300).withEndAction(() -> {
                binding.statusSubtitle.setText(subtitle);
                binding.statusSubtitle.animate().alpha(1f).setDuration(300).start();
            }).start();
        }
    }
}

package com.emojimixer.classes.emojimixer;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import java.net.HttpURLConnection;
import java.net.URL;

public class EmojiMixer implements Runnable {
    // constants for failure reasons
    public static final int NO_INTERNET = 0;
    public static final int NO_EMOJI_FOUND = 1;

    private String emoji_1;
    private String emoji_2;
    private String creation_date;
    private final Activity context;
    private final String LOG = "EMOJI_LOGS";
    public String API = "https://www.gstatic.com/android/keyboard/emojikitchen/";
    public EmojiListener listener;
    private String finalURL;
    private int failure_reason;
    private boolean isTaskSuccessful = false;
    private boolean shouldAbortTask = false;

    public EmojiMixer(Activity context) {
        this.context = context;
    }

    public void mixEmojis(String emoji1, String emoji2, String date, EmojiListener emojiListener) {
        this.emoji_1 = emoji1;
        this.emoji_2 = emoji2;
        this.creation_date = date;
        this.listener = emojiListener;

        Thread thread = new Thread(this);
        thread.start();
    }

    @Override
    public void run() {
        Log.d(LOG, "Emojis checker started with the following data:\nEmojis 1: " + emoji_1 + "\nEmoji 2: " + emoji_2 + "\nDate: " + creation_date);
        if (isConnected()) {
            checkIfImageEmojiInServer(emoji_1, emoji_2, creation_date);
        }
        context.runOnUiThread(() -> {
            if (isTaskSuccessful) {
                if (listener != null) {
                    listener.onSuccess(finalURL);
                }
            } else {
                if (listener != null)
                    listener.onFailure(failure_reason);
            }
        });
    }


    public void checkIfImageEmojiInServer(String emoji1, String emoji2, String date) {
        if (!shouldAbortTask) {
            String Combination = "/" + emoji1 + "/" + emoji1 + "_" + emoji2 + ".png";
            finalURL = API + date + Combination;
            Log.d(LOG, "Checking url: " + finalURL);
            if (checkImage(finalURL)) {
                isTaskSuccessful = true;
                Log.d(LOG, "Found a combination at:  " + finalURL);
            } else {
                Log.d(LOG, "Couldn't find a combination in the regular order, swap emojis then recheck...");
                checkReversedEmojis(emoji2, emoji1, date);
            }
        }
    }

    public void checkReversedEmojis(String emoji1, String emoji2, String date) {
        if (!shouldAbortTask) {
            String Combination = "/" + emoji1 + "/" + emoji1 + "_" + emoji2 + ".png";
            finalURL = API + date + Combination;
            Log.d(LOG, "Checking reversed url: " + finalURL);
            if (checkImage(finalURL)) {
                isTaskSuccessful = true;
                Log.d(LOG, "Found a combination at:  " + finalURL);
            } else {
                Log.d(LOG, "Couldn't find a combination in the reversed order, task failed.");
                failure_reason = NO_EMOJI_FOUND;
                isTaskSuccessful = false;
            }
        }
    }

    public boolean checkImage(String url) {
        if (isConnected()) {
            try {
                HttpURLConnection.setFollowRedirects(false);
                HttpURLConnection con = (HttpURLConnection) new URL(url).openConnection();
                con.setRequestMethod("HEAD");
                return con.getResponseCode() == HttpURLConnection.HTTP_OK;
            } catch (Exception e) {
                return false;
            }
        } else {
            return false;
        }
    }

    public boolean isConnected() {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        if (activeNetwork != null) {
            return true;
        } else {
            Log.d(LOG, "Device is not connected.");
            failure_reason = NO_INTERNET;
            isTaskSuccessful = false;
            shouldAbortTask = true;
        }
        return false;
    }

    public void setListener(EmojiListener listener) {
        this.listener = listener;
    }


    public interface EmojiListener {
        void onSuccess(String emojiUrl);

        void onFailure(int failureReason);
    }
}

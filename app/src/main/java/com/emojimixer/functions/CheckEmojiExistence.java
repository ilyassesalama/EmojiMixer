package com.emojimixer.functions;

import static com.emojimixer.functions.Utils.convertEmojisToUnicode;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class CheckEmojiExistence implements Runnable {

    private final String emoji_1;
    private final String emoji_2;
    private final ArrayList<String> dates;
    private final Activity mContext;
    private final String LOG = "EMOJI_LOGS";
    public int currentPosition = 0;
    public String API = "https://www.gstatic.com/android/keyboard/emojikitchen/";
    public EmojiListener listener;
    private String finalURL;
    private String failure_reason;
    private boolean isTaskSuccessful = false;
    private boolean shouldAbortTask = false;

    /**
     * This method is now deprecated because I made a better and faster way to get emojis. I didn't delete this one because I might use it later.
     * Use {@link #EmojiMixer} class instead.
     */
    @Deprecated
    //
    public CheckEmojiExistence(String emoji1, String emoji2, Activity context, EmojiListener emojiListener) {
        this.listener = emojiListener;
        mContext = context;
        emoji_1 = convertEmojisToUnicode(emoji1);
        emoji_2 = convertEmojisToUnicode(emoji2);
        dates = new ArrayList<>();
        dates.add("20201001");
        dates.add("20210521");
        dates.add("20210218");
        dates.add("20210831");
    }


    @Override
    public void run() {
        Log.d(LOG, "Emojis checker started.");
        if (isConnected()) {
            checkIfImageEmojiInServer(emoji_1, emoji_2, dates);
        }
        mContext.runOnUiThread(() -> {
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


    public void checkIfImageEmojiInServer(String emoji1, String emoji2, ArrayList<String> dates) {
        if (!shouldAbortTask) {
            String Combination = "/" + emoji1 + "/" + emoji1 + "_" + emoji2 + ".png";
            finalURL = API + dates.get(currentPosition) + Combination;
            if (checkImage(finalURL)) {
                isTaskSuccessful = true;
                Log.d(LOG, "Found a combination at:  " + finalURL);
            } else if (currentPosition != dates.toArray().length - 1) {
                Log.d(LOG, "Couldn't find a combination in the regular order, skipping to another date...");
                currentPosition++;
                checkIfImageEmojiInServer(emoji1, emoji2, dates);
            } else {
                Log.d(LOG, "Couldn't find a combination in the regular order, swap emojis then recheck...");
                currentPosition = 0;
                checkReversedEmojis(emoji2, emoji1, dates);
            }
        }
    }

    public void checkReversedEmojis(String emoji1, String emoji2, ArrayList<String> dates) {
        if (!shouldAbortTask) {
            String Combination = "/" + emoji1 + "/" + emoji1 + "_" + emoji2 + ".png";
            finalURL = API + dates.get(currentPosition) + Combination;
            if (checkImage(finalURL)) {
                isTaskSuccessful = true;
                Log.d(LOG, "Found a combination at:  " + finalURL);
            } else if (currentPosition != dates.toArray().length - 1) {
                Log.d(LOG, "Couldn't find a combination in reverse order, skipping to another date...");
                currentPosition++;
                checkReversedEmojis(emoji1, emoji2, dates);
            } else {
                Log.d(LOG, "Couldn't find a combination in the reversed order, task failed.");
                failure_reason = "No combination found for selected emojis.";
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
        ConnectivityManager cm = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        if (activeNetwork != null) {
            return true;
        } else {
            Log.d(LOG, "Device is not connected.");
            failure_reason = "Your device is not connected to the internet.";
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

        void onFailure(String failureReason);
    }
}

package com.emojimixer.classes;

import static com.emojimixer.utils.Utils.runOnUiThread;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class Network {
    private static final String TAG_NETWORK = "EMOJIMIX_NETWORK";
    private final Context context;

    public Network(Context context) {
        this.context = context;
    }

    public interface RequestListener {
        void onSuccess(String response);
        void onFailure(String error);
    }

    public void sendHTTPRequest(String url, RequestListener basicRequestListener) {
        Log.d(TAG_NETWORK, "Sending request to URL: " + url);
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());
        executor.execute(() -> {
            try {
                OkHttpClient client = new OkHttpClient.Builder()
                        .connectTimeout(15, TimeUnit.SECONDS)
                        .writeTimeout(15, TimeUnit.SECONDS)
                        .readTimeout(15, TimeUnit.SECONDS)
                        .build();
                Request request = new Request.Builder().url(url).build();
                Response response = client.newCall(request).execute();
                if (response.isSuccessful()) {
                    var responseStr = Objects.requireNonNull(response.body()).string().trim();
                    runOnUiThread(() -> basicRequestListener.onSuccess(responseStr));
                    Log.d(TAG_NETWORK, "Server responded OK");
                } else {
                    runOnUiThread(() -> basicRequestListener.onFailure(response.message()));
                }
            } catch (Exception e) {
                Log.e(TAG_NETWORK, "Failed to get send request: " + e);
                e.printStackTrace();
                runOnUiThread(() -> basicRequestListener.onFailure(e.toString()));
            }
            handler.post(() -> {
            });
        });
    }

}

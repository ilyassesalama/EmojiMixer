package com.emojimixer.functions;

import android.app.Activity;

import java.util.HashMap;

public class RequestNetwork {
    private final HashMap<String, Object> params = new HashMap<>();
    private HashMap<String, Object> headers = new HashMap<>();
    private int requestType = 0;

    public RequestNetwork() {
    }

    public HashMap<String, Object> getParams() {
        return params;
    }

    public HashMap<String, Object> getHeaders() {
        return headers;
    }

    public void setHeaders(HashMap<String, Object> headers) {
        this.headers = headers;
    }

    public int getRequestType() {
        return requestType;
    }

    public void startRequestNetwork(
            String method,
            String url,
            String tag,
            RequestListener requestListener
    ) {
        RequestNetworkController.getInstance().execute(this, method, url, tag, requestListener);
    }

    public interface RequestListener {
        void onResponse(String tag, String response, HashMap<String, Object> responseHeaders);
        void onErrorResponse(String tag, String message);
    }
}

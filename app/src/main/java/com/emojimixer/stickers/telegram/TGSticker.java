package com.emojimixer.stickers.telegram;

import android.net.Uri;

public class TGSticker {

    private final Uri uri;
    private String emoji;

    public TGSticker(Uri uri){
        this.uri = uri;
    }

    public void setEmoji(String emoji) {
        this.emoji = emoji;
    }

    public String getEmoji() {
        return emoji;
    }

    public Uri getUri() {
        return uri;
    }
}

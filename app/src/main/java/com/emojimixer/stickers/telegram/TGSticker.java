package com.emojimixer.stickers.telegram;

import android.net.Uri;

import java.io.File;

public class TGSticker {

    private String path;
    private Uri uri;
    private File file;
    private String emoji;

    public TGSticker(String path){
        this.path = path;
    }

    public TGSticker(Uri uri){
        this.uri = uri;
    }

    public TGSticker(File file){
        this.file = file;
    }

    public void setEmoji(String emoji) {
        this.emoji = emoji;
    }

    public String getEmoji() {
        return emoji;
    }

    public File getFile() {
        return file;
    }

    public String getPath() {
        return path;
    }

    public Uri getUri() {
        return uri;
    }
}

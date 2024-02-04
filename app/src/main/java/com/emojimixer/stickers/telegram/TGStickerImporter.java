package com.emojimixer.stickers.telegram;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.widget.Toast;

import java.util.ArrayList;

public class TGStickerImporter {

    private static final String CREATE_STICKER_PACK_ACTION = "org.telegram.messenger.CREATE_STICKER_PACK";
    private static final String CREATE_STICKER_PACK_EMOJIS_EXTRA = "STICKER_EMOJIS";
    private static final String CREATE_STICKER_PACK_IMPORTER_EXTRA = "IMPORTER";

    private final ArrayList<TGSticker> stickers;
    private final Context context;

    public TGStickerImporter(Context context, ArrayList<TGSticker> stickers){
        this.stickers = stickers;
        this.context = context;
    }

    private ArrayList<Uri> getUris(){
        ArrayList<Uri> uris = new ArrayList<>();
        for(TGSticker uri : stickers){
            uris.add(uri.getUri());
        }
        return uris;
    }

    private ArrayList<String> getEmojis(){
        ArrayList<String> uris = new ArrayList<>();
        for(TGSticker uri : stickers){
            uris.add(uri.getEmoji());
        }
        return uris;
    }

    public void importStickers(){
        Intent intent = new Intent(CREATE_STICKER_PACK_ACTION);
        intent.putExtra(Intent.EXTRA_STREAM, getUris());
        intent.putExtra(CREATE_STICKER_PACK_IMPORTER_EXTRA, context.getPackageName());
        intent.putExtra(CREATE_STICKER_PACK_EMOJIS_EXTRA, getEmojis());
        intent.setType("image/*");

        // Try to invoke the intent.
        try {
            context.startActivity(intent);
        } catch (ActivityNotFoundException e) {
            Toast.makeText(context, "Telegram is not installed", Toast.LENGTH_SHORT).show();
        }
    }

}

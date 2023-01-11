package com.emojimixer.functions;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.TransitionDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.recyclerview.widget.LinearSnapHelper;
import androidx.recyclerview.widget.PagerSnapHelper;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SnapHelper;

import com.bumptech.glide.Glide;
import com.emojimixer.R;
import com.emojimixer.stickers.telegram.TGSticker;
import com.emojimixer.stickers.telegram.TGStickerImporter;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;

public class Utils {

    public static String convertEmojisToUnicode(String emoji) {
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
    public static void setSnapHelper(RecyclerView recyclerView, SnapHelper snapHelper, RecyclerView.LayoutManager layoutManager) {
        snapHelper = new LinearSnapHelper();
        recyclerView.setLayoutManager(layoutManager);
        snapHelper.attachToRecyclerView(recyclerView);
    }

    public static int getRecyclerCurrentItem(RecyclerView recyclerView, SnapHelper snapHelper, RecyclerView.LayoutManager layoutManager) {
        View view = snapHelper.findSnapView(layoutManager);
        if (view != null) {
            return recyclerView.getChildAdapterPosition(view);
        } else {
            return 0;
        }
    }

    public static void saveImage(ImageView imageView, Context context, String unicodeRep, boolean exportTelegram) {
        String fileName = System.currentTimeMillis() + ".png";

        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.DISPLAY_NAME, fileName);
        values.put(MediaStore.Images.Media.MIME_TYPE, "image/png");


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            values.put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DCIM);
        } else {
            File directory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM);
            File file = new File(directory, fileName);
            values.put(MediaStore.MediaColumns.DATA, file.getAbsolutePath());
        }
        Uri uri = context.getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);

        int width = 512;
        int height = 512;

        float aspectRatio = imageView.getWidth() / (float) imageView.getHeight();

        if (imageView.getWidth() > width || imageView.getHeight() > height) {
            if (aspectRatio > 1) {
                width = (int) (height * aspectRatio);
            } else {
                height = (int) (width / aspectRatio);
            }
        }

        try (OutputStream output = context.getContentResolver().openOutputStream(uri)) {
            Drawable drawable = imageView.getDrawable();
            Bitmap bitmap = drawableToBitmap(drawable);
            Bitmap bm = Bitmap.createScaledBitmap(bitmap, width, height, false);
            bm.compress(Bitmap.CompressFormat.PNG, 100, output);
            output.close();

            if(exportTelegram){
                TGSticker tgSticker = new TGSticker(uri);
                tgSticker.setEmoji(unicodeRep);
                ArrayList<TGSticker> stickers = new ArrayList<>();
                TGStickerImporter importer = new TGStickerImporter(context,stickers);
                stickers.add(tgSticker);
                importer.importStickers();
                Toast.makeText(context, R.string.exporting, Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(context, R.string.saved, Toast.LENGTH_SHORT).show();
            }
        } catch (IOException e) {
            Toast.makeText(context, R.string.failed_to_save, Toast.LENGTH_SHORT).show();
        }
    }

    public static Bitmap drawableToBitmap (Drawable drawable) {
        Bitmap bitmap;

        if (drawable instanceof BitmapDrawable) {
            BitmapDrawable bitmapDrawable = (BitmapDrawable) drawable;
            if(bitmapDrawable.getBitmap() != null) {
                return bitmapDrawable.getBitmap();
            }
        }

        if(drawable.getIntrinsicWidth() <= 0 || drawable.getIntrinsicHeight() <= 0) {
            bitmap = Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888); // Single color bitmap will be created of 1x1 pixel
        } else {
            bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        }

        Canvas canvas = new Canvas(bitmap);
        canvas.drawColor(Color.TRANSPARENT);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);
        return bitmap;
    }

}

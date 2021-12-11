package com.emojismixer.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.emojismixer.R;

import java.util.ArrayList;
import java.util.HashMap;


public class EmojisSliderAdapter extends RecyclerView.Adapter<EmojisSliderAdapter.ViewHolder> {
    private final Context mContext;
    private final SharedPreferences sharedPref;
    ArrayList<HashMap<String, Object>> data;

    public EmojisSliderAdapter(ArrayList<HashMap<String, Object>> _arr, Context context) {
        mContext = context;
        data = _arr;
        sharedPref = context.getSharedPreferences("AppData", Activity.MODE_PRIVATE);

    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.emojis_slider_item, parent, false));
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        View view = holder.itemView;

        double hex = (double) data.get(position).get("emojiHexCode");

        String unicode = Integer.toHexString((int) hex);

        String emojiURL = "https://ilyassesalama.github.io/EmojisMixer/emojis/supported_emojis_png/" + unicode + ".png";

        loadEmojiFromUrl(holder.emoji, holder.progressBar, emojiURL);


        holder.emoji.setOnClickListener(v -> {
//            if (data.get(position + 1).containsKey("emojiHexCode")) {
//
//            }
        });

        RecyclerView.LayoutParams _lp = new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        view.setLayoutParams(_lp);

    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView emoji;
        ImageView progressBar;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            emoji = itemView.findViewById(R.id.emoji);
            progressBar = itemView.findViewById(R.id.progressBar);
        }
    }

    private void loadEmojiFromUrl(ImageView image, ImageView progressBar, String url) {
        Glide.with(mContext)
                .load(url)
                .fitCenter()
                .transition(DrawableTransitionOptions.withCrossFade())
                .listener(new RequestListener<Drawable>() {
                              @Override
                              public boolean onLoadFailed(GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {

                                  return false;
                              }

                              @Override
                              public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                                  progressBar.setVisibility(View.GONE);
                                  return false;
                              }
                          }
                )
                .into(image);
    }
}

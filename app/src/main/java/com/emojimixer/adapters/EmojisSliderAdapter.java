package com.emojimixer.adapters;

import android.app.Activity;
import android.content.Context;
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
import com.emojimixer.R;
import com.google.android.material.progressindicator.CircularProgressIndicator;

import java.util.ArrayList;
import java.util.HashMap;

public class EmojisSliderAdapter extends RecyclerView.Adapter<EmojisSliderAdapter.ViewHolder> {

    private final ArrayList<HashMap<String, Object>> data;
    private final ArrayList<HashMap<String, Object>> original;
    private final Context mContext;

    public EmojisSliderAdapter(ArrayList<HashMap<String, Object>> data, Context mContext) {
        this.data = data;
        this.mContext = mContext;
        this.original = (ArrayList<HashMap<String, Object>>) data.clone();
        mContext.getSharedPreferences("AppData", Activity.MODE_PRIVATE);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.emojis_slider_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String unicode = data.get(position).get("emojiUnicode").toString();
        String emojiURL = "https://ilyassesalama.github.io/EmojiMixer/emojis/supported_emojis_png/" + unicode + ".png";
        loadEmojiFromUrl(holder.emoji, holder.progressBar, emojiURL);

        RecyclerView.LayoutParams layoutParams = new RecyclerView.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        );
        holder.itemView.setLayoutParams(layoutParams);
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public void addItems(int position) {
        data.addAll(position, original);
        notifyItemRangeInserted(position, original.size());
    }

    private void loadEmojiFromUrl(ImageView image, CircularProgressIndicator progressBar, String url) {
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
                })
                .into(image);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public ImageView emoji;
        public CircularProgressIndicator progressBar;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            emoji = itemView.findViewById(R.id.emoji);
            progressBar = itemView.findViewById(R.id.progressBar);
        }
    }
}

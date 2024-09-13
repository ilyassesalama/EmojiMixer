package com.emojimixer.adapters;

import static com.emojimixer.utils.Utils.setImageFromUrl;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.emojimixer.databinding.ViewEmojisSliderItemBinding;

import java.util.ArrayList;
import java.util.HashMap;

public class EmojisSliderAdapter extends RecyclerView.Adapter<EmojisSliderAdapter.ViewHolder> {

    private final ArrayList<HashMap<String, Object>> data;
    private final Context mContext;

    public EmojisSliderAdapter(ArrayList<HashMap<String, Object>> data, Context mContext) {
        this.data = data;
        this.mContext = mContext;
    }

    @NonNull
    @Override
    public EmojisSliderAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        ViewEmojisSliderItemBinding binding = ViewEmojisSliderItemBinding.inflate(inflater, parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull EmojisSliderAdapter.ViewHolder holder, int position) {
        String unicode = data.get(position).get("emojiUnicode").toString();
        String emojiURL = "https://ilyassesalama.github.io/EmojiMixer/emojis/supported_emojis_png/" + unicode + ".png";

        setImageFromUrl(holder.binding.emoji, emojiURL, isLoaded -> holder.binding.progressBar.setVisibility(View.GONE));
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public final ViewEmojisSliderItemBinding binding;

        public ViewHolder(@NonNull ViewEmojisSliderItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}

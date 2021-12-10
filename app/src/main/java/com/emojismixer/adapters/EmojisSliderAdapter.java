package com.emojismixer.adapters;

import static com.emojismixer.functions.Utils.setSVGFromUrl;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

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

        String emojiURL = "https://ilyassesalama.github.io/EmojisMixer/emojis/supported_emojis/" + unicode + ".svg";

        setSVGFromUrl(holder.emoji, emojiURL, (Activity) mContext);

        RecyclerView.LayoutParams _lp = new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        view.setLayoutParams(_lp);

    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView emoji;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            emoji = itemView.findViewById(R.id.emoji);
        }
    }
}

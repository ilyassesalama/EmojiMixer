package com.emojimixer.adapters

import android.app.Activity
import android.content.Context
import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.emojimixer.R
import com.google.android.material.progressindicator.CircularProgressIndicator

class EmojisSliderAdapter(
    private val data: ArrayList<HashMap<String, Any>>,
    private val mContext: Context
) : RecyclerView.Adapter<EmojisSliderAdapter.ViewHolder>() {
    private val original = data.clone() as ArrayList<HashMap<String, Any>>
    init {
        mContext.getSharedPreferences("AppData", Activity.MODE_PRIVATE)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.emojis_slider_item, parent, false)
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val view = holder.itemView
        val unicode = data[position]["emojiUnicode"]!!.toString()
        val emojiURL =
            "https://ilyassesalama.github.io/EmojiMixer/emojis/supported_emojis_png/$unicode.png"
        loadEmojiFromUrl(holder.emoji, holder.progressBar, emojiURL)
        val layoutParams = RecyclerView.LayoutParams(
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        view.layoutParams = layoutParams
    }

    override fun getItemCount(): Int {
        return data.size
    }

    fun addItems(position: Int) {
        data.addAll(position, original)
        notifyItemRangeInserted(position, original.size)
    }

    private fun loadEmojiFromUrl(
        image: ImageView,
        progressBar: CircularProgressIndicator,
        url: String
    ) {
        Glide.with(mContext)
            .load(url)
            .fitCenter()
            .transition(DrawableTransitionOptions.withCrossFade())
            .listener(
                object : RequestListener<Drawable?> {
                    override fun onLoadFailed(
                        e: GlideException?,
                        model: Any,
                        target: Target<Drawable?>,
                        isFirstResource: Boolean
                    ): Boolean {
                        return false
                    }

                    override fun onResourceReady(
                        resource: Drawable?,
                        model: Any,
                        target: Target<Drawable?>,
                        dataSource: DataSource,
                        isFirstResource: Boolean
                    ): Boolean {
                        progressBar.visibility = View.GONE
                        return false
                    }
                }
            )
            .into(image)
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var emoji: ImageView
        var progressBar: CircularProgressIndicator

        init {
            emoji = itemView.findViewById(R.id.emoji)
            progressBar = itemView.findViewById(R.id.progressBar)
        }
    }

}
package se.mobileinteraction.mp3player.utils

import android.graphics.drawable.Drawable
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target

fun ImageView.load(url: String, measuredWidth: Int? = null, measuredHeight: Int? = null, onLoadingFinished: (() -> Unit)? = null) {
    if (measuredWidth != null && measuredHeight == null || measuredWidth == null && measuredHeight != null) error(
        "Both width and height need to be set or none of them"
    )

    val listener = object : RequestListener<Drawable> {
        override fun onLoadFailed(
            e: GlideException?,
            model: Any?,
            target: Target<Drawable>?,
            isFirstResource: Boolean
        ): Boolean {
            onLoadingFinished?.invoke()
            return false
        }

        override fun onResourceReady(
            resource: Drawable?,
            model: Any?,
            target: Target<Drawable>?,
            dataSource: DataSource?,
            isFirstResource: Boolean
        ): Boolean {
            onLoadingFinished?.invoke()
            return false
        }
    }

    Glide.with(this)
        .load(url)
        .also { if (measuredWidth != null && measuredHeight != null) { it.override(measuredWidth, measuredHeight) } }
        .also { if (onLoadingFinished != null) it.listener(listener) }
        .into(this)
}
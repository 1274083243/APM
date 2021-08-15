package com.mobile.apm

import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.widget.ImageView
import com.apm.apm_interface.LargeImageViewTracker

open class CustomImageView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : androidx.appcompat.widget.AppCompatImageView(context, attrs) {
    override fun setImageDrawable(drawable: Drawable?) {
        super.setImageDrawable(drawable)
        LargeImageViewTracker.checkDrawable(drawable,this)
    }

    override fun setImageBitmap(bm: Bitmap?) {
        super.setImageBitmap(bm)
        LargeImageViewTracker.checkBitmap(bm,this)

    }
}
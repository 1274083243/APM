package com.apm.apm_interface

import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.util.Log
import android.view.View

/**
 * 大图检测类
 */
object LargeImageViewTracker {
    @JvmStatic
    fun checkDrawable(drawable:Drawable?,view:View){
        drawable?.let {
            if (drawable.intrinsicWidth > view.width || drawable.intrinsicHeight > view.height) {
                Log.d(
                    "ImageViewChecker", "图片大小和控件不匹配:控件尺寸:" +
                            "${view.width}x${view.height},图片尺寸:" +
                            "${drawable.intrinsicWidth}x${drawable.intrinsicHeight}"
                )
            }
        }

    }
    @JvmStatic
    fun checkBitmap(bitmap: Bitmap?, view:View){
        bitmap?.let {
            if (bitmap.width > view.width || bitmap.height >  view.height) {
                Log.d(
                    "ImageViewChecker", "图片大小和控件不匹配:控件尺寸:" +
                            "${ view.width}x${view.height},图片尺寸:" +
                            "${it.width}x${it.height}"
                )
            }
        }

    }
}
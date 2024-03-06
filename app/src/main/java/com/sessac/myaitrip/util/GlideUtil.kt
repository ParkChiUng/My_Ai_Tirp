package com.sessac.myaitrip.util

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.widget.ImageView
import com.bumptech.glide.Glide

object GlideUtil {
    fun loadProfileImage(context: Context, imgUrl: String, imageView: ImageView) {
        Glide.with(context)
            .load(imgUrl)
            .circleCrop()
            .into(imageView)
    }

    fun loadProfileImage(context: Context, imgUri: Uri, imageView: ImageView) {
        Glide.with(context)
            .load(imgUri)
            .circleCrop()
            .into(imageView)
    }

    fun loadProfileImage(context: Context, bitmap: Bitmap, imageView: ImageView) {
        Glide.with(context)
            .load(bitmap)
            .circleCrop()
            .into(imageView)
    }
}
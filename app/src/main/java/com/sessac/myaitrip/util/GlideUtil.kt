package com.sessac.myaitrip.util

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.sessac.myaitrip.R
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

object GlideUtil {
    fun loadProfileImage(context: Context, imgUrl: String, imageView: ImageView) {
        Glide.with(context)
            .load(imgUrl)
            .circleCrop()
            .placeholder(R.drawable.progress_animation)
            .error(R.drawable.error_image)
            .into(imageView)
    }

    fun loadProfileImage(context: Context, imgUri: Uri, imageView: ImageView) {
        Glide.with(context)
            .load(imgUri)
            .circleCrop()
            .placeholder(R.drawable.progress_animation)
            .error(R.drawable.error_image)
            .into(imageView)
    }

    fun loadProfileImage(context: Context, bitmap: Bitmap, imageView: ImageView) {
        Glide.with(context)
            .load(bitmap)
            .circleCrop()
            .placeholder(R.drawable.progress_animation)
            .error(R.drawable.error_image)
            .into(imageView)
    }

    fun loadImage(context: Context, imgUrl: String, imageView: ImageView) {
        Glide.with(context)
            .load(imgUrl)
            .placeholder(R.drawable.progress_animation)
            .error(R.drawable.error_image)
            .into(imageView)
    }

    fun loadImage(context: Context, imgUrl: Uri, imageView: ImageView) {
        Glide.with(context)
            .load(imgUrl)
            .placeholder(R.drawable.progress_animation)
            .error(R.drawable.error_image)
            .into(imageView)
    }

}
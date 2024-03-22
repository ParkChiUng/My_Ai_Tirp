package com.sessac.myaitrip.presentation.diary.adapter

import android.annotation.SuppressLint
import android.net.Uri
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.sessac.myaitrip.databinding.ItemDiaryImgCardBinding
import com.sessac.myaitrip.util.GlideUtil
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import reactivecircus.flowbinding.android.view.clicks

class ImageCardAdapter(
    private val scope: CoroutineScope
) :
    RecyclerView.Adapter<ImageCardAdapter.ImageCardViewHolder>() {

    private var images: MutableList<String> = mutableListOf()

    class ImageCardViewHolder(val binding: ItemDiaryImgCardBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageCardViewHolder {
        return ImageCardViewHolder(ItemDiaryImgCardBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )
    }

    override fun onBindViewHolder(holder: ImageCardViewHolder, position: Int) {
        with(holder.binding) {

            GlideUtil.loadImage(ivTour.context, images[position], ivTour)

            btnDelete.clicks()
                .onEach {
                    Log.d("test"," test delete button on click")
                }
                .launchIn(scope)
        }
    }

    override fun getItemCount(): Int {
        return images.size
    }

    fun updateImages(newImages: MutableList<String>) {
        images = newImages
        notifyDataSetChanged()
    }
}
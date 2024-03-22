package com.sessac.myaitrip.presentation.tourDetail.adapter

import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.sessac.myaitrip.R
import com.sessac.myaitrip.util.GlideUtil

class ImageSliderAdapter(private val images: List<String>) :
    RecyclerView.Adapter<ImageSliderAdapter.SliderViewHolder>() {

    inner class SliderViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val imageView: ImageView = view.findViewById(R.id.iv_slider)
        val darkView: View = view.findViewById(R.id.view_FadingEdge)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SliderViewHolder {
        return SliderViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.item_tour_img_slider, parent, false)
        )
    }

    override fun onBindViewHolder(holder: SliderViewHolder, position: Int) {
        GlideUtil.loadImage(holder.imageView.context, images[position], holder.imageView)

        val gradient = GradientDrawable(
            GradientDrawable.Orientation.TOP_BOTTOM,
            intArrayOf(Color.TRANSPARENT, Color.BLACK)
        )
        holder.darkView.background = gradient
    }

    override fun getItemCount(): Int {
        return images.size ?: 0
    }
}

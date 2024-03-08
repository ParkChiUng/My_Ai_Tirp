package com.sessac.myaitrip.presentation.home.adapter

import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.sessac.myaitrip.data.entities.TourItem
import com.sessac.myaitrip.databinding.ItemFullTourImgCardBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import reactivecircus.flowbinding.android.view.clicks

class FullCardAdapter(
    val itemOnClick: (TourItem) -> (Unit),
    private val scope: CoroutineScope
) :
    ListAdapter<TourItem, FullCardAdapter.HomeViewHolder>(diffUtil) {

    companion object {
        val diffUtil = object : DiffUtil.ItemCallback<TourItem>() {
            override fun areItemsTheSame(oldItem: TourItem, newItem: TourItem): Boolean {
                return oldItem.contentTypeId == newItem.contentTypeId
            }

            override fun areContentsTheSame(oldItem: TourItem, newItem: TourItem): Boolean {
                return oldItem == newItem
            }
        }
    }

    inner class HomeViewHolder(val binding: ItemFullTourImgCardBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun widgetBinding(tourItem: TourItem) {
            tourItem.let { tour ->
                with(binding) {
                    Glide.with(ivTour.context)
                        .load(tour.firstImage)
                        .into(ivTour)

                    tvTourName.text = tour.title

                    val gradient = GradientDrawable(
                        GradientDrawable.Orientation.TOP_BOTTOM,
                        intArrayOf(Color.TRANSPARENT, Color.BLACK)
                    )
                    layoutText.background = gradient

                    root.clicks()
                        .onEach {
                            itemOnClick(tour)
                        }
                        .launchIn(scope)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HomeViewHolder {
        return HomeViewHolder(
            ItemFullTourImgCardBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: HomeViewHolder, position: Int) {
        holder.widgetBinding(getItem(position))
    }

    fun setTourList(tour: List<TourItem>) {
        submitList(tour)
    }
}
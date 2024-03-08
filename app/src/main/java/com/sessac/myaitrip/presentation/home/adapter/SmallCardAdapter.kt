package com.sessac.myaitrip.presentation.home.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.sessac.myaitrip.data.entities.TourItem
import com.sessac.myaitrip.databinding.ItemSmallTourImgCardBinding
import com.sessac.myaitrip.util.GlideUtil
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import reactivecircus.flowbinding.android.view.clicks

class SmallCardAdapter(
    val itemOnClick: (TourItem) -> (Unit),
    private val scope: CoroutineScope
) :
    ListAdapter<TourItem, SmallCardAdapter.HomeViewHolder>(diffUtil) {

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

    inner class HomeViewHolder(private val binding: ItemSmallTourImgCardBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun widgetBinding(tourItem: TourItem) {
            tourItem.let { tour ->
                with(binding) {
                    tour.firstImage?.let {
                        GlideUtil.loadImage(ivTour.context, tour.firstImage, ivTour)
                    }

                    tvTourName.text = tour.title

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
            ItemSmallTourImgCardBinding.inflate(
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
package com.sessac.myaitrip.presentation.tours.adapter

import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.sessac.myaitrip.data.entities.TourItem
import com.sessac.myaitrip.databinding.ItemTourImgCardBinding
import com.sessac.myaitrip.util.GlideUtil
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import reactivecircus.flowbinding.android.view.clicks

class ToursPagingAdapter(
    val itemOnClick: (TourItem) -> (Unit),
    private val scope: CoroutineScope
) :
    PagingDataAdapter<TourItem, ToursPagingAdapter.TourItemViewHolder>(diffUtil) {

    companion object {
        val diffUtil = object : DiffUtil.ItemCallback<TourItem>() {
            override fun areItemsTheSame(oldItem: TourItem, newItem: TourItem): Boolean {
                return oldItem.contentId == newItem.contentId
            }

            override fun areContentsTheSame(oldItem: TourItem, newItem: TourItem): Boolean {
                return oldItem == newItem
            }
        }
    }

    inner class TourItemViewHolder(val binding: ItemTourImgCardBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun widgetBinding(tourItem: TourItem) {
            tourItem.let { tour ->
                with(binding) {

                    GlideUtil.loadImage(ivTour.context, tour.firstImage!!, ivTour)

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

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TourItemViewHolder {
        return TourItemViewHolder(
            ItemTourImgCardBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: TourItemViewHolder, position: Int) {
        val tourItem = getItem(position)
        tourItem?.let {
            holder.widgetBinding(it)
        }
    }
}
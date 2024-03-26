package com.sessac.myaitrip.presentation.airecommend

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.sessac.myaitrip.data.entities.TourItem
import com.sessac.myaitrip.databinding.ItemAiRecommendTourBinding
import com.sessac.myaitrip.util.GlideUtil
import kotlinx.coroutines.CoroutineScope

class AIRecommendTourAdapter(
    private val onItemClick: (TourItem) -> (Unit),
    private val scope: CoroutineScope
): ListAdapter<TourItem, AIRecommendTourAdapter.AIRecommendTourViewHolder>(diffUtil) {

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

    inner class AIRecommendTourViewHolder(private val binding: ItemAiRecommendTourBinding): RecyclerView.ViewHolder(binding.root) {
        fun bind(tour: TourItem) {
            with(binding) {
                if(tour.firstImage.isNotEmpty()) {
                    GlideUtil.loadImage(ivItemAiTourImg.context, tour.firstImage, ivItemAiTourImg)
                } else if(tour.firstImage2.isNotEmpty()) {
                    GlideUtil.loadImage(ivItemAiTourImg.context, tour.firstImage2, ivItemAiTourImg)
                } else {
                    // 이미지 없음
                }

                tvItemAiTourTitle.text = tour.title

                tvItemAiTourAddress.text = tour.address

                tvItemAiTourType.text= when(tour.contentTypeId) {
                    // 12:관광지, 14:문화시설, 15:축제공연행사, 25:여행코스, 28:레포츠, 32:숙박, 38:쇼핑, 39:음식점)
                    "12" -> "관광지"
                    "14" -> "문화시설"
                    "15" -> "축제•공연•행사"
                    "25" -> "여행코스"
                    "28" -> "레포츠"
                    "32" -> "숙박"
                    "38" -> "쇼핑"
                    else -> "음식점"
                }
            }
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): AIRecommendTourAdapter.AIRecommendTourViewHolder {
        return AIRecommendTourViewHolder(
            ItemAiRecommendTourBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(
        holder: AIRecommendTourAdapter.AIRecommendTourViewHolder,
        position: Int
    ) {
        holder.bind(getItem(position))
    }

    fun setTourList(tourList: List<TourItem>) {
        submitList(tourList)
    }
}
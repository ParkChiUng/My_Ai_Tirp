package com.sessac.myaitrip.presentation.tourmap

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.sessac.myaitrip.data.entities.remote.LocationBasedTourItem
import com.sessac.myaitrip.databinding.ItemTourMapBottomSheetTourBinding
import com.sessac.myaitrip.util.GlideUtil
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import reactivecircus.flowbinding.android.view.clicks

class LocationBottomSheetTourAdapter(
    val itemOnClick: (LocationBasedTourItem) -> (Unit),
    private val scope: CoroutineScope
): ListAdapter<LocationBasedTourItem, LocationBottomSheetTourAdapter.LocationBottomSheetTourViewHolder>(diffUtil) {

    companion object {
        private val diffUtil = object: DiffUtil.ItemCallback<LocationBasedTourItem>() {
            override fun areItemsTheSame(oldItem: LocationBasedTourItem, newItem: LocationBasedTourItem): Boolean {
                return oldItem.contentId == newItem.contentId
            }

            override fun areContentsTheSame(oldItem: LocationBasedTourItem, newItem: LocationBasedTourItem): Boolean {
                return oldItem == newItem
            }
        }
    }

    inner class LocationBottomSheetTourViewHolder(private val binding: ItemTourMapBottomSheetTourBinding): RecyclerView.ViewHolder(binding.root) {
        fun bind(tour: LocationBasedTourItem) {

            with(binding) {
                tvItemTourMapBottomSheetTitle.text = tour.title // 제목
                tvItemTourMapBottomSheetAddress.text = tour.address // 주소
                tvItemTourMapBottomSheetDistance.text = String.format("%.1fkm", tour.distance.toFloat() / 1000 )  // 거리
                tvItemTourMapBottomSheetType.text = when(tour.contentTypeId) {
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

                tour.firstImageUrl.isEmpty()
                GlideUtil.loadImage(ivItemTourMapBottomSheetImg.context, tour.firstImageUrl, ivItemTourMapBottomSheetImg)   // 이미지

                // 아이템 클릭
                root.clicks()
                    .onEach {
                        itemOnClick(tour)
                    }
                    .launchIn(scope)
            }

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LocationBottomSheetTourViewHolder {
        val binding = ItemTourMapBottomSheetTourBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return LocationBottomSheetTourViewHolder(binding)
    }

    override fun onBindViewHolder(holder: LocationBottomSheetTourViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    fun setTourList(tourList: List<LocationBasedTourItem>) {
        submitList(tourList)
    }
}
package com.sessac.myaitrip.presentation.myPage.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.sessac.myaitrip.R
import com.sessac.myaitrip.data.entities.DiaryItem
import com.sessac.myaitrip.databinding.ItemDiaryListBinding
import com.sessac.myaitrip.util.GlideUtil
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import reactivecircus.flowbinding.android.view.clicks

class DiaryAdapter(
    val itemOnClick: (DiaryItem) -> (Unit),
    private val scope: CoroutineScope
) :
    ListAdapter<DiaryItem, DiaryAdapter.HomeViewHolder>(diffUtil) {

    companion object {
        val diffUtil = object : DiffUtil.ItemCallback<DiaryItem>() {
            override fun areItemsTheSame(oldItem: DiaryItem, newItem: DiaryItem): Boolean {
                return oldItem.contentId == newItem.contentId
            }

            override fun areContentsTheSame(oldItem: DiaryItem, newItem: DiaryItem): Boolean {
                return oldItem == newItem
            }
        }
    }

    inner class HomeViewHolder(private val binding: ItemDiaryListBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun widgetBinding(diaryItem: DiaryItem) {
            diaryItem.let { diary ->
                with(binding) {

                    if (diary.diaryImage.isEmpty())
                        ivItemDiaryTourImg.setImageResource(R.drawable.splash_bg)
                    else
                        GlideUtil.loadImage(
                            ivItemDiaryTourImg.context,
                            diary.diaryImage[0],
                            ivItemDiaryTourImg
                        )

                    tvItemDiaryTitle.text = diary.diaryTitle
                    tvItemDiaryTourName.text = diary.tourTitle
                    tvItemDiaryTourAddress.text = diary.tourAddress
                    tvItemDiaryCreateDateTime.text = diary.createDateTime

                    root.clicks()
                        .onEach {
                            itemOnClick(diary)
                        }
                        .launchIn(scope)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HomeViewHolder {
        return HomeViewHolder(
            ItemDiaryListBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: HomeViewHolder, position: Int) {
        holder.widgetBinding(getItem(position))
    }

    fun setDiaryList(tour: List<DiaryItem>) {
        submitList(tour)
    }
}
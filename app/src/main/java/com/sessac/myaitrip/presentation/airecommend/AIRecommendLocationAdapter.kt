package com.sessac.myaitrip.presentation.airecommend

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.sessac.myaitrip.R
import com.sessac.myaitrip.databinding.ItemAreaCardBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import reactivecircus.flowbinding.android.view.clicks

class AIRecommendLocationAdapter(
    private val onItemClick:(String) -> (Unit),
    private val scope: CoroutineScope
): ListAdapter<String, AIRecommendLocationAdapter.AILocationViewHolder>(diffUtil) {

    companion object {
        val diffUtil = object : DiffUtil.ItemCallback<String>() {
            override fun areItemsTheSame(oldItem: String, newItem: String): Boolean {
                return oldItem === newItem
            }

            override fun areContentsTheSame(oldItem: String, newItem: String): Boolean {
                return oldItem == newItem
            }
        }
    }
    inner class AILocationViewHolder(private val binding: ItemAreaCardBinding): RecyclerView.ViewHolder(binding.root) {
        fun bind(area: String) {
            with(binding) {
                tvAreaName.text = area
                when(area) {
                    "서울" -> ivArea.setImageResource(R.drawable.img_seoul)
                    "인천" -> ivArea.setImageResource(R.drawable.img_incheon)
                    "대전" -> ivArea.setImageResource(R.drawable.img_daejeon)
                    "대구" -> ivArea.setImageResource(R.drawable.img_daegu)
                    "광주" -> ivArea.setImageResource(R.drawable.img_kwangju)
                    "부산" -> ivArea.setImageResource(R.drawable.img_busan)
                    "울산" -> ivArea.setImageResource(R.drawable.img_ulsan)
                    "세종" -> ivArea.setImageResource(R.drawable.img_sejong)
                    "경기도" -> ivArea.setImageResource(R.drawable.img_gyeonggi)
                    "강원도" -> ivArea.setImageResource(R.drawable.img_kangwon)
                    "충청북도" -> ivArea.setImageResource(R.drawable.img_chungbuk)
                    "충청남도" -> ivArea.setImageResource(R.drawable.img_chungnam)
                    "경상북도" -> ivArea.setImageResource(R.drawable.img_gyungbuk)
                    "경상남도" -> ivArea.setImageResource(R.drawable.img_gyeongnam)
                    "전라북도" -> ivArea.setImageResource(R.drawable.img_jeonbuk)
                    "전라남도" -> ivArea.setImageResource(R.drawable.img_jeonnam)
                    else -> ivArea.setImageResource(R.drawable.img_jeju)
                }

                root.clicks().onEach {
                    onItemClick(area)
                }.launchIn(scope)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AILocationViewHolder {
        return AILocationViewHolder(
            ItemAreaCardBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: AILocationViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    fun setAreaList(areaList: List<String>) {
        submitList(areaList)
    }
}
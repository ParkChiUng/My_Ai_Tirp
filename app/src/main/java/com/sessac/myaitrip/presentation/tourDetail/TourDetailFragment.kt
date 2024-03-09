package com.sessac.myaitrip.presentation.tourDetail

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import com.bumptech.glide.Glide
import com.sessac.myaitrip.data.tour.TourItem
import com.sessac.myaitrip.databinding.FragmentTourDetailBinding
import com.sessac.myaitrip.presentation.common.ViewBindingBaseFragment

/**
 * 관광지 상세 페이지
 */
class TourDetailFragment :
    ViewBindingBaseFragment<FragmentTourDetailBinding>(FragmentTourDetailBinding::inflate) {

    private var parcelableTourItem: TourItem? = null
    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        parcelableTourItem = if(Build.VERSION.SDK_INT > Build.VERSION_CODES.TIRAMISU){
            arguments?.getParcelable("tourItem", TourItem::class.java)
        }else{
            arguments?.getParcelable("tourItem")
        }

        parcelableTourItem?.let{
            with(binding){
                Glide.with(ivTour.context)
                    .load(it.firstImage)
                    .into(ivTour)
            }
        }
    }
}
package com.sessac.myaitrip.data.entities

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class TourDetailItem(
    @SerializedName("contentid")
    val contentId: String,
    @SerializedName("contenttypeid")
    val contentTypeId: String? = null,
    @SerializedName("overview")
    val overview: String? = null,
): Parcelable
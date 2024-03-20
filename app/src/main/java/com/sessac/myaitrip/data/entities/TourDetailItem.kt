package com.sessac.myaitrip.data.entities

import com.google.gson.annotations.SerializedName

data class TourDetailItem(
    @SerializedName("contentid")
    val contentId: String,
    @SerializedName("contenttypeid")
    val contentTypeId: String? = null,
    @SerializedName("overview")
    val overview: String? = null,
)